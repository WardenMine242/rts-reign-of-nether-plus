package com.solegendary.siege_of_settlements.building.buildings.piglins;

import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.Buildings;
import com.solegendary.siege_of_settlements.building.buildings.shared.AbstractFarm;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.research.ResearchClient;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.faction.Faction;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class NetherwartFarm extends AbstractFarm {

    public final static String buildingName = "Netherwart Farm";
    public final static String structureName = "netherwart_farm";
    public final static ResourceCost cost = ResourceCosts.NETHERWART_FARM;

    public NetherwartFarm() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.NETHER_WART_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/nether_wart_stage2.png");

        this.startingBlockTypes.add(Blocks.WARPED_STEM);

        this.explodeChance = 0;
    }

    public Faction getFaction() {return Faction.PIGLINS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/nether_wart_stage2.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.NETHERWART_FARM,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.CENTRAL_PORTAL) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.netherwart_farm"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.netherwart_farm.tooltip1", cost.wood, ResourceCosts.REPLANT_WOOD_COST), MyRenderer.iconStyle),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.netherwart_farm.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.piglins.siege_of_settlements.netherwart_farm.tooltip3"), Style.EMPTY)
                ),
                this
        );
    }
}
