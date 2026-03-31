package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.buildings.neutral.NeutralTransportPortal;
import com.solegendary.siege_of_settlements.building.buildings.placements.PortalPlacement;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ConnectPortal extends Ability {

    private static final int CD_MAX = 0;
    private static final int RANGE = 0;

    public ConnectPortal() {
        super(UnitAction.CONNECT_PORTAL, CD_MAX, RANGE, 0, true);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof PortalPlacement)) return null;
        PortalPlacement portal = (PortalPlacement) placement;
        return new AbilityButton("Connect Portal",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/portal.png"),
            hotkey,
            () -> false,
            () -> portal.getPortalType() != PortalPlacement.PortalType.TRANSPORT,
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.CONNECT_PORTAL),
            null,
            List.of(FormattedCharSequence.forward(
                    I18n.get("abilities.siege_of_settlements.connect_portal"),
                    Style.EMPTY.withBold(true)
                ),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.connect_portal.tooltip1"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.connect_portal.tooltip2"), Style.EMPTY)
            ),
            this,
            placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos targetBp) {
        if (buildingUsing instanceof PortalPlacement portalPlacement && portalPlacement.getPortalType() == PortalPlacement.PortalType.TRANSPORT) {
            portalPlacement.disconnectPortal();

            BuildingPlacement targetBuilding = BuildingUtils.findBuilding(level.isClientSide(), targetBp);
            if (targetBuilding instanceof PortalPlacement targetPortal && targetPortal.getPortalType() == PortalPlacement.PortalType.TRANSPORT &&
                targetBuilding != portalPlacement && targetBuilding.isBuilt &&
                (targetBuilding.ownerName.equals(portalPlacement.ownerName) ||
                (targetBuilding.getBuilding() instanceof NeutralTransportPortal &&
                    portalPlacement.getBuilding() instanceof NeutralTransportPortal))) {

                targetPortal.disconnectPortal();
                targetPortal.destination = portalPlacement.centrePos;
                portalPlacement.destination = targetPortal.centrePos;
            } else if (level.isClientSide()) {
                HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.connect_portal.error1"));
            }
        }
    }
}
