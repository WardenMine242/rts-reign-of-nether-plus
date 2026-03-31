package com.solegendary.siege_of_settlements.ability.abilities;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.neutral.EndermanUnit;
import com.solegendary.siege_of_settlements.util.MyMath;
import com.solegendary.siege_of_settlements.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class Teleport extends Ability {

    public static final int CD_MAX_SECONDS = 8;
    public static final int RANGE = 10;

    public Teleport() {
        super(UnitAction.TELEPORT, CD_MAX_SECONDS * ResourceCost.TICKS_PER_SECOND, RANGE, 0, false);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        return new AbilityButton("Teleport",
            ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/items/enderpearl.png"),
            hotkey,
            () -> CursorClientEvents.getLeftClickAction() == UnitAction.TELEPORT,
            () -> false,
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.TELEPORT),
            null,
            List.of(
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.teleport"), Style.EMPTY.withBold(true)),
                FormattedCharSequence.forward(
                    I18n.get("abilities.siege_of_settlements.teleport.tooltip1", CD_MAX_SECONDS) + RANGE,
                    MyRenderer.iconStyle
                ),
                FormattedCharSequence.forward(I18n.get("abilities.siege_of_settlements.teleport.tooltip2"), Style.EMPTY)
            ),
            this,
            unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        BlockPos limitedBp = MyMath.getXZRangeLimitedBlockPos(((LivingEntity) unitUsing).getOnPos(), targetBp, range);
        if (level.getWorldBorder().isWithinBounds(targetBp)) {
            ((EndermanUnit) unitUsing).teleport(limitedBp);
            this.setToMaxCooldown(unitUsing);
        }
    }
}
