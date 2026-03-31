package com.solegendary.siege_of_settlements.building.buildings.villagers;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.shared.AbstractFarm;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.tutorial.TutorialStage;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.WardenMine242.siege_of_settlements.ability.abilities.GiveSteakFromFarm;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class WheatFarm extends AbstractFarm {

    public final static String buildingName = "Wheat Farm";
    public final static String structureName = "wheat_farm";
    public final static ResourceCost cost = ResourceCosts.WHEAT_FARM;

    public WheatFarm() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.HAY_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/hay_block_side.png");

        this.startingBlockTypes.add(Blocks.OAK_LOG);

        this.explodeChance = 0;

        this.abilities.add(new GiveSteakFromFarm(), Keybindings.keyQ);
    }

    public Faction getFaction() {return Faction.VILLAGERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/hay_block_side.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.WHEAT_FARM,
                () -> !TutorialClientEvents.isAtOrPastStage(TutorialStage.EXPLAIN_BUILDINGS),
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.wheat_farm"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.wheat_farm.tooltip1", cost.wood, ResourceCosts.REPLANT_WOOD_COST), MyRenderer.iconStyle),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.wheat_farm.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.villagers.siege_of_settlements.wheat_farm.tooltip3"), Style.EMPTY)
                ),
                this
        );
    }
}
