package com.WardenMine242.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;

import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public class VillagerGatePlacement extends ProductionPlacement {
    private static final int FIRE_SECONDS = 4;
    private static final double CHANCE = 1.0;

    private boolean fireEnabled;
    private boolean fireEnhancedEnabled;

    private boolean isOpen = true;

    public VillagerGatePlacement(Building building,
                                 Level level,
                                 BlockPos originPos,
                                 Rotation rotation,
                                 String ownerName,
                                 ArrayList<BuildingBlock> blocks,
                                 boolean isCapitol) {

        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
        }

    public void toggleGate() {

        if (!(this.level instanceof ServerLevel serverLevel)) return;

        boolean newState = !this.isOpen;

        String newStructureName = newState
                ? "villager_gate_open"
                : "villager_gate_closed";

        for (BuildingBlock block : this.getBlocks()) {
            serverLevel.setBlock(block.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        }

        StructureTemplate template = serverLevel
                .getStructureManager()
                .get(ResourceLocation.fromNamespaceAndPath("siege_of_settlements", newStructureName))
                .orElse(null);

        if (template == null) {
            System.out.println("STRUCTURE NOT FOUND: " + newStructureName);
            return;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(this.rotation);

        template.placeInWorld(
                serverLevel,
                this.originPos,
                this.originPos,
                settings,
                serverLevel.random,
                3
        );

        ArrayList<BuildingBlock> newBlocks =
                BuildingBlockData.getBuildingBlocksFromNbt(newStructureName, serverLevel);

        int minY = Integer.MAX_VALUE;

        for (BuildingBlock b : newBlocks) {
            int y = b.getBlockPos().getY();
            if (y < minY) {
                minY = y;
            }
        }
        BlockPos correctedOrigin = this.originPos.offset(0, -minY, 0);

        this.setBlocks(
                getAbsoluteBlockData(newBlocks, serverLevel, correctedOrigin, this.rotation)
        );

        this.isOpen = newState;
        spawnGateParticles(serverLevel);
    }

    private void spawnGateParticles(ServerLevel serverLevel) {

        int totalWidth = 11;
        int totalDepth = 4;

        int effectWidth = 5;
        int effectDepth = 2;

        int startX = (totalWidth - effectWidth) / 2; // 3
        int startZ = (totalDepth - effectDepth) / 2; // 1

        for (int i = 0; i < 30; i++) {

            int localX = startX + (int) (Math.random() * effectWidth);
            int localZ = startZ + (int) (Math.random() * effectDepth);

            BlockPos rotatedOffset = new BlockPos(localX, 0, localZ)
                    .rotate(this.rotation);

            BlockPos finalPos = this.originPos.offset(
                    rotatedOffset.getX(),
                    0,
                    rotatedOffset.getZ()
            );

            double x = finalPos.getX() + 0.5;
            double y = finalPos.getY() + 1.1;
            double z = finalPos.getZ() + 0.5;

            double angle = Math.random() * Math.PI * 2;
            double speed = 0.25 + Math.random() * 0.15;

            double motionX = Math.cos(angle) * speed;
            double motionZ = Math.sin(angle) * speed;
            double motionY = 0.1 + Math.random() * 0.1;

            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    x, y, z,
                    0,
                    motionX, motionY, motionZ,
                    1.0
            );
        }
    }
    private void placeStructure(String structureName) {

        if (!(this.level instanceof ServerLevel serverLevel)) return;

        StructureTemplate template = serverLevel
                .getStructureManager()
                .get(
                        ResourceLocation.fromNamespaceAndPath(
                                "siege_of_settlements",
                                structureName
                        )
                )
                .orElse(null);

        if (template == null) return;

        if (template == null) return;

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(this.rotation);

        template.placeInWorld(
                serverLevel,
                this.originPos,
                this.originPos,
                settings,
                serverLevel.random,
                3
        );
    }
    public void onAttacked(Entity attacker, int damage) {
        if (!(this.level instanceof ServerLevel)) return;
        if (attacker == null || !(attacker instanceof LivingEntity)) return;

        LivingEntity living = (LivingEntity) attacker;

        boolean hasFire = ResearchServerEvents.playerHasResearch(
                this.ownerName,
                ProductionItems.RESEARCH_FIRE_WALLS
        );

        boolean hasFirePlus = ResearchServerEvents.playerHasResearch(
                this.ownerName,
                ProductionItems.RESEARCH_FIRE_WALLS2
        );

        if (!hasFire && !hasFirePlus) return;

        if (Math.random() > CHANCE) return;
        if (isAlly(living)) return;

        int fireTime = FIRE_SECONDS;

        if (hasFirePlus) {
            fireTime = 6;
        }

        living.setSecondsOnFire(fireTime);

        if (hasFirePlus) {
            living.hurt(
                    this.level.damageSources().inFire(),
                    2.0f
            );
        }

        SiegeOfSettlements.LOGGER.info(
                "[WallPlacement] set {} on fire for {} seconds (firePlus={})",
                living.getName().getString(),
                fireTime,
                hasFirePlus
        );
    }

    @Override
    public void onBlockBreak(ServerLevel level, BlockPos pos, boolean breakBlocks) {
        if (!ResearchServerEvents.playerHasResearch(this.ownerName, ProductionItems.RESEARCH_FIRE_WALLS)) {
            super.onBlockBreak(level, pos, breakBlocks);
            return;
        }

        if (this.lastAttacker != null) {
            onAttacked(this.lastAttacker, 2);
        } else {
            Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (player != null) {
                var mobs = level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(6));
                for (Mob mob : mobs) {
                    try {
                        var method = mob.getClass().getMethod("getOwnerName");
                        Object res = method.invoke(mob);
                        if (res instanceof String owner && owner.equalsIgnoreCase(player.getGameProfile().getName())) {
                            mob.setSecondsOnFire(FIRE_SECONDS);
                        }
                    } catch (Exception ignored) {}
                }
            }
        }

        super.onBlockBreak(level, pos, breakBlocks);
    }

    private boolean isAlly(LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getGameProfile() == null) return false;
            String attackerName = player.getGameProfile().getName();
            if (attackerName == null) return false;
            return attackerName.equalsIgnoreCase(this.ownerName);
        }


        if (entity instanceof Mob mob) {
            try {
                var method = mob.getClass().getMethod("getOwnerName");
                if (method != null) {
                    Object res = method.invoke(mob);
                    if (res instanceof String && ((String) res).equalsIgnoreCase(this.ownerName)) return true;
                }
            } catch (NoSuchMethodException ignored) {

            } catch (Throwable e) {

                SiegeOfSettlements.LOGGER.warn("[WallPlacement] ошибка проверки владельца моба: {}", e.toString());
            }
        }
        return false;
    }
}