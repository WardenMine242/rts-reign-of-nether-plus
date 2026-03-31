package com.solegendary.siege_of_settlements.ability.heroAbilities.piglinmerchant;

//Throws a TNT item which, when it hits the ground, becomes a full block of TNT that explodes after a delay
//Higher levels raise the damage
//Greed is Good reduces the cooldown

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
import com.solegendary.siege_of_settlements.unit.units.piglins.PiglinMerchantUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcsIcons;

public class ThrowTNT extends HeroAbility {

    public static final int RANGE = 12;
    public float explosionPower = 2;

    // for some reason 0 and negative cooldown values cause this to stop working
    public static int LESS_COOLDOWN_PER_100_RESOURCES = 6 * (ResourceCost.TICKS_PER_SECOND + 2);
    public static int MANA_REFUND_PER_100_RESOURCES = 5;

    public ThrowTNT() {
        super(3, 40, UnitAction.THROW_TNT, 20 * ResourceCost.TICKS_PER_SECOND, RANGE, 0, true);
    }

    @Override
    public boolean isCasting(Unit unit) {
        if (unit instanceof PiglinMerchantUnit piglinMerchantUnit) {
            GenericTargetedSpellGoal goal = piglinMerchantUnit.getCastTNTGoal();
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
            explosionPower = 2;
        } else if (getRank(hero) == 2) {
            explosionPower = 3;
        } else if (getRank(hero) == 3) {
            explosionPower = 4;
        }
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof HeroUnit hero)) return null;
        AbilityButton button = new AbilityButton("Throw TNT",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/throw_tnt.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.THROW_TNT,
                () -> getRank(hero) == 0,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.THROW_TNT),
                null,
                getTooltipLines((HeroUnit) hero),
                this,
                hero
        );
        button.stretchIconToBorders = true;
        return button;
    }

    @Override
    public Button getRankUpButton(HeroUnit hero) {
        return super.getRankUpButtonProtected(
                "Throw TNT",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/throw_tnt.png"),
                hero
        );
    }

    public List<FormattedCharSequence> getTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt") + " " + rankString(hero), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.throw_tnt.stats", Math.round(explosionPower * 6.67f), cooldownMax / 20, RANGE, manaCost)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.tooltip2", LESS_COOLDOWN_PER_100_RESOURCES / 20, MANA_REFUND_PER_100_RESOURCES))
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines(HeroUnit hero) {
        return List.of(
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt"), true),
                fcs(I18n.get("abilities.siege_of_settlements.level_req", getLevelRequirement(hero)), getLevelReqStyle(hero)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.tooltip2", LESS_COOLDOWN_PER_100_RESOURCES / 20, MANA_REFUND_PER_100_RESOURCES)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.rank1"), getRank(hero) == 0),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.rank2"), getRank(hero) == 1),
                fcs(I18n.get("abilities.siege_of_settlements.throw_tnt.rank3"), getRank(hero) == 2)
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((PiglinMerchantUnit) unitUsing).getCastTNTGoal().setAbility(this);
        ((PiglinMerchantUnit) unitUsing).getCastTNTGoal().setTarget(targetBp);
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((PiglinMerchantUnit) unitUsing).getCastTNTGoal().setAbility(this);
        ((PiglinMerchantUnit) unitUsing).getCastTNTGoal().setTarget(targetEntity);
    }
}
