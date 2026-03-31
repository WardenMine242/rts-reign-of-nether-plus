package com.solegendary.siege_of_settlements.ability.heroAbilities.shared;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.goals.GenericTargetedSpellGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.WretchedWraithUnit;
import com.solegendary.siege_of_settlements.unit.units.piglins.PiglinMerchantUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class PlaceholderTargetedAbility extends HeroAbility {

    public PlaceholderTargetedAbility() {
        super(3, 3, UnitAction.DEBUG1, 3 * ResourceCost.TICKS_PER_SECOND, 3, 0, true);
    }

    @Override
    public boolean isCasting(Unit unit) {
        if (unit instanceof WretchedWraithUnit wretchedWraithUnit) {
            GenericTargetedSpellGoal goal = wretchedWraithUnit.getCastFrostblinkGoal();
            if (goal != null)
                return goal.isCasting();
        }
        return false;
    }

    @Override
    public boolean rankUp(HeroUnit hero) {
        if (super.rankUp(hero)) {
            updateStatsForRank(hero);
            return true;
        }
        return false;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof HeroUnit hero)) return null;
        return new AbilityButton("Placeholder Targeted",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/command_block_front.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.DEBUG1,
                () -> getRank(hero) == 0,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.DEBUG1),
                null,
                getTooltipLines(hero),
                this,
                hero
        );
    }

    @Override
    public Button getRankUpButton(HeroUnit hero) {
        return super.getRankUpButtonProtected(
                "Placeholder Targeted",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/command_block_front.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        return List.of(
                fcs("Placeholder targeted")
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of();
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((WretchedWraithUnit) unitUsing).getCastFrostblinkGoal().setAbility(this);
        ((WretchedWraithUnit) unitUsing).getCastFrostblinkGoal().setTarget(targetBp);
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((WretchedWraithUnit) unitUsing).getCastFrostblinkGoal().setAbility(this);
        ((WretchedWraithUnit) unitUsing).getCastFrostblinkGoal().setTarget(targetEntity);
    }
}
