package com.solegendary.siege_of_settlements.survival.spawners;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingServerEvents;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.registrars.BlockRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;

import java.util.*;

import static com.solegendary.siege_of_settlements.survival.SurvivalServerEvents.ENEMY_OWNER_NAME;

public class WaveSpawner {

    public static final int MAX_SPAWN_RANGE = 100;
    public static final int MIN_SPAWN_RANGE = 70;
    public static final int SAMPLE_POINTS_PER_BUILDING = 100;
    public static final int MAX_FAILED_BUILDINGS = 10;

    public static final int MIN_VALID_BUILDINGS = 5; // once we sort the buildings by distance to centroid, how many buildings do we pick to spawn around?
    public static final float PERCENT_VALID_BUILDINGS = 0.1f; // % of all buildings added to MIN_VALID_BUILDINGS

    private static final Random random = new Random();

    public static int getModifiedPopCost(Unit unit) {
        return Math.max(1, unit.getCost().population - 1);
    }

    // used to determine how flat an area is
    public static double getYVariance(Level level, BlockPos bp, int radius) {
        ArrayList<BlockPos> bps = new ArrayList<>();

        for (int x = -radius; x < radius; x++)
            for (int z = -radius; z < radius; z++)
                bps.add(MiscUtil.getHighestGroundBlock(level, bp.offset(x, 0, z)));

        int n = bps.size();
        if (n == 0)
            return 0;

        // Calculate mean Y value
        double sumY = 0.0;
        for (BlockPos bp1 : bps)
            sumY += bp1.getY();

        double meanY = sumY / n;

        // Calculate variance
        double variance = 0.0;
        for (BlockPos bp1 : bps)
            variance += Math.pow(bp1.getY() - meanY, 2);

        return variance / n;
    }

    public static void placeIceOrMagma(BlockPos bp, Level level) {

        BlockPos bpLiquid = null;
        for (int i = 0; i < 10; i++) {
            if (!level.getBlockState(bp.offset(0,5-i,0)).getFluidState().isEmpty()) {
                bpLiquid = bp.offset(0,5-i,0);
                break;
            }
        }
        if (bpLiquid == null)
            return;

        BlockState bs = level.getBlockState(bpLiquid);
        BlockState bsToPlace;

        if (bs.getFluidState().is(FluidTags.LAVA))
            bsToPlace = BlockRegistrar.WALKABLE_MAGMA_BLOCK.get().defaultBlockState();
        else if (bs.getFluidState().is(FluidTags.WATER))
            bsToPlace = Blocks.FROSTED_ICE.defaultBlockState();
        else
            return;

        level.setBlockAndUpdate(bpLiquid, bsToPlace);

        List<BlockPos> bps = List.of(bpLiquid.north(), bpLiquid.east(), bpLiquid.south(), bpLiquid.west(),
                bpLiquid.north().east(),
                bpLiquid.south().west(),
                bpLiquid.north().east(),
                bpLiquid.south().west());

        // Frostwalker effect provided in LivingEntityMixin, but it only happens on changing block positions on the ground
        for (BlockPos pos : bps) {
            BlockState bsAdj = level.getBlockState(pos);
            if (!bsAdj.getFluidState().isEmpty() ||
                    (bsAdj instanceof IPlantable plantable && plantable instanceof LiquidBlockContainer))
                level.setBlockAndUpdate(pos, bsToPlace);
        }
    }

    public static List<BlockPos> getValidSpawnPoints(int amount, Level level, boolean allowLiquid, int flatnessRadius) {
        return getValidSpawnPointsForOwner(amount, level, allowLiquid, flatnessRadius, null);
    }

