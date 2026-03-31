package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.ability.Ability;
import com.solegendary.siege_of_settlements.ability.AbilityClientboundPacket;
import com.solegendary.siege_of_settlements.ability.HeroAbility;
import com.solegendary.siege_of_settlements.unit.UnitAnimationAction;
import com.solegendary.siege_of_settlements.unit.interfaces.HeroUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.packets.UnitAnimationClientboundPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class GenericUntargetedSpellGoal extends Goal {

    private final LivingEntity mob;
    private Ability ability; // used for syncing cooldown with clientside
    private int ticksCasting = 0; // how long have we spent trying to cast this spell
    public boolean isCasting() { return isCasting; }
    public final int channelTicksMax;
    private boolean isCasting = false;

    private final Runnable onCast;
    private final UnitAnimationAction startAnimation;
    private final UnitAnimationAction stopAnimation;
    private final UnitAnimationAction castAnimation;

    public GenericUntargetedSpellGoal(LivingEntity mob,
                                      int channelTime,
                                      Runnable onCast,
                                      UnitAnimationAction startAnimation,
                                      UnitAnimationAction stopAnimation,
                                      UnitAnimationAction castAnimation) {
        this.mob = mob;
        this.channelTicksMax = channelTime;
        this.onCast = onCast;
        this.startAnimation = startAnimation;
        this.stopAnimation = stopAnimation;
        this.castAnimation = castAnimation;
    }

    public GenericUntargetedSpellGoal(LivingEntity mob,
                                      int channelTime,
                                      Runnable onCast,
                                      UnitAnimationAction startAnimation,
                                      UnitAnimationAction stopAnimation) {
        this(mob, channelTime, onCast, startAnimation, stopAnimation, null);
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    @Override
    public void tick() {
        if (isCasting) {
            ticksCasting += 1;
            if (ticksCasting >= channelTicksMax) {
                if (!this.mob.level().isClientSide() && castAnimation != null) {
                    UnitAnimationClientboundPacket.sendBasicPacket(castAnimation, this.mob);
                }
                if (onCast != null)
                    onCast.run();
                if (this.ability != null && !this.mob.level().isClientSide()) {
                    if (!this.mob.level().isClientSide()) {
                        AbilityClientboundPacket.sendSetCooldownPacket(this.mob.getId(), this.ability.action, this.ability.cooldownMax);
                        if (mob instanceof HeroUnit heroUnit && this.ability instanceof HeroAbility heroAbility) {
                            heroUnit.setMana(heroUnit.getMana() - heroAbility.manaCost);
                        }
                    }
                    else if (mob instanceof Unit unit) {
                        this.ability.setToMaxCooldown(unit);
                    }
                }
                this.ticksCasting = 0;
                this.isCasting = false;
            }
        }
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void startCasting() {
        this.isCasting = true;
        if (!this.mob.level().isClientSide() && startAnimation != null) {
            UnitAnimationClientboundPacket.sendBasicPacket(startAnimation, this.mob);
        }
    }

    @Override
    public void stop() {
        this.ticksCasting = 0;
        this.isCasting = false;
        if (!this.mob.level().isClientSide() && stopAnimation != null)
            UnitAnimationClientboundPacket.sendBasicPacket(stopAnimation, this.mob);
    }
}
