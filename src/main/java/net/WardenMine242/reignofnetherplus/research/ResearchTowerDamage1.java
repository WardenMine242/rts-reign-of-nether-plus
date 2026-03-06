package net.WardenMine242.reignofnetherplus.research;


import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.Buildings;
import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import com.solegendary.reignofnether.building.production.ProdDupeRule;
import com.solegendary.reignofnether.building.production.ProductionItem;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.building.production.StopProductionButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.research.ResearchServerEvents;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.building.production.StartProductionButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ResearchTowerDamage1 extends ProductionItem {

    public final static String itemName = "Research Tower Damage 1";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_TOWER_DAMAGE1;

    public ResearchTowerDamage1() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide()) {
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_TOWER_DAMAGE_1);
            } else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_TOWER_DAMAGE_1);
            }
        };
    }

    public String getItemName() {
        return ResearchTowerDamage1.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(ResearchTowerDamage1.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/stone_bricks.png"),
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> ProductionItems.RESEARCH_TOWER_DAMAGE_1.itemIsBeingProduced(prodBuilding.ownerName)
                        || ResearchClient.hasResearch(ProductionItems.RESEARCH_TOWER_DAMAGE_1),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(FormattedCharSequence.forward(I18n.get("research.reignofnether.tower_damage_1"),
                                Style.EMPTY.withBold(true)
                        ),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.tower_damage_1.tooltip1"),
                                Style.EMPTY
                        ),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.tower_damage_1.tooltip2"), Style.EMPTY
                        )

                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchTowerDamage1.itemName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/stone_bricks.png"),
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}
