package com.solegendary.siege_of_settlements.building.buildings.placements;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.buildings.villagers.Barracks;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BarracksPlacement extends ProductionPlacement {
    public static final int MIN_BARRACKS_TIER = 1;
    public static final int MAX_BARRACKS_TIER = 3;

    private int barracksTier = MIN_BARRACKS_TIER;

    public BarracksPlacement(Building building,
                             Level level,
                             BlockPos originPos,
                             Rotation rotation,
                             String ownerName,
                             ArrayList<BuildingBlock> blocks,
                             boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    public int getBarracksTier() {
        return barracksTier;
    }

    public void syncTierFromNetwork(int upgradeLevelIndex) {
        barracksTier = Mth.clamp(upgradeLevelIndex + 1, MIN_BARRACKS_TIER, MAX_BARRACKS_TIER);
    }

    public void incrementBarracksTierAfterProduction() {
        if (barracksTier >= MAX_BARRACKS_TIER) {
            return;
        }
        barracksTier++;
        applyStructureForCurrentTier();
    }

    public void applyStructureForCurrentTier() {
        changeStructure(Barracks.getStructureNameForTier(barracksTier));
    }

    @Override
    public void changeStructure(String newStructureName) {
        ArrayList<BuildingBlock> previousBlocks = new ArrayList<>(getBlocks());
        super.changeStructure(newStructureName);
        barracksTier = Barracks.getTierForStructureName(newStructureName);
        if (!level.isClientSide) {
            queueStructureUpgradeOnServer(previousBlocks);
        }
        updateButtons();
    }

    @Override
    public int getUpgradeLevel() {
        return barracksTier - 1;
    }

    @Override
    public String getUpgradedName() {
        String key = barracksTier >= 3
                ? "buildings.villagers.siege_of_settlements.barracks.tier3"
                : "buildings.villagers.siege_of_settlements.barracks.tier2";
        String translated = I18n.get(key);
        if (translated.equals(key)) {
            return I18n.get("buildings.villagers.siege_of_settlements.barracks") + " T" + barracksTier;
        }
        return translated;
    }

    private void queueStructureUpgradeOnServer(ArrayList<BuildingBlock> previousBlocks) {
        Map<BlockPos, BuildingBlock> newBlocksByPos = new HashMap<>();
        for (BuildingBlock block : getBlocks()) {
            newBlocksByPos.put(block.getBlockPos(), block);
        }

        Map<BlockPos, BuildingBlock> oldBlocksByPos = new HashMap<>();
        for (BuildingBlock block : previousBlocks) {
            oldBlocksByPos.put(block.getBlockPos(), block);
        }

        for (BuildingBlock oldBlock : previousBlocks) {
            BlockPos pos = oldBlock.getBlockPos();
            BuildingBlock newBlock = newBlocksByPos.get(pos);
            if (newBlock == null || newBlock.getBlockState().isAir()) {
                if (!level.getBlockState(pos).isAir()) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }

        blockPlaceQueue.clear();
        for (BuildingBlock newBlock : getBlocks()) {
            if (newBlock.getBlockState().isAir()) {
                continue;
            }
            BlockPos pos = newBlock.getBlockPos();
            boolean changedFromOldStructure = !oldBlocksByPos.containsKey(pos)
                    || !oldBlocksByPos.get(pos).getBlockState().equals(newBlock.getBlockState());
            boolean differsFromWorld = !level.getBlockState(pos).equals(newBlock.getBlockState());
            if (changedFromOldStructure || differsFromWorld) {
                addToBlockPlaceQueue(newBlock);
            }
        }
        initPlacedBlocks();
    }
}
