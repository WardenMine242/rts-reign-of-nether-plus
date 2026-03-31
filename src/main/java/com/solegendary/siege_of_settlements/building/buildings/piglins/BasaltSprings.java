package com.solegendary.siege_of_settlements.building.buildings.piglins;

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

public class BasaltSprings extends ProductionBuilding {

    public final static String buildingName = "Basalt Springs";
    public final static String structureName = "basalt_springs";
    public final static ResourceCost cost = ResourceCosts.BASALT_SPRINGS;

    public BasaltSprings() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.POLISHED_BASALT;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/polished_basalt_top.png");

        this.canSetRallyPoint = false;

        this.startingBlockTypes.add(Blocks.BASALT);
        this.startingBlockTypes.add(Blocks.POLISHED_BLACKSTONE);

        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.RESEARCH_CUBE_MAGMA, Keybindings.keyQ);
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/polished_basalt_top.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.BASALT_SPRINGS,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.BASTION) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.basalt_springs"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.basalt_springs.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.basalt_springs.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.basalt_springs.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }
}