    public static List<BlockPos> getValidSpawnPointsForOwner(int amount, Level level, boolean allowLiquid, int flatnessRadius, String ownerName) {
        List<BlockPos> basePositions = new ArrayList<>();
        for (BuildingPlacement buildingPlacement : BuildingServerEvents.getBuildings()) {
            if (!ENEMY_OWNER_NAME.equals(buildingPlacement.ownerName) && !buildingPlacement.ownerName.isBlank()) {
                if (ownerName == null || ownerName.isBlank() || buildingPlacement.ownerName.equals(ownerName)) {
                    basePositions.add(buildingPlacement.centrePos);
                }
            }
        }

        if (basePositions.isEmpty()) {
            for (net.minecraft.server.level.ServerPlayer player : PlayerServerEvents.players) {
                if (ownerName == null || ownerName.isBlank() || player.getName().getString().equals(ownerName)) {
                    basePositions.add(player.blockPosition());
                }
            }
        }

        Random random = new Random();
        if (basePositions.isEmpty())
            return List.of();

        Vec3 centroid = new Vec3(0,0,0);

        for (BlockPos pos : basePositions) {
            centroid = centroid.add(Vec3.atCenterOf(pos));
        }
        double invBs = 1f / basePositions.size();
        final Vec3 fCentroid = centroid.multiply(new Vec3(invBs, invBs, invBs));

        // calculate all valid points to spawn around based on distance from the centroid
        basePositions.sort(
                Comparator.comparing((BlockPos b) -> b.distToCenterSqr(fCentroid.x, fCentroid.y, fCentroid.z)).reversed()
        );

        int numValidPoints = (int) (MIN_VALID_BUILDINGS + (basePositions.size() * PERCENT_VALID_BUILDINGS));
        List<BlockPos> validPoints = basePositions.subList(0, Math.min(basePositions.size(), numValidPoints));

        int spawnAttemptsThisPoint = 0;
        BlockState spawnBs;
        BlockPos spawnBp;
        double distSqrToNearestBuilding = 999999;
        double distSqrToNearestEnemyBuilding = 999999;
        int failedPoints = 0;
        ArrayList<BlockPos> validSpawns = new ArrayList<>();

        outerloop:
        do {
            do {
                BlockPos basePos = validPoints.get(random.nextInt(validPoints.size()));

                int x = basePos.getX() + random.nextInt(-MAX_SPAWN_RANGE, MAX_SPAWN_RANGE);
                int z = basePos.getZ() + random.nextInt(-MAX_SPAWN_RANGE, MAX_SPAWN_RANGE);
                int y = level.getChunkAt(new BlockPos(x, 0, z)).getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

                spawnBp = MiscUtil.getHighestGroundBlock(level, new BlockPos(x, y, z));
                spawnBs = level.getBlockState(spawnBp);
                spawnAttemptsThisPoint += 1;
                if (spawnAttemptsThisPoint > 100) {
                    //SiegeOfSettlements.LOGGER.warn("Gave up trying to find a suitable spawn!");
                    failedPoints += 1;
                    if (failedPoints > MAX_FAILED_BUILDINGS)
                        break outerloop;
                    else
                        continue;
                }
                Vec3 vec3 = new Vec3(x, y, z);
                BuildingPlacement b = BuildingUtils.findClosestBuilding(false, vec3, (b1) -> !b1.ownerName.equals(ENEMY_OWNER_NAME));
                BuildingPlacement eb = BuildingUtils.findClosestBuilding(false, vec3, (b1) -> b1.ownerName.equals(ENEMY_OWNER_NAME));

                if (b != null)
                    distSqrToNearestBuilding = b.centrePos.distToCenterSqr(vec3);
                if (eb != null)
                    distSqrToNearestEnemyBuilding = eb.centrePos.distToCenterSqr(vec3);

            } while (spawnBs.is(BlockTags.LEAVES)
                    || spawnBs.is(BlockTags.LOGS) || spawnBs.is(BlockTags.PLANKS)
                    || distSqrToNearestBuilding < (MIN_SPAWN_RANGE * MIN_SPAWN_RANGE)
                    || distSqrToNearestEnemyBuilding < (10 * 10)
                    || (!spawnBs.getFluidState().isEmpty() && !allowLiquid)
                    || BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), spawnBp)
                    || BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), spawnBp.above())
                    || (flatnessRadius > 0 && getYVariance(level, spawnBp, flatnessRadius) >= flatnessRadius / 2f));

            validSpawns.add(spawnBp);
            amount -= 1;

        } while(amount > 0);


        Collections.shuffle(validSpawns);

        if (validSpawns.isEmpty())
            PlayerServerEvents.sendMessageToAllPlayers("WARNING: could not find any valid spawn locations!");

        return validSpawns;
    }

    public static BuildingPlacement spawnBuilding(Building building, BlockPos bp) {
        BuildingPlacement placement = BuildingServerEvents.placeBuilding(
                building, bp,
                Rotation.NONE,
                ENEMY_OWNER_NAME,
                new int[] {},
                false,
                false
        );
        if (placement != null)
            placement.selfBuilding = true;
        BuildingUtils.clearBuildingArea(placement);
        return placement;
    }
}
