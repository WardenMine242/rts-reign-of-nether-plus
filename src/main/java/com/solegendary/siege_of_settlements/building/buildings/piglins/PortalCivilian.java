package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class PortalCivilian extends AbstractPortal {

    public final static String buildingName = "Civilian Portal";
    public final static String structureName = "portal_civilian";

    public final static ResourceCost cost = ResourceCosts.CIVILIAN_PORTAL;

    public PortalCivilian() {
        super(structureName, cost);
        this.name = buildingName;
        this.portraitBlock = Blocks.CYAN_GLAZED_TERRACOTTA;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/cyan_glazed_terracotta.png");
        this.canSetRallyPoint = false;
        this.canAcceptResources = true;
        productions.add(ProductionItems.RESEARCH_RESOURCE_CAPACITY, Keybindings.keyQ);
    }

    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        return 1;
    }
}