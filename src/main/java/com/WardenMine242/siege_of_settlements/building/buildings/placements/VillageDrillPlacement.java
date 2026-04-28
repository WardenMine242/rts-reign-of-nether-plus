package com.WardenMine242.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.WardenMine242.siege_of_settlements.building.buildings.villagers.VillagerDrill;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class VillageDrillPlacement extends ProductionPlacement {

    /** 1 = newly built drill, 6 = max. */
    private int drillTier = 1;

    public VillageDrillPlacement(Building building,
                                 Level level,
                                 BlockPos originPos,
                                 Rotation rotation,
                                 String ownerName,
                                 ArrayList<BuildingBlock> blocks,
                                 boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    /**
     * Old saves stored {@code upgradeLevel} 0..2 from block-detection / former logic.
     * Maps to approximate new tiers (structure + income).
     */
    public static int migrateLegacyUpgradeLevelToTier(int legacyUpgradeLevel) {
        return switch (legacyUpgradeLevel) {
            case 1 -> 3;
            case 2 -> 5;
            default -> 1;
        };
    }

    public int getDrillTier() {
        return drillTier;
    }

    /**
     * Called when loading from disk. If {@code savedDrillTier} &gt; 0, it came from {@link com.solegendary.siege_of_settlements.building.BuildingSave#drillTier}.
     */
    public void initFromDisk(int savedDrillTier, int savedUpgradeLevel) {
        if (savedDrillTier > 0) {
            drillTier = Mth.clamp(savedDrillTier, VillagerDrill.MIN_DRILL_TIER, VillagerDrill.MAX_DRILL_TIER);
        } else {
            drillTier = migrateLegacyUpgradeLevelToTier(savedUpgradeLevel);
        }
    }

    /** Client login / packet: {@code upgradeLevelIndex} is {@code drillTier - 1}. */
    public void syncDrillTierFromNetwork(int upgradeLevelIndex) {
        drillTier = Mth.clamp(upgradeLevelIndex + 1, VillagerDrill.MIN_DRILL_TIER, VillagerDrill.MAX_DRILL_TIER);
    }

    public void applyStructureForCurrentTier() {
        changeStructure(VillagerDrill.getStructureNameForTier(drillTier));
    }

    public void incrementDrillTierAfterProduction() {
        if (drillTier >= VillagerDrill.MAX_DRILL_TIER) {
            return;
        }
        drillTier++;
        applyStructureForCurrentTier();
    }

    @Override
    public int getUpgradeLevel() {
        return drillTier - 1;
    }

    @Override
    public String getUpgradedName() {
        if (drillTier <= 1) {
            return super.getUpgradedName();
        }
        return I18n.get("buildings.siege_of_settlements.drill_level." + drillTier);
    }

    @Override
    public boolean canDestroyBlock(BlockPos relativeBp) {
        BlockPos worldBp = relativeBp.offset(this.originPos);
        return this.getLevel().getBlockState(worldBp).getBlock() != Blocks.BEACON
                && this.getLevel().getBlockState(worldBp.above()).getBlock() != Blocks.BEACON;
    }

    @Override
    public void destroy(ServerLevel serverLevel) {
        super.destroy(serverLevel);
        cleanupFloatingBlocks(serverLevel);
    }

    private void cleanupFloatingBlocks(ServerLevel level) {
        BlockPos min = originPos;
        BlockPos max = originPos.offset(8, 9, 8);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block == Blocks.OAK_FENCE
                    || block == Blocks.LANTERN
                    || block == Blocks.IRON_TRAPDOOR) {

                level.destroyBlock(pos, false);
            }
        }
    }
}
