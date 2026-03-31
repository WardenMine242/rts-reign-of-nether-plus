package com.solegendary.siege_of_settlements.building.buildings.shared;

import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.BridgePlacement;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public abstract class AbstractBridge extends Building {
    public final float MELEE_DAMAGE_MULTIPLIER = 0.05f;

    public AbstractBridge(ResourceCost cost) {
        super("", cost, false);
    }

    @Override
    public float getMeleeDamageMult() { return MELEE_DAMAGE_MULTIPLIER; }

    public ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level, boolean diagonal) {
        return BuildingBlockData.getBuildingBlocksFromNbt(diagonal ? getDiagonalStructureName() : getOrthogonalStructureName(), level);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return createBuildingPlacement(level, pos, rotation, ownerName, false);
    }

    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName, boolean diagonal) {
        return new BridgePlacement(this, level, pos, rotation, "", getCulledBlocks(getAbsoluteBlockData(getRelativeBlockData(level, diagonal), level, pos, rotation), level), isCapitol, diagonal);
    }

    public abstract String getDiagonalStructureName();
    public abstract String getOrthogonalStructureName();
}
