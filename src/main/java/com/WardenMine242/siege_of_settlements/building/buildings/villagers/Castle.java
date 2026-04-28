package com.WardenMine242.siege_of_settlements.building.buildings.villagers;

import com.WardenMine242.siege_of_settlements.building.buildings.placements.CastlePlacement;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.production.ProductionItemList;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;

public class Castle extends com.solegendary.siege_of_settlements.building.buildings.villagers.Castle {
    public static final String upgradedStructureName = "castle_t2";
    private static final String baseStructureName = "castle";

    public Castle() {
        super();
        this.productions = new ProductionItemList();
        this.productions.add(ProductionItems.ROYAL_GUARD, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_CASTLE_FLAG, Keybindings.keyW);
        this.productions.add(ProductionItems.RAVAGER, Keybindings.keyE);
    }

    @Override
    public ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level) {
        ArrayList<BuildingBlock> blocks = super.getRelativeBlockData(level);
        for (BuildingBlock block : blocks) {
            if (CastlePlacement.isCastleVegetation(block.getBlockState())) {
                block.setBlockState(Blocks.AIR.defaultBlockState());
            }
        }
        return blocks;
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new CastlePlacement(
                this,
                level,
                pos,
                rotation,
                ownerName,
                BuildingUtils.getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation),
                false
        );
    }

    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        for (var block : placement.getBlocks()) {
            if (block.getBlockState().getBlock() == Blocks.IRON_BLOCK) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String getUpgradedStructureName(int upgradeLevel) {
        return upgradeLevel > 0 ? upgradedStructureName : baseStructureName;
    }
}
