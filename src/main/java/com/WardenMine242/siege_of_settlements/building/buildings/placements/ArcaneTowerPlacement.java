package com.WardenMine242.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.buildings.villagers.ArcaneTower;
import com.solegendary.siege_of_settlements.entities.ArcaneChargeProjectile;
import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.registrars.ParticleRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class ArcaneTowerPlacement extends ProductionPlacement {
    public static final int MIN_ARCANE_TOWER_TIER = 1;
    public static final int MAX_ARCANE_TOWER_TIER = 3;
    private static final int MIN_ATTACK_TIER = 2;
    private static final int ATTACK_INTERVAL_TICKS = 40; // ~2 seconds
    private static final float TIER2_PROJECTILE_DAMAGE = 14.0F;
    private static final float TIER3_PROJECTILE_DAMAGE = 20.0F;
    private static final float TARGET_ACQUIRE_RANGE = 34.0F;
    private static final int CRYSTAL_PARTICLE_INTERVAL_TICKS = 4;
    private static final int MIN_PARTICLE_TIER = 2;
    private static final BlockPos CRYSTAL_OFFSET = new BlockPos(3, 17, 3);
    private static final double TIER3_CRYSTAL_PARTICLE_RADIUS = 4.0D;
    private static final int TIER2_PARTICLE_CHANCE = 2; // 1 in 2 checks
    private static final int TIER3_PARTICLE_CHANCE = 1; // every check (2x more often than T2)

    private int arcaneTowerTier = MIN_ARCANE_TOWER_TIER;

    public ArcaneTowerPlacement(Building building,
                                Level level,
                                BlockPos originPos,
                                Rotation rotation,
                                String ownerName,
                                ArrayList<BuildingBlock> blocks,
                                boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    public void syncTierFromNetwork(int upgradeLevelIndex) {
        arcaneTowerTier = Mth.clamp(upgradeLevelIndex + 1, MIN_ARCANE_TOWER_TIER, MAX_ARCANE_TOWER_TIER);
    }

    public void incrementArcaneTowerTierAfterProduction() {
        if (arcaneTowerTier >= MAX_ARCANE_TOWER_TIER) {
            return;
        }
        arcaneTowerTier++;
        applyStructureForCurrentTier();
    }

    public void applyStructureForCurrentTier() {
        changeStructure(ArcaneTower.getStructureNameForTier(arcaneTowerTier));
    }

    @Override
    public void changeStructure(String newStructureName) {
        ArrayList<BuildingBlock> previousBlocks = new ArrayList<>(getBlocks());
        ArrayList<BuildingBlock> newBlocks = BuildingBlockData.getBuildingBlocksFromNbt(newStructureName, this.getLevel());
        setBlocks(getAbsoluteBlockData(newBlocks, this.getLevel(), originPos, rotation));
        arcaneTowerTier = ArcaneTower.getTierForStructureName(newStructureName);

        // On world load tickAge is still 0; avoid queuing missing blocks, otherwise
        // the tower auto-repairs itself after relog.
        if (!level.isClientSide) {
            if (tickAge > 0) {
                queueStructureUpgradeOnServer(previousBlocks);
            } else {
                blockPlaceQueue.clear();
                initPlacedBlocks();
            }
        } else {
            initPlacedBlocks();
        }
        updateButtons();
    }

    @Override
    public int getUpgradeLevel() {
        return arcaneTowerTier - 1;
    }

    @Override
    public void tick(Level tickLevel) {
        super.tick(tickLevel);
        if (!isBuilt) {
            return;
        }

        if (tickLevel.isClientSide()) {
            handleClientCrystalParticles(tickLevel);
        } else {
            handleServerCrystalAutoFire((ServerLevel) tickLevel);
        }
    }

    private void handleClientCrystalParticles(Level tickLevel) {
        if (tickAge % CRYSTAL_PARTICLE_INTERVAL_TICKS != 0) {
            return;
        }

        if (arcaneTowerTier < MIN_PARTICLE_TIER) {
            return;
        }

        int particleChance = arcaneTowerTier >= MAX_ARCANE_TOWER_TIER
                ? TIER3_PARTICLE_CHANCE
                : TIER2_PARTICLE_CHANCE;
        spawnCrystalParticle(tickLevel, particleChance);
    }

    private void handleServerCrystalAutoFire(ServerLevel serverLevel) {
        if (arcaneTowerTier < MIN_ATTACK_TIER) {
            return;
        }
        if (tickAgeAfterBuilt <= 0 || tickAgeAfterBuilt % ATTACK_INTERVAL_TICKS != 0) {
            return;
        }

        LivingEntity target = findClosestHostileUnitTarget(serverLevel);
        if (target == null) {
            return;
        }

        fireArcaneChargeAtTarget(serverLevel, target);
    }

    private LivingEntity findClosestHostileUnitTarget(ServerLevel serverLevel) {
        Vec3 crystalPos = getCrystalPosVec();
        List<LivingEntity> nearby = MiscUtil.getEntitiesWithinRange(
                new Vector3d(crystalPos.x, crystalPos.y, crystalPos.z),
                TARGET_ACQUIRE_RANGE,
                LivingEntity.class,
                serverLevel
        );

        LivingEntity closestTarget = null;
        double closestDistanceSqr = Double.MAX_VALUE;

        for (LivingEntity entity : nearby) {
            if (!isValidHostileUnitTarget(entity)) {
                continue;
            }
            double distanceSqr = entity.position().distanceToSqr(crystalPos);
            if (distanceSqr < closestDistanceSqr) {
                closestDistanceSqr = distanceSqr;
                closestTarget = entity;
            }
        }
        return closestTarget;
    }

    private boolean isValidHostileUnitTarget(LivingEntity entity) {
        if (!(entity instanceof Unit targetUnit) || !entity.isAlive() || entity.isRemoved()) {
            return false;
        }
        String targetOwner = targetUnit.getOwnerName();
        if (targetOwner == null || targetOwner.isBlank()) {
            return false;
        }
        return !targetOwner.equals(this.ownerName) && !AlliancesServerEvents.isAllied(this.ownerName, targetOwner);
    }

    private void fireArcaneChargeAtTarget(ServerLevel serverLevel, LivingEntity target) {
        Vec3 crystalPos = getCrystalPosVec();
        Vec3 targetPos = target.getEyePosition();
        Vec3 direction = targetPos.subtract(crystalPos);
        if (direction.lengthSqr() < 1.0E-7D) {
            return;
        }

        Vec3 velocity = direction.normalize().scale(ArcaneChargeProjectile.GHAST_CHARGE_SPEED);
        ArcaneChargeProjectile projectile = new ArcaneChargeProjectile(EntityRegistrar.ARCANE_CHARGE_PROJECTILE.get(), serverLevel);
        projectile.setPos(crystalPos.x, crystalPos.y, crystalPos.z);
        projectile.setDeltaMovement(velocity);
        projectile.xPower = 0.0D;
        projectile.yPower = 0.0D;
        projectile.zPower = 0.0D;
        projectile.setProjectileDamage(getProjectileDamageForCurrentTier());
        serverLevel.addFreshEntity(projectile);
    }

    private float getProjectileDamageForCurrentTier() {
        return arcaneTowerTier >= MAX_ARCANE_TOWER_TIER ? TIER3_PROJECTILE_DAMAGE : TIER2_PROJECTILE_DAMAGE;
    }

    private Vec3 getCrystalPosVec() {
        return new Vec3(
                originPos.getX() + CRYSTAL_OFFSET.getX() + 0.5D,
                originPos.getY() + CRYSTAL_OFFSET.getY() + 0.5D,
                originPos.getZ() + CRYSTAL_OFFSET.getZ() + 0.5D
        );
    }

    private void spawnCrystalParticle(Level tickLevel, int chance) {
        if (chance > 1 && tickLevel.random.nextInt(chance) != 0) {
            return;
        }

        double crystalX = originPos.getX() + CRYSTAL_OFFSET.getX() + 0.5D;
        double crystalY = originPos.getY() + CRYSTAL_OFFSET.getY() + 0.5D;
        double crystalZ = originPos.getZ() + CRYSTAL_OFFSET.getZ() + 0.5D;

        // uniform random point inside a sphere
        double theta = tickLevel.random.nextDouble() * Math.PI * 2.0D;
        double cosPhi = tickLevel.random.nextDouble() * 2.0D - 1.0D;
        double sinPhi = Math.sqrt(1.0D - cosPhi * cosPhi);
        double radius = Math.cbrt(tickLevel.random.nextDouble()) * TIER3_CRYSTAL_PARTICLE_RADIUS;

        double x = crystalX + radius * sinPhi * Math.cos(theta);
        double y = crystalY + radius * cosPhi;
        double z = crystalZ + radius * sinPhi * Math.sin(theta);

        tickLevel.addParticle(
                ParticleRegistrar.ARCANE_PARTICLE.get(),
                x,
                y,
                z,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private void queueStructureUpgradeOnServer(ArrayList<BuildingBlock> previousBlocks) {
        Map<BlockPos, BuildingBlock> newBlocksByPos = new HashMap<>();
        for (BuildingBlock block : getBlocks()) {
            newBlocksByPos.put(block.getBlockPos(), block);
        }

        Map<BlockPos, BuildingBlock> oldBlocksByPos = new HashMap<>();
        for (BuildingBlock oldBlock : previousBlocks) {
            oldBlocksByPos.put(oldBlock.getBlockPos(), oldBlock);
        }

        blockPlaceQueue.clear();

        for (BuildingBlock oldBlock : previousBlocks) {
            BlockPos pos = oldBlock.getBlockPos();
            BuildingBlock newBlock = newBlocksByPos.get(pos);
            if (newBlock == null || newBlock.getBlockState().isAir()) {
                if (!level.getBlockState(pos).isAir()) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (BuildingBlock newBlock : getBlocks()) {
            BlockPos pos = newBlock.getBlockPos();
            BlockState newState = newBlock.getBlockState();

            if (newState.isAir()) {
                if (!level.getBlockState(pos).isAir()) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
                continue;
            }

            boolean changedFromOldStructure = !oldBlocksByPos.containsKey(pos)
                    || !oldBlocksByPos.get(pos).getBlockState().equals(newState);
            boolean differsFromWorld = !level.getBlockState(pos).equals(newState);
            if (changedFromOldStructure || differsFromWorld) {
                addToBlockPlaceQueue(newBlock);
            }
        }
        initPlacedBlocks();
    }
}
