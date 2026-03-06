package net.WardenMine242.reignofnetherplus.building.buildings.placements;

import com.solegendary.reignofnether.building.Building;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.research.ResearchServerEvents;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.ReignOfNether;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;

public class WallPlacement extends BuildingPlacement {
    private static final int FIRE_SECONDS = 4;
    private static final double CHANCE = 1.0;


    private boolean fireEnabled;
    private boolean fireEnhancedEnabled;

    public WallPlacement(Building building,
                         Level level,
                         BlockPos originPos,
                         Rotation rotation,
                         String ownerName,
                         ArrayList<com.solegendary.reignofnether.building.BuildingBlock> blocks,
                         boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
        if (this.blocks == null) this.blocks = new ArrayList<>();
        if (this.blockMap == null) this.blockMap = new java.util.HashMap<>();

        try {
            if (!level.isClientSide()) {
                this.fireEnabled = ResearchServerEvents.playerHasResearch(ownerName, ProductionItems.RESEARCH_FIRE_WALLS);
            } else {
                this.fireEnabled = false;
            }
        } catch (Throwable t) {
            ReignOfNether.LOGGER.warn("[WallPlacement] Ошибка при определении fireEnabled: {}", t.toString());
            this.fireEnabled = false;
        }

        ReignOfNether.LOGGER.info("[WallPlacement] created for owner='{}' fireEnabled={}", ownerName, this.fireEnabled);
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

        ReignOfNether.LOGGER.info(
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

                ReignOfNether.LOGGER.warn("[WallPlacement] ошибка проверки владельца моба: {}", e.toString());
            }
        }
        return false;
    }
}