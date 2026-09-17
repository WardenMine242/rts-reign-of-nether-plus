package com.solegendary.siege_of_settlements.research.researchItems;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.hud.Button;
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

public class ResearchRavagerCavalry extends ProductionItem {

    public final static String itemName = "Ravager Artillery";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_RAVAGER_ARTILLERY;

    public ResearchRavagerCavalry() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide()) {
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_RAVAGER_CAVALRY);
            } else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_RAVAGER_CAVALRY);
            }
        };
    }

    public String getItemName() {
        return ResearchRavagerCavalry.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(ResearchRavagerCavalry.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/ravager.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            hotkey,
            () -> ProductionItems.RESEARCH_RAVAGER_CAVALRY.itemIsBeingProduced(prodBuilding.ownerName)
                || ResearchClient.hasResearch(ProductionItems.RESEARCH_RAVAGER_CAVALRY),
                () -> (ResearchClient.hasResearch(ProductionItems.RESEARCH_CASTLE)) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
            List.of(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.ravager_cavalry"),
                    Style.EMPTY.withBold(true)
                ),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.ravager_cavalry.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.ravager_cavalry.tooltip2"), Style.EMPTY)
            ),
            this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchRavagerCavalry.itemName,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/mobheads/ravager.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            prodBuilding,
            this,
            first
        );
    }
}
