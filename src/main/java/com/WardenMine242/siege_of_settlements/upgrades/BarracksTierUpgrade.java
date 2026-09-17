package com.WardenMine242.siege_of_settlements.upgrades;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.BarracksPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.Predicate;

public class BarracksTierUpgrade extends ProductionItem {
    private final String itemName;
    private final ResourceCost resourceCost;
    private final int requiredUpgradeLevelBefore;
    private final Predicate<ProductionPlacement> hideStart;
    private final String titleLangKey;
    private final String tooltip1LangKey;
    private final String tooltip2LangKey;

    public BarracksTierUpgrade(
            String itemName,
            ResourceCost resourceCost,
            int requiredUpgradeLevelBefore,
            Predicate<ProductionPlacement> hideStart,
            String titleLangKey,
            String tooltip1LangKey,
            String tooltip2LangKey
    ) {
        super(resourceCost, ProdDupeRule.DISALLOW_FOR_BUILDING);
        this.itemName = itemName;
        this.resourceCost = resourceCost;
        this.requiredUpgradeLevelBefore = requiredUpgradeLevelBefore;
        this.hideStart = hideStart;
        this.titleLangKey = titleLangKey;
        this.tooltip1LangKey = tooltip1LangKey;
        this.tooltip2LangKey = tooltip2LangKey;

        this.onComplete = (level, placement) -> {
            if (level == null || level.isClientSide) {
                return;
            }
            if (!(placement instanceof BarracksPlacement barracks)) {
                return;
            }
            if (barracks.getUpgradeLevel() != requiredUpgradeLevelBefore) {
                return;
            }
            barracks.incrementBarracksTierAfterProduction();
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
                () -> hideStart.test(prodBuilding),
                () -> prodBuilding instanceof BarracksPlacement
                        && prodBuilding.getUpgradeLevel() == requiredUpgradeLevelBefore
                        && researchOk(),
                List.of(
                        FormattedCharSequence.forward(I18n.get(titleLangKey), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(resourceCost),
                        ResourceCosts.getFormattedTime(resourceCost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(tooltip1LangKey), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get(tooltip2LangKey), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires.research"), Style.EMPTY)
                ),
                this
        );
    }

    private boolean researchOk() {
        if (ResearchClient.hasCheat("modifythephasevariance")) {
            return true;
        }
        return ResearchClient.hasResearch(ProductionItems.RESEARCH_UPGRADED_BARRACKS);
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
