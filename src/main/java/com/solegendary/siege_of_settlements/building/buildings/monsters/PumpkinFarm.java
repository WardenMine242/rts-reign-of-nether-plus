package com.solegendary.siege_of_settlements.building.buildings.monsters;

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

public class PumpkinFarm extends AbstractFarm {

    public final static String buildingName = "Pumpkin Farm";
    public final static String structureName = "pumpkin_farm";
    public final static ResourceCost cost = ResourceCosts.PUMPKIN_FARM;

    private static final int ICE_CHECK_TICKS_MAX = 100;
    private int ticksToNextIceCheck = ICE_CHECK_TICKS_MAX;

    public PumpkinFarm() {
        super(structureName, cost, false);
        this.name = buildingName;
        this.portraitBlock = Blocks.PUMPKIN;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/pumpkin_side.png");

        this.startingBlockTypes.add(Blocks.DARK_OAK_LOG);

        this.explodeChance = 0;
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = SiegeOfSettlementsRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
                name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/pumpkin_side.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.PUMPKIN_FARM,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.pumpkin_farm"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.pumpkin_farm.tooltip1", cost.wood, ResourceCosts.REPLANT_WOOD_COST), MyRenderer.iconStyle),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.pumpkin_farm.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.siege_of_settlements.pumpkin_farm.tooltip3"), Style.EMPTY)
                ),
                this
        );
    }
}
