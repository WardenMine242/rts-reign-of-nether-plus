package com.solegendary.siege_of_settlements.ability.heroAbilities.royalguard;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.hud.AbilityButton;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.keybinds.Keybinding;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.sounds.SoundAction;
import com.solegendary.siege_of_settlements.sounds.SoundClientboundPacket;
import com.solegendary.siege_of_settlements.unit.UnitAction;
import com.solegendary.siege_of_settlements.unit.goals.GenericUntargetedSpellGoal;
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

public class Avatar extends Ability {

    private static final int CD_MAX_SECONDS = 300 * ResourceCost.TICKS_PER_SECOND;
    public static final int DURATION = 60 * ResourceCost.TICKS_PER_SECOND;
    public static final float ATTACK_SPLASH_RADIUS = 2.5f;
    public static final float ATTACK_SPLASH_MULT = 0.5f;
    public static final float BONUS_HEALTH = 100;

    public Avatar() {
        super(UnitAction.AVATAR, CD_MAX_SECONDS, 0, 0, false);
    }

    @Override
    public boolean isCasting(Unit unit) {
        if (unit instanceof RoyalGuardUnit royalGuardUnit) {
            GenericUntargetedSpellGoal goal = royalGuardUnit.getCastAvatarGoal();
            if (goal != null)
                return goal.isCasting();
        }
        return false;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        AbilityButton button = new AbilityButton(
                "Avatar",
                ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "textures/icons/abilities/avatar.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.AVATAR),
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
                fcs(I18n.get("abilities.siege_of_settlements.avatar"), true),
                fcsIcons(I18n.get("abilities.siege_of_settlements.avatar.stats", CD_MAX_SECONDS / 20, 0)),
                fcs(""),
                fcs(I18n.get("abilities.siege_of_settlements.avatar.tooltip1")),
                fcs(I18n.get("abilities.siege_of_settlements.avatar.tooltip2", BONUS_HEALTH)),
                fcs(I18n.get("abilities.siege_of_settlements.avatar.tooltip3", DURATION / 20))
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        use(level, unitUsing);
    }

    @Override
    public void use(Level level, Unit unitUsing, LivingEntity targetEntity) {
        use(level, unitUsing);
    }

    private void use(Level level, Unit unitUsing) {
        boolean isAvatarActive = ((RoyalGuardUnit) unitUsing).avatarTicksLeft > 0;
        if (level.isClientSide()) {
            if (isAvatarActive) {
                HudClientEvents.showTemporaryMessage(I18n.get("abilities.siege_of_settlements.avatar.already_active"));
            }
        }
        if (!isAvatarActive) {
            ((RoyalGuardUnit) unitUsing).getCastAvatarGoal().setAbility(this);
            ((RoyalGuardUnit) unitUsing).getCastAvatarGoal().startCasting();
            ((RoyalGuardUnit) unitUsing).avatarScalingStarted = true;
            if (!level.isClientSide()) {
                SoundClientboundPacket.playSoundAtPos(SoundAction.BLOODLUST, ((LivingEntity) unitUsing).getOnPos().above());
            }
        }
    }
}
