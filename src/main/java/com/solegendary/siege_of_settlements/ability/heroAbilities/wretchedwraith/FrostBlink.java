package com.solegendary.siege_of_settlements.ability.heroAbilities.wretchedwraith;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.Button;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.goals.GenericTargetedSpellGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.monsters.WretchedWraithUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcsIcons;

public class FrostBlink extends HeroAbility {

    public static final int RANGE_RANK_1 = 6;
    public static final int RANGE_RANK_2 = 9;
    public static final int RANGE_RANK_3 = 12;

    public static final int CD_RANK_1 = 20;
    public static final int CD_RANK_2 = 16;
    public static final int CD_RANK_3 = 12;

    public static final int RADIUS = 3;

    public FrostBlink() {
        super(3, 40, UnitAction.FROSTBLINK, CD_RANK_1 * ResourceCost.TICKS_PER_SECOND, RANGE_RANK_1, RADIUS, false);
    }

    @Override
    public boolean isCasting(Unit unit) {
        if (unit instanceof WretchedWraithUnit wraithUnit) {
            GenericTargetedSpellGoal goal = wraithUnit.getCastFrostblinkGoal();
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
    public void updateStatsForRank(HeroUnit hero) {
        if (getRank(hero) == 1) {
            range = RANGE_RANK_1;
            cooldownMax = CD_RANK_1 * ResourceCost.TICKS_PER_SECOND;
        } else if (getRank(hero) == 2) {
            range = RANGE_RANK_2;
            cooldownMax = CD_RANK_2 * ResourceCost.TICKS_PER_SECOND;
        } else if (getRank(hero) == 3) {
            range = RANGE_RANK_3;
            cooldownMax = CD_RANK_3 * ResourceCost.TICKS_PER_SECOND;
        }
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof HeroUnit hero)) return null;
        AbilityButton button = new AbilityButton("Frostblink",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/frostblink.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.FROSTBLINK,
                () -> getRank(hero) == 0,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.FROSTBLINK),
                null,
                getTooltipLines(hero),
                this,
                hero
        );
        button.stretchIconToBorders = true;
        return button;
    }

    @Override
    public Button getRankUpButton(HeroUnit hero) {
        return super.getRankUpButtonProtected(
                "Frostblink",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/frostblink.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.frostblink") + " " + rankString(hero), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.frostblink.stats", cooldownMax / 20, range, manaCost)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.tooltip2"))
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.frostblink"), true),
                fcs(I18n.get("abilities.siege_of_settlements.level_req", getLevelRequirement(hero)), getLevelReqStyle(hero)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.tooltip2")),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.rank1", RANGE_RANK_1, CD_RANK_1), getRank(hero) == 0),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.rank2", RANGE_RANK_2, CD_RANK_2), getRank(hero) == 1),
                fcs(I18n.get("abilities.siege_of_settlements.frostblink.rank3", RANGE_RANK_3, CD_RANK_3), getRank(hero) == 2)
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (level.getWorldBorder().isWithinBounds(targetBp)) {
            ((WretchedWraithUnit) unitUsing).getCastFrostblinkGoal().setAbility(this);
            ((WretchedWraithUnit) unitUsing).getCastFrostblinkGoal().setTarget(targetBp);
        } else if (level.isClientSide()) {
            HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.frostblink.out_of_bounds"), 200);
        }
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        use(level, unitUsing, targetEntity.getOnPos());
    }
}
