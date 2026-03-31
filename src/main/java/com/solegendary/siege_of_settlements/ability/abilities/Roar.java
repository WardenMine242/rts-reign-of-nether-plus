package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.RavagerUnit;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class Roar extends Ability {

    private static final int CD_MAX_SECONDS = 20;

    public Roar() {
        super(UnitAction.ROAR, CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND, 0, 0, false, true);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        return new AbilityButton("Roar",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/ravager_roar.png"),
            hotkey,
            () -> false,
            () -> false,
            () -> true,
            () -> UnitClientEvents.sendUnitCommand(UnitAction.ROAR),
            null,
            List.of(
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.roar"), Style.EMPTY),
                FormattedCharSequence.forward(
                    I18n.get("abilities.siege_of_settlements.roar.tooltip1", RavagerUnit.ROAR_DAMAGE, CD_MAX_SECONDS)
                        + RavagerUnit.ROAR_RANGE,
                    MyRenderer.iconStyle
                ),
                FormattedCharSequence.forward("", Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.roar.tooltip2"), Style.EMPTY),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.roar.tooltip3"), Style.EMPTY)
            ),
            this,
            unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((RavagerUnit)unitUsing).resetBehaviours();
        ((RavagerUnit)unitUsing).startToRoar();
        this.setToMaxCooldown(unitUsing);
    }
}
