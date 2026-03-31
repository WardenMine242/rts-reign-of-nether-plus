package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.*;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class HauntedHouse extends Building {

    public final static String buildingName = "Haunted House";
    public final static String structureName = "haunted_house";
    public final static ResourceCost cost = ResourceCosts.HAUNTED_HOUSE;

    public HauntedHouse() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.DARK_OAK_LOG;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dark_oak_log.png");

        this.buildTimeModifier = 0.8f;

        this.startingBlockTypes.add(Blocks.SPRUCE_PLANKS);
        this.startingBlockTypes.add(Blocks.DARK_OAK_LOG);
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dark_oak_log.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.HAUNTED_HOUSE,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                    FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.haunted_house"), Style.EMPTY.withBold(true)),
                    ResourceCosts.getFormattedCost(cost),
                    ResourceCosts.getFormattedPop(cost),
                    FormattedCharSequence.forward("", Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.haunted_house.tooltip1"), Style.EMPTY)
            ),
            this
        );
    }
}
