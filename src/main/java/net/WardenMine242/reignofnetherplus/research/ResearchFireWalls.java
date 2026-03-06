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

public class ResearchFireWalls extends ProductionItem {

    public final static String itemName = "Research Fire Walls";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_FIRE_WALLS;

    public ResearchFireWalls() {
        super(cost, ProdDupeRule.DISALLOW);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (level.isClientSide()) {
                ResearchClient.addResearch(placement.ownerName, ProductionItems.RESEARCH_FIRE_WALLS);
            } else {
                ResearchServerEvents.addResearch(placement.ownerName, ProductionItems.RESEARCH_FIRE_WALLS);
            }
        };
    }

    public String getItemName() {
        return ResearchFireWalls.itemName;
    }

    public StartProductionButton getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new StartProductionButton(ResearchFireWalls.itemName,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/blocks/fire.png"),
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                hotkey,
                () -> ProductionItems.RESEARCH_FIRE_WALLS.itemIsBeingProduced(prodBuilding.ownerName)
                        || ResearchClient.hasResearch(ProductionItems.RESEARCH_FIRE_WALLS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(FormattedCharSequence.forward(I18n.get("research.reignofnether.fire_walls"),
                                Style.EMPTY.withBold(true)
                        ),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedTime(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.fire_walls.tooltip1"),
                                Style.EMPTY
                        ),
                        FormattedCharSequence.forward(I18n.get("research.reignofnether.fire_walls.tooltip2"), Style.EMPTY
                        )

                ),
                this
        );
    }

    public StopProductionButton getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new StopProductionButton(ResearchFireWalls.itemName,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/blocks/fire.png"),
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
                prodBuilding,
                this,
                first
        );
    }
}

