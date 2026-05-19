package com.WardenMine242.siege_of_settlements.upgrades;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.BlacksmithPlacement;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlacksmithTierUpgrade extends ProductionItem {
    private final String itemName;
    private final ResourceCost resourceCost;
    private final int requiredUpgradeLevelBefore;
    private final ResourceLocation requiredResearch;
    private final Predicate<ProductionPlacement> hideStart;
    private final String titleLangKey;
    private final String tooltip1LangKey;
    private final String tooltip2LangKey;

    public BlacksmithTierUpgrade(
            String itemName,
            ResourceCost resourceCost,
            int requiredUpgradeLevelBefore,
            ResourceLocation requiredResearch,
            Predicate<ProductionPlacement> hideStart,
            String titleLangKey,
            String tooltip1LangKey,
            String tooltip2LangKey
    ) {
        super(resourceCost, ProdDupeRule.DISALLOW_FOR_BUILDING);
        this.itemName = itemName;
        this.resourceCost = resourceCost;
        this.requiredUpgradeLevelBefore = requiredUpgradeLevelBefore;
        this.requiredResearch = requiredResearch;
        this.hideStart = hideStart;
        this.titleLangKey = titleLangKey;
        this.tooltip1LangKey = tooltip1LangKey;
        this.tooltip2LangKey = tooltip2LangKey;

        this.onComplete = (level, placement) -> {
            if (level == null || level.isClientSide) {
                return;
            }
            if (!(placement instanceof BlacksmithPlacement blacksmith)) {
                return;
            }
            if (blacksmith.getUpgradeLevel() != requiredUpgradeLevelBefore) {
                return;
            }
            blacksmith.incrementBlacksmithTierAfterProduction();
        };
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    private boolean hasRequiredResearch() {
        return requiredResearch == null
                || ResearchClient.hasCheat("modifythephasevariance")
                || ResearchClient.hasResearch(requiredResearch);
    }

    @Override
    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        List<FormattedCharSequence> tooltip = new ArrayList<>(List.of(
                FormattedCharSequence.forward(I18n.get(titleLangKey), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(resourceCost),
                ResourceCosts.getFormattedTime(resourceCost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get(tooltip1LangKey), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get(tooltip2LangKey), Style.EMPTY)
        ));
        if (requiredResearch != null) {
            tooltip.add(FormattedCharSequence.forward("", Style.EMPTY));
            tooltip.add(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires.research"), Style.EMPTY));
        }

        return new StartProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> hideStart.test(prodBuilding),
                () -> prodBuilding instanceof BlacksmithPlacement
                        && prodBuilding.getUpgradeLevel() == requiredUpgradeLevelBefore
                        && hasRequiredResearch(),
                tooltip,
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
