package com.solegendary.siege_of_settlements.building.buildings.shared;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.StockpilePlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.researchItems.ResearchResourceCapacity;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import static com.solegendary.siege_of_settlements.building.BuildingUtils.getAbsoluteBlockData;

public abstract class AbstractStockpile extends ProductionBuilding {

    public final static ResourceCost cost = ResourceCosts.STOCKPILE;
    public AbstractStockpile(String structureName) {
        super(structureName, cost, false);
        this.portraitBlock = Blocks.CHEST;
        this.icon = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/chest.png");

        this.canAcceptResources = true;
        this.canSetRallyPoint = false;

        this.productions.add(ProductionItems.RESEARCH_RESOURCE_CAPACITY, Keybindings.keyQ);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new StockpilePlacement(this, level, pos, rotation, ownerName, getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), isCapitol);
    }
}
