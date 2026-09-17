package com.solegendary.siege_of_settlements.unit.interfaces;

import com.solegendary.siege_of_settlements.unit.goals.RangedFlyingAttackGroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface RangedAttackerUnit {

    int FOG_REVEAL_TICKS_MAX = 60;

    int getFogRevealDuration();
    void setFogRevealDuration(int duration);

    default RangedFlyingAttackGroundGoal<?> getRangedAttackGroundGoal() { return null; }

    default void performUnitRangedAttack(LivingEntity pTarget, float velocity) {
        Vec3 targetPos = ((Unit)this).getLeadingTargetPosition(pTarget, velocity);
        performUnitRangedAttack(targetPos.x, targetPos.y, targetPos.z, velocity);
    }

    // attack ground
    default void performUnitRangedAttack(double x, double y, double z, float velocity) { }
}
