package net.WardenMine242.siege_of_settlements.research;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.buildings.placements.ProductionPlacement;
import com.solegendary.siege_of_settlements.building.production.ProdDupeRule;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.building.production.StopProductionButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.building.production.StartProductionButton;
import net.WardenMine242.siege_of_settlements.building.buildings.villagers.VillagerDrill;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class DrillT2 extends ProductionItem {

    public final static String itemName = "Drill Tier 2";
    public final static ResourceCost cost = ResourceCosts.DRILL_T2;

    public DrillT2() {
        super(cost, ProdDupeRule.DISALLOW_FOR_BUILDING);

        this.onComplete = (level, placement) -> {
            if (level == null) return;
            if (level.isClientSide) return;

            try {
                int currentLevel = placement == null ? 0 : placement.getUpgradeLevel();
                SiegeOfSettlements.LOGGER.info("DrillT2.onComplete: placement=" +
                        (placement == null ? "null" : placement.getClass().getName()) +
                        ", currentLevel=" + currentLevel);

                if (currentLevel < 1) {
                    placement.changeStructure(net.WardenMine242.siege_of_settlements.building.buildings.villagers.VillagerDrill.structureNameT1);
                    SiegeOfSettlements.LOGGER.info("DrillT2.onComplete: upgrade applied.");
                } else {
                    SiegeOfSettlements.LOGGER.info("DrillT2.onComplete: placement already upgraded, skipping.");
                }
            } catch (Throwable t) {
                SiegeOfSettlements.LOGGER.error("DrillT2.onComplete: error applying upgrade", t);
            }
        };
    }

    public String getItemName() {
        return net.WardenMine242.siege_of_settlements.research.DrillT2.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
                DrillT2.itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,

                () -> ProductionItems.DRILL_T2.itemIsBeingProducedAt(prodBuilding)
                        || (prodBuilding.getBuilding() instanceof VillagerDrill
                        && prodBuilding.getUpgradeLevel() > 0),

                () -> prodBuilding.getBuilding() instanceof VillagerDrill
                        && prodBuilding.getUpgradeLevel() == 0
                        && (
                        ResearchClient.hasResearch(ProductionItems.RESEARCH_DRILL_T2)
                                || ResearchClient.hasCheat("modifythephasevariance")
                ),

                List.of(
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgrade.t2"),
                                Style.EMPTY.withBold(true)
                        ),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgrade.drill_tier_2.tooltip1"),
                                Style.EMPTY
                        ),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.upgrade.drill_tier_2.tooltip2"), Style.EMPTY
                        ),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.siege_of_settlements.requires.research"), Style.EMPTY
                        )
                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(DrillT2.itemName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/level_up.png"),
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}
