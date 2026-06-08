package com.solegendary.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.buildings.villagers.TownCentre;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TownCentrePlacement extends RangeIndicatorProductionPlacement {
    public static final int MIN_TOWN_CENTRE_TIER = 1;
    public static final int MAX_TOWN_CENTRE_TIER = 2;
    private static final int TIER_2_HEALTH_MULTIPLIER = 2;

    private int townCentreTier = MIN_TOWN_CENTRE_TIER;

    public TownCentrePlacement(Building building,
                               Level level,
                               BlockPos originPos,
                               Rotation rotation,
                               String ownerName,
                               ArrayList<BuildingBlock> blocks,
                               boolean isCapitol,
                               int range,
                               boolean showOnlyWhenSelected,
                               boolean checkUpgraded) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol, range, showOnlyWhenSelected, checkUpgraded);
    }

    public void syncTierFromNetwork(int upgradeLevelIndex) {
        townCentreTier = Mth.clamp(upgradeLevelIndex + 1, MIN_TOWN_CENTRE_TIER, MAX_TOWN_CENTRE_TIER);
    }

    public void incrementTownCentreTierAfterProduction() {
        if (townCentreTier >= MAX_TOWN_CENTRE_TIER) {
            return;
        }
        townCentreTier++;
        applyStructureForCurrentTier();
        repairCurrentStructureToFullHealth();
    }

    public void applyStructureForCurrentTier() {
        changeStructure(townCentreTier >= MAX_TOWN_CENTRE_TIER
                ? TownCentre.upgradedStructureName
                : TownCentre.structureName);
    }

    @Override
    public void changeStructure(String newStructureName) {
        ArrayList<BuildingBlock> previousBlocks = new ArrayList<>(getBlocks());
        super.changeStructure(newStructureName);
        townCentreTier = TownCentre.upgradedStructureName.equals(newStructureName) ? MAX_TOWN_CENTRE_TIER : MIN_TOWN_CENTRE_TIER;

        if (!level.isClientSide) {
            queueStructureUpgradeOnServer(previousBlocks);
        }
        updateButtons();
    }

    @Override
    public int getUpgradeLevel() {
        return townCentreTier - 1;
    }

    @Override
    public int getHealth() {
        return super.getHealth() * getHealthMultiplier();
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() * getHealthMultiplier();
    }

    private int getHealthMultiplier() {
        return townCentreTier >= MAX_TOWN_CENTRE_TIER ? TIER_2_HEALTH_MULTIPLIER : 1;
    }

    private void repairCurrentStructureToFullHealth() {
        if (level.isClientSide) {
            return;
        }
        blockPlaceQueue.clear();
        for (BuildingBlock block : getBlocks()) {
            BlockPos pos = block.getBlockPos();
            BlockState state = block.getBlockState();
            if (!state.isAir() && level.isLoaded(pos)) {
                level.setBlockAndUpdate(pos, state);
            }
        }
        initPlacedBlocks();
        highestBlockCountReached = getBlocksTotal();
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
