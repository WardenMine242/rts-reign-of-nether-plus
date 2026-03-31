package com.solegendary.siege_of_settlements.building.buildings.shared;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.FarmPlacement;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public abstract class AbstractFarm extends Building {
    public AbstractFarm(String structureName, ResourceCost cost, boolean isCapitol) {
        super(structureName, cost, isCapitol);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new FarmPlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), isCapitol);
    }
}
