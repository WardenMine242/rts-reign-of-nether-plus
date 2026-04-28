package com.solegendary.siege_of_settlements.ability.heroAbilities.royalguard;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.cursor.CursorClientEvents;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.goals.GenericTargetedSpellGoal;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.RoyalGuardUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;
import static com.solegendary.siege_of_settlements.util.MiscUtil.fcsIcons;

public class MaceSlam extends Ability {

    public static final float RADIUS = 3.0f;
    public static final float RANGE = 3;
    public float damage = 10f;
    public int stunDuration = 2 * ResourceCost.TICKS_PER_SECOND;

    public MaceSlam() {
        super(UnitAction.MACE_SLAM, 30 * ResourceCost.TICKS_PER_SECOND, RANGE, 0, true);
    }

    @Override
    public boolean isCasting(Unit unit) {
        if (unit instanceof RoyalGuardUnit royalGuardUnit) {
            GenericTargetedSpellGoal goal = royalGuardUnit.getCastMaceSlamGoal();
            if (goal != null)
                return goal.isCasting();
        }
        return false;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        AbilityButton button = new AbilityButton(
                "Mace Slam",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/mace_slam.png"),
                hotkey,
                () -> CursorClientEvents.getLeftClickAction() == UnitAction.MACE_SLAM,
                () -> false,
                () -> true,
                () -> CursorClientEvents.setLeftClickAction(UnitAction.MACE_SLAM),
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
                fcs(I18n.get("abilities.siege_of_settlements.mace_slam"), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.mace_slam.stats", damage, cooldownMax / 20, 0)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.mace_slam.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.mace_slam.tooltip2", stunDuration / 20))
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        ((RoyalGuardUnit) unitUsing).getCastMaceSlamGoal().setAbility(this);
        ((RoyalGuardUnit) unitUsing).getCastMaceSlamGoal().setTarget(targetEntity);
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        ((RoyalGuardUnit) unitUsing).getCastMaceSlamGoal().setAbility(this);
        ((RoyalGuardUnit) unitUsing).getCastMaceSlamGoal().setTarget(targetBp);
    }
}
