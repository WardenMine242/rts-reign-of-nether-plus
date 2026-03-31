package com.solegendary.siege_of_settlements.building.buildings.neutral;

import com.solegendary.siege_of_settlements.building.BuildingClientEvents;
import com.solegendary.siege_of_settlements.building.BuildingPlaceButton;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.buildings.piglins.PortalTransport;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class NeutralTransportPortal extends PortalTransport {

    public final static String buildingName = "Neutral Transport Portal";
    public final static String structureName = "neutral_transport_portal";

    public final static ResourceCost cost = ResourceCost.Building(0,0,0,0);

    public NeutralTransportPortal() {
        this.name = buildingName;
        this.portraitBlock = Blocks.BLUE_GLAZED_TERRACOTTA;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/blue_glazed_terracotta.png");
        this.startingBlockTypes.add(Blocks.LAPIS_BLOCK);

        this.invulnerable = true;
        this.shouldDestroyOnReset = false;
        this.selfBuilding = true;
    }

    @Override
    public int getUpgradeLevel(BuildingPlacement placement) {
        return 1;
    }

    @Override
    public Faction getFaction() {
        return Faction.NONE;
    }

    @Override
    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        return new BuildingPlaceButton(NeutralTransportPortal.buildingName,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/blue_glazed_terracotta.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == this,
                () -> false,
                () -> true,
                List.of(FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.neutral_transport_portal"),
                                Style.EMPTY.withBold(true)
                        ),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.neutral_transport_portal.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.siege_of_settlements.neutral_transport_portal.tooltip2"), Style.EMPTY)
                ),
                this
        );
    }
}




















