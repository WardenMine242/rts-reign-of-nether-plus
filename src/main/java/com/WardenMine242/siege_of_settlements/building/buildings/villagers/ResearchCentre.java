package com.WardenMine242.siege_of_settlements.building.buildings.villagers;

import com.WardenMine242.siege_of_settlements.ability.abilities.ResearchTreeAbility;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
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

        this.abilities.add(new ResearchTreeAbility(), Keybindings.keyQ);
    }


    public Faction getFaction() {
        return Faction.VILLAGERS;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/bookshelf.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.RESEARCH_CENTRE,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.BLACKSMITH) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.research_centre"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.research_centre.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.research_centre.tooltip2"), Style.EMPTY)
                ),
                this
        );
    }
}
