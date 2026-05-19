package com.WardenMine242.siege_of_settlements.upgrades;

import com.WardenMine242.siege_of_settlements.building.buildings.placements.VillagerHousePlacement;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
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
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class VillagerHouseT2 extends ProductionItem {
    public static final String itemName = "Villager House Tier 2";
    public static final ResourceCost cost = ResourceCosts.RESEARCH_VILLAGER_HOUSE_T2;

    public VillagerHouseT2() {
        super(cost, ProdDupeRule.DISALLOW_FOR_BUILDING);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level == null || level.isClientSide) {
                return;
            }
            if (placement instanceof VillagerHousePlacement house && house.getUpgradeLevel() == 0) {
                house.incrementHouseTierAfterProduction();
            }
        };
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    private boolean hasRequiredResearch() {
        return ResearchClient.hasCheat("modifythephasevariance")
                || ResearchClient.hasResearch(ProductionItems.RESEARCH_BIG_HOUSE);
    }

    @Override
    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        List<FormattedCharSequence> tooltip = new ArrayList<>(List.of(
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgrade.t2"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedTime(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.villager_house_t2.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires.research"), Style.EMPTY)
        ));

        return new StartProductionButton(
                itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> ProductionItems.RESEARCH_VILLAGER_HOUSE_T2.itemIsBeingProducedAt(prodBuilding) ||
                        prodBuilding.getUpgradeLevel() > 0,
                () -> prodBuilding instanceof VillagerHousePlacement
                        && prodBuilding.getUpgradeLevel() == 0
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
