package com.solegendary.siege_of_settlements.ability.heroAbilities.shared;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.goals.GenericUntargetedSpellGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.WretchedWraithUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.unit.UnitClientEvents.sendUnitCommand;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class PlaceholderUntargetedAbility extends HeroAbility {

    public PlaceholderUntargetedAbility() {
        super(3, 3, UnitAction.DEBUG2, 3 * ResourceCost.TICKS_PER_SECOND, 3, 0, true);
    }

    @Override
    public boolean isCasting(Unit unit) {
        if (unit instanceof WretchedWraithUnit wretchedWraithUnit) {
            GenericUntargetedSpellGoal goal = wretchedWraithUnit.getCastBlizzardGoal();
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
        return new AbilityButton("Placeholder Untargeted",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/chain_command_block_front.png"),
                hotkey,
                () -> false,
                () -> getRank(hero) <= 0,
                () -> true,
                () -> sendUnitCommand(UnitAction.DEBUG2),
                null,
                getTooltipLines(hero),
                this,
                hero
        );
    }

    @Override
    public Button getRankUpButton(HeroUnit hero) {
        return super.getRankUpButtonProtected(
                "Placeholder Untargeted",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/blocks/chain_command_block_front.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        return List.of(
                fcs("Placeholder Untargeted")
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of();
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((WretchedWraithUnit) unitUsing).getCastBlizzardGoal().setAbility(this);
        ((WretchedWraithUnit) unitUsing).getCastBlizzardGoal().startCasting();
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((WretchedWraithUnit) unitUsing).getCastBlizzardGoal().setAbility(this);
        ((WretchedWraithUnit) unitUsing).getCastBlizzardGoal().startCasting();
    }
}
