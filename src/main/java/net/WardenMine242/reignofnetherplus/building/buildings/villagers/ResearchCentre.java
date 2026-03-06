package net.WardenMine242.reignofnetherplus.building.buildings.villagers;

import com.solegendary.reignofnether.api.ReignOfNetherRegistries;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.BuildingPlaceButton;
import com.solegendary.reignofnether.building.Buildings;
import com.solegendary.reignofnether.building.production.ProductionBuilding;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.tutorial.TutorialClientEvents;
import com.solegendary.reignofnether.tutorial.TutorialStage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class ResearchCentre extends ProductionBuilding {
    public final static String buildingName = "ResearchCentre";
    public final static String structureName = "research_centre";
    public final static ResourceCost cost = ResourceCosts.RESEARCH_CENTRE;

    public ResearchCentre() {
        super(structureName, cost, false);


        this.name = buildingName;
        this.portraitBlock = Blocks.BOOKSHELF;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/bookshelf.png");

        this.buildTimeModifier = 0.5f;

        this.startingBlockTypes.add(Blocks.STRIPPED_SPRUCE_LOG);
        this.startingBlockTypes.add(Blocks.STRIPPED_OAK_LOG);
        this.startingBlockTypes.add(Blocks.OAK_LOG);

        this.productions.add(ProductionItems.RESEARCH_DRILL_T2, Keybindings.keyQ);
        this.productions.add(ProductionItems.RESEARCH_DRILL_T3, Keybindings.keyW);
        this.productions.add(ProductionItems.RESEARCH_CASTLE, Keybindings.keyE);
        this.productions.add(ProductionItems.RESEARCH_FIRE_WALLS, Keybindings.keyR);
        this.productions.add(ProductionItems.RESEARCH_FIRE_WALLS2, Keybindings.keyT);
        this.productions.add(ProductionItems.RESEARCH_TOWER_DAMAGE_1, Keybindings.keyY);
        this.productions.add(ProductionItems.RESEARCH_TOWER_DAMAGE_2, Keybindings.keyU);



        this.productions.add(ProductionItems.RESEARCH_BEACON, Keybindings.keyZ);
    }


    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = ReignOfNetherRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/bookshelf.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.RESEARCH_CENTRE,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.reignofnether.research_centre"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.reignofnether.research_centre.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }
}
