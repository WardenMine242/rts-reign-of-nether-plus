package com.WardenMine242.siege_of_settlements.research;


import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ResearchUpgradedDrill extends ProductionItem {

    public final static String itemName = "Research Upgraded Drill";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_UPGRADED_DRILL;

    public ResearchUpgradedDrill() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide()) {
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_UPGRADED_DRILL);
            } else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_UPGRADED_DRILL);
            }
        };
    }

    public String getItemName() {
        return ResearchUpgradedDrill.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(ResearchUpgradedDrill.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> ProductionItems.RESEARCH_UPGRADED_DRILL.itemIsBeingProduced(prodBuilding.ownerName)
                        || ResearchClient.hasResearch(ProductionItems.RESEARCH_UPGRADED_DRILL),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgraded_drill"),
                                Style.EMPTY.withBold(true)
                        ),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgraded_drill.tooltip1"),
                                Style.EMPTY
                        )
                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchUpgradedDrill.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}
