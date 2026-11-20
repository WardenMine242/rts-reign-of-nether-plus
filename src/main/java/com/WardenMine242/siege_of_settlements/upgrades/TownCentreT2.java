package com.WardenMine242.siege_of_settlements.upgrades;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.TownCentrePlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.building.buildings.villagers.TownCentre;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TownCentreT2 extends ProductionItem {
    public static final String itemName = "Town Centre Tier II";
    // Uses its dedicated configurable research cost, defined in SiegeOfSettlementsCommonConfigs.
    public static final ResourceCost cost = ResourceCosts.RESEARCH_TOWN_CENTRE_T2;

    public TownCentreT2() {
        super(cost, ProdDupeRule.DISALLOW_FOR_BUILDING);
        this.onComplete = (level, placement) -> {
            if (level == null || level.isClientSide) {
                return;
            }
            if (placement instanceof TownCentrePlacement townCentre && townCentre.getUpgradeLevel() == 0) {
                townCentre.incrementTownCentreTierAfterProduction();
            }
        };
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> ProductionItems.RESEARCH_TOWN_CENTRE_T2.itemIsBeingProducedAt(prodBuilding) ||
                        !(prodBuilding instanceof TownCentrePlacement) ||
                        prodBuilding.getUpgradeLevel() > 0,
                () -> prodBuilding instanceof TownCentrePlacement
                        && prodBuilding.getBuilding() instanceof TownCentre
                        && prodBuilding.getUpgradeLevel() == 0,
                List.of(
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgrade.t2"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.town_centre_t2.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}


