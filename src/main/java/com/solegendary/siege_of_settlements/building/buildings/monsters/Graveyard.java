package com.solegendary.siege_of_settlements.building.buildings.monsters;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.production.ProductionBuilding;
import com.solegendary.siege_of_settlements.building.production.ProductionItems;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
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

public class Graveyard extends ProductionBuilding {

    public final static String buildingName = "Graveyard";
    public final static String structureName = "graveyard";
    public final static ResourceCost cost = ResourceCosts.GRAVEYARD;

    public Graveyard() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.MOSSY_STONE_BRICKS;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/mossy_stone_bricks.png");

        this.startingBlockTypes.add(Blocks.DEEPSLATE_BRICKS);

        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.ZOMBIE, Keybindings.keyQ);
        this.productions.add(ProductionItems.HUSK, Keybindings.keyQ);
        this.productions.add(ProductionItems.DROWNED, Keybindings.keyW);
        this.productions.add(ProductionItems.SKELETON, Keybindings.keyE);
        this.productions.add(ProductionItems.STRAY, Keybindings.keyE);
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/mossy_stone_bricks.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.GRAVEYARD,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.graveyard"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.graveyard.tooltip1"), Style.EMPTY)
            ),
            this
        );
    }
}
