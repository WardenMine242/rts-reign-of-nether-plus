package com.WardenMine242.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.buildings.villagers.VillagerHouse;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VillagerHousePlacement extends ProductionPlacement {
    public static final int MIN_HOUSE_TIER = 1;
    public static final int MAX_HOUSE_TIER = 2;

    private int houseTier = MIN_HOUSE_TIER;

    public VillagerHousePlacement(Building building,
                                  Level level,
                                  BlockPos originPos,
                                  Rotation rotation,
                                  String ownerName,
                                  ArrayList<BuildingBlock> blocks,
                                  boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    public void syncTierFromNetwork(int upgradeLevelIndex) {
        houseTier = Mth.clamp(upgradeLevelIndex + 1, MIN_HOUSE_TIER, MAX_HOUSE_TIER);
    }

    public void incrementHouseTierAfterProduction() {
        if (houseTier >= MAX_HOUSE_TIER) {
            return;
        }
        houseTier++;
        applyStructureForCurrentTier();
    }

    public void applyStructureForCurrentTier() {
        changeStructure(houseTier >= MAX_HOUSE_TIER
                ? VillagerHouse.upgradedStructureName
                : VillagerHouse.structureName);
    }

    @Override
    public void changeStructure(String newStructureName) {
        ArrayList<BuildingBlock> previousBlocks = new ArrayList<>(getBlocks());
        super.changeStructure(newStructureName);
        houseTier = VillagerHouse.upgradedStructureName.equals(newStructureName) ? MAX_HOUSE_TIER : MIN_HOUSE_TIER;

        if (!level.isClientSide) {
            queueStructureUpgradeOnServer(previousBlocks);
        }
        updateButtons();
    }

    @Override
    public int getUpgradeLevel() {
        return houseTier - 1;
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
