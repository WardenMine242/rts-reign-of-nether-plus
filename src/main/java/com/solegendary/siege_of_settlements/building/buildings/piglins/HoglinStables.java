package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
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

public class HoglinStables extends ProductionBuilding {

    public final static String buildingName = "Hoglin Stables";
    public final static String structureName = "hoglin_stables";
    public final static ResourceCost cost = ResourceCosts.HOGLIN_STABLES;

    public HoglinStables() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.CRIMSON_STEM;
        this.icon = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/crimson_stem.png");

        this.canSetRallyPoint = false;

        this.startingBlockTypes.add(Blocks.NETHER_BRICK_FENCE);

        this.explodeChance = 0.2f;

        this.productions.add(ProductionItems.RESEARCH_HOGLIN_CAVALRY, Keybindings.keyQ);
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
            name,
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/crimson_stem.png"),
            hotkey,
            () -> BuildingClientEvents.getBuildingToPlace() == Buildings.HOGLIN_STABLES,
            () -> false,
            () -> BuildingClientEvents.hasFinishedBuilding(Buildings.PORTAL_BASIC) ||
                    ResearchClient.hasCheat("modifythephasevariance"),
            List.of(
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.hoglin_stables"), Style.EMPTY.withBold(true)),
                ResourceCosts.getFormattedCost(cost),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.hoglin_stables.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.hoglin_stables.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.hoglin_stables.tooltip3"), Style.EMPTY)
            ),
            this
        );
    }
}
