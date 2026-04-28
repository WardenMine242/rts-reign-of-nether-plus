package com.solegendary.siege_of_settlements.ability.heroAbilities.royalguard;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.RoyalGuardUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcsIcons;

public class TauntingCry extends Ability {

    public static final int RANGE = 6;
    private static final int CD_MAX_SECONDS = 50 * ResourceCost.TICKS_PER_SECOND;
    public int duration = 4 * ResourceCost.TICKS_PER_SECOND;
    public static final float DAMAGE_MULT = 0.5f;

    public TauntingCry() {
        super(UnitAction.TAUNTING_CRY, CD_MAX_SECONDS, RANGE, 0, false);
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        AbilityButton button = new AbilityButton(
                "Taunting Cry",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/taunting_cry.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.TAUNTING_CRY),
                null,
                getTooltipLines(),
                this,
                unit
        );
        button.stretchIconToBorders = true;
        return button;
    }

    public List<FormattedCharSequence> getTooltipLines() {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.taunting_cry"), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.taunting_cry.stats", CD_MAX_SECONDS / 20, range, 0)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.taunting_cry.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.taunting_cry.tooltip2", duration / 20)),
                fcs(I18n.get("abilities.siege_of_settlements.taunting_cry.tooltip3"))
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((RoyalGuardUnit) unitUsing).getCastTauntingCryGoal().setAbility(this);
        ((RoyalGuardUnit) unitUsing).getCastTauntingCryGoal().startCasting();
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((RoyalGuardUnit) unitUsing).getCastTauntingCryGoal().setAbility(this);
        ((RoyalGuardUnit) unitUsing).getCastTauntingCryGoal().startCasting();
    }
}
