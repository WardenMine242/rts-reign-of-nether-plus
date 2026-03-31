package com.solegendary.siege_of_settlements.research.researchItems;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingServerboundPacket;
import com.solegendary.siege_of_settlements.building.Buildings;
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

public class ResearchSculkAmplifiers extends ProductionItem {

    public final static String itemName = "Sculk Amplifiers";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_SCULK_AMPLIFIERS;

    public final static float SPLIT_BOOM_DAMAGE_MULT = 0.6f;
    public final static int SPLIT_BOOM_RANGE = 15;
    public final static int SPLIT_BOOM_AMOUNT = 3;

    public ResearchSculkAmplifiers() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide())
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_SCULK_AMPLIFIERS);
            else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_SCULK_AMPLIFIERS);
            }
        };
    }

    public String getItemName() {
        return ResearchSculkAmplifiers.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
            ResearchSculkAmplifiers.itemName,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/sculk_shrieker_side.png"),
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            hotkey,
            () -> ProductionItems.RESEARCH_SCULK_AMPLIFIERS.itemIsBeingProduced(prodBuilding.ownerName) ||
                    ResearchClient.hasResearch(ProductionItems.RESEARCH_SCULK_AMPLIFIERS),
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.STRONGHOLD),
            List.of(
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.sculk_amplifiers"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.sculk_amplifiers.tooltip1"), Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.sculk_amplifiers.tooltip2", SPLIT_BOOM_AMOUNT, SPLIT_BOOM_RANGE), Style.EMPTY),
                    FormattedCharSequence.forward("", Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.sculk_amplifiers.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                ResearchSculkAmplifiers.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/sculk_shrieker_side.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}
