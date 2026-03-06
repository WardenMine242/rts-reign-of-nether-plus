package net.WardenMine242.reignofnetherplus.research;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import com.solegendary.reignofnether.building.production.ProdDupeRule;
import com.solegendary.reignofnether.building.production.ProductionItem;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.building.production.StopProductionButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.building.production.StartProductionButton;
import net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill;
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
                ReignOfNether.LOGGER.info("DrillT2.onComplete: placement=" +
                        (placement == null ? "null" : placement.getClass().getName()) +
                        ", currentLevel=" + currentLevel);

                if (currentLevel < 1) {
                    placement.changeStructure(net.WardenMine242.reignofnetherplus.building.buildings.villagers.VillagerDrill.structureNameT1);
                    ReignOfNether.LOGGER.info("DrillT2.onComplete: upgrade applied.");
                } else {
                    ReignOfNether.LOGGER.info("DrillT2.onComplete: placement already upgraded, skipping.");
                }
            } catch (Throwable t) {
                ReignOfNether.LOGGER.error("DrillT2.onComplete: error applying upgrade", t);
            }
        };
    }

    public String getItemName() {
        return net.WardenMine242.reignofnetherplus.research.DrillT2.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(
                DrillT2.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png"),
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
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
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.upgrade.t2"),
                                Style.EMPTY.withBold(true)
                        ),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.upgrade.drill_tier_2.tooltip1"),
                                Style.EMPTY
                        ),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.upgrade.drill_tier_2.tooltip2"), Style.EMPTY
                        ),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.requires.research"), Style.EMPTY
                        )
                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(DrillT2.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/raw_iron_block.png"),
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}