package com.solegendary.siege_of_settlements.building.buildings.neutral;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.Building;
import com.solegendary.siege_of_settlements.building.BuildingBlock;
import com.solegendary.siege_of_settlements.building.BuildingBlockData;
import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.tutorial.TutorialClientEvents;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class CapturableBeacon extends Beacon {

    public final static String buildingName = "The Beacon";
    public final static String structureName = "beacon_t5";
    public final static ResourceCost cost = ResourceCost.Building(0,0,0,0);

    public CapturableBeacon() {
        super();
        ((Building)this).structureName = structureName;
        ((Building)this).cost = cost;

        this.name = buildingName;
        this.portraitBlock = Blocks.BEACON;
        this.icon = ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/netherite_beacon.png");

        this.buildTimeModifier = 1.0f;

        this.capturable = true;
        this.invulnerable = true;
        this.shouldDestroyOnReset = false;
        this.selfBuilding = true;

        this.startingBlockTypes.add(Blocks.NETHERITE_BLOCK);

        this.explodeChance = 0.2f;
    }

    @Override
    public ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level) {
        return BuildingBlockData.getBuildingBlocksFromNbt(structureName, level);
    }

    @Override
    public Faction getFaction() {
        return Faction.NONE;
    }

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        return new BuildingPlaceButton(
                buildingName,
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/netherite_beacon.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == this,
                TutorialClientEvents::isEnabled,
                () -> {
                    List<BuildingPlacement> list = new ArrayList<>();
                    for (BuildingPlacement b : BuildingClientEvents.getBuildings()) {
                        if (b.getBuilding() instanceof CapturableBeacon) {
                            list.add(b);
                        }
                    }
                    return list.isEmpty();
                },
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.capturable_beacon"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.capturable_beacon.tooltip3"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.capturable_beacon.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.capturable_beacon.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.beacon.tooltip3"), Style.EMPTY)
                ),
                this
        );
    }
}











