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

public class ResearchTowerDamage2 extends ProductionItem {

    public final static String itemName = "Research Tower Damage 2";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_TOWER_DAMAGE2;

    public ResearchTowerDamage2() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide()) {
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_TOWER_DAMAGE_2);
            } else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_TOWER_DAMAGE_2);
            }
        };
    }

    public String getItemName() {
        return ResearchTowerDamage2.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(ResearchTowerDamage2.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/stone_bricks.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> ProductionItems.RESEARCH_TOWER_DAMAGE_2.itemIsBeingProduced(prodBuilding.ownerName)
                        || ResearchClient.hasResearch(ProductionItems.RESEARCH_TOWER_DAMAGE_2)
                        || !ResearchClient.hasResearch(ProductionItems.RESEARCH_TOWER_DAMAGE_1),
                () -> ResearchClient.hasResearch(ProductionItems.RESEARCH_TOWER_DAMAGE_1) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.tower_damage_2"),
                                Style.EMPTY.withBold(true)
                        ),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.tower_damage_2.tooltip1"),
                                Style.EMPTY
                        ),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.tower_damage_1.tooltip2"), Style.EMPTY
                        )

                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchTowerDamage2.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/stone_bricks.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}
