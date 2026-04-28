package com.WardenMine242.siege_of_settlements.upgrades;

import com.WardenMine242.siege_of_settlements.building.buildings.villagers.Castle;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class CastleT2 extends ProductionItem {
    public static final String itemName = "Castle Tier II";
    public static final ResourceCost cost = ResourceCosts.RESEARCH_CASTLE_FLAG;

    public CastleT2() {
        super(cost, ProdDupeRule.DISALLOW_FOR_BUILDING);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (placement.getBuilding() instanceof Castle) {
                placement.changeStructure(Castle.upgradedStructureName);
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
                () -> ProductionItems.RESEARCH_CASTLE_FLAG.itemIsBeingProducedAt(prodBuilding) ||
                        prodBuilding.getBuilding() instanceof Castle &&
                                prodBuilding.getUpgradeLevel() > 0,
                () -> true,
                List.of(
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgrade.t2"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.castle_t2.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.castle_t2.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires.research"), Style.EMPTY)
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

