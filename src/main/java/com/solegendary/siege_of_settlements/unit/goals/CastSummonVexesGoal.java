package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.ability.AbilityClientboundPacket;
import com.solegendary.siege_of_settlements.ability.abilities.CastSummonVexes;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.unit.UnitAnimationAction;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.packets.UnitAnimationClientboundPacket;
import com.solegendary.siege_of_settlements.unit.units.villagers.EvokerUnit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class CastSummonVexesGoal extends Goal {

    private final LivingEntity mob;
    private CastSummonVexes ability; // used for syncing cooldown with clientside
    private int ticksCasting = 0; // how long have we spent trying to cast this spell
    public boolean isCasting() { return isCasting; }
    public final static int TICKS_CASTING_MAX = 4 * ResourceCost.TICKS_PER_SECOND; // max time required to cast a spell
    private boolean isCasting = false;

    public CastSummonVexesGoal(LivingEntity mob) {
        this.mob = mob;
    }

    public void setAbility(CastSummonVexes ability) {
        this.ability = ability;
    }

    @Override
    public void tick() {
        if (isCasting) {
            ticksCasting += 1;
            if (ticksCasting >= TICKS_CASTING_MAX) {
                if (!this.mob.level().isClientSide())
                    ((EvokerUnit) this.mob).summonVexes();

                if (this.ability != null && !this.mob.level().isClientSide()) {
                    if (!this.mob.level().isClientSide())
                        AbilityClientboundPacket.sendSetCooldownPacket(this.mob.getId(), this.ability.action, this.ability.cooldownMax);
                    else if (mob instanceof Unit unit)
                        this.ability.setToMaxCooldown(unit);
                }
                this.stop();
            }
        }
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void startCasting() {
        this.isCasting = true;
        if (!this.mob.level().isClientSide())
            UnitAnimationClientboundPacket.sendBasicPacket(UnitAnimationAction.NON_KEYFRAME_START, this.mob);
    }

    @Override
    public void stop() {
        this.ticksCasting = 0;
        this.isCasting = false;
        if (!this.mob.level().isClientSide())
            UnitAnimationClientboundPacket.sendBasicPacket(UnitAnimationAction.NON_KEYFRAME_STOP, this.mob);
    }
}
