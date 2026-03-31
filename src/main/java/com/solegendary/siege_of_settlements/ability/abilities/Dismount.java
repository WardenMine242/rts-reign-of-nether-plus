package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class Dismount extends Ability {
    public Dismount() {
        super(
            UnitAction.DISMOUNT,
            0,
            0,
            0,
            false
        );
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        LivingEntity entity = (LivingEntity) unit;
        return new AbilityButton(
            "Dismount",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/barrier.png"),
            hotkey,
            () -> CursorClientEvents.getLeftClickAction() == UnitAction.DISMOUNT,
            () -> entity.getVehicle() == null,
            () -> true,
            () -> UnitClientEvents.sendUnitCommand(UnitAction.DISMOUNT),
            null,
            List.of(
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.dismount"), Style.EMPTY)
            ),
            this,
            unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((LivingEntity) unitUsing).stopRiding();
    }
}
