package com.WardenMine242.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.building.GarrisonableBuilding;
import com.solegendary.siege_of_settlements.unit.interfaces.AttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.RangedAttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.GhastUnit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.TridentItem;

import java.util.EnumSet;

/**
 * Illusioner-specific bow goal:
 * - no random attack pauses (prevents visual hand flicker on high attack speed)
 * - draw time scales with current attack cooldown (faster AS => faster draw)
 * - preserves projectile power by scaling virtual bow pull time to 20 ticks
 */
public class IllusionerBowAttackGoal<T extends Mob> extends Goal {
    private static final int MIN_WINDUP_TICKS = 3;
    private static final int MAX_WINDUP_TICKS = 20;
    private static final float WINDUP_FROM_COOLDOWN_MULT = 0.8F;
    private static final int GARRISON_BONUS_RANGE_TO_GHASTS = 10;

    private final T mob;
    private int attackCooldown = 0;
    private int seeTime = 0;

    public IllusionerBowAttackGoal(T mob) {
        this.mob = mob;
        setToMaxAttackCooldown();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void tickAttackCooldown() {
        if (this.attackCooldown > ((AttackerUnit) this.mob).getAttackCooldown()) {
            setToMaxAttackCooldown();
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown -= 1;
        }
    }

    public void setToMaxAttackCooldown() {
        this.attackCooldown = (int) ((AttackerUnit) this.mob).getAttackCooldown();
    }

    public void resetCooldown() {
        this.attackCooldown = 0;
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null && isHoldingRangedWeapon();
    }

    private boolean isHoldingBow() {
        return this.mob.isHolding(is -> is.getItem() instanceof BowItem);
    }

    private boolean isHoldingTrident() {
        return this.mob.isHolding(is -> is.getItem() instanceof TridentItem);
    }

    private boolean isHoldingRangedWeapon() {
        return isHoldingBow() || isHoldingTrident();
    }

    @Override
    public boolean canContinueToUse() {
        Entity target = this.mob.getTarget();
        if (target == null || !target.isAlive() || !isHoldingRangedWeapon()) {
            return false;
        }
        return this.canUse() || !isDoneMoving();
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.seeTime = 0;
        this.mob.stopUsingItem();
    }

    @Override
    public void tick() {
        tickAttackCooldown();

        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        this.mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());

        GarrisonableBuilding garr = GarrisonableBuilding.getGarrison((Unit) this.mob);
        GarrisonableBuilding targetGarr = null;
        if (target instanceof Unit unit) {
            targetGarr = GarrisonableBuilding.getGarrison(unit);
        }

        boolean isGarrisoned = garr != null;
        boolean isTargetGarrisoned = targetGarr != null;
        boolean canSeeTarget = this.mob.getSensing().hasLineOfSight(target) || isGarrisoned || isTargetGarrisoned;
        boolean sawBefore = this.seeTime > 0;
        if (canSeeTarget != sawBefore) {
            this.seeTime = 0;
        }
        if (canSeeTarget) {
            this.seeTime++;
        } else {
            this.seeTime--;
        }

        float attackRange = ((AttackerUnit) this.mob).getAttackRange();
        if (!(this.mob instanceof GhastUnit)) {
            if (isGarrisoned) {
                attackRange = garr.getAttackRange();
                if (target instanceof GhastUnit) {
                    attackRange += GARRISON_BONUS_RANGE_TO_GHASTS;
                }
            } else if (isTargetGarrisoned) {
                attackRange += targetGarr.getExternalAttackRangeBonus();
            } else if (target instanceof GhastUnit ghastUnit) {
                attackRange += ghastUnit.getAttackerRangeBonus(this.mob);
            }
        }

        double distToTarget = this.mob.distanceTo(target);
        if (!this.mob.isPassenger()) {
            if ((distToTarget > attackRange - 1 || !canSeeTarget) && !((Unit) this.mob).getHoldPosition()) {
                moveTo(target);
            } else {
                stopMoving();
            }
        }

        if (this.mob.isUsingItem()) {
            if (distToTarget > attackRange || (!canSeeTarget && this.seeTime < -60)) {
                this.mob.stopUsingItem();
                return;
            }

            int usedTicks = this.mob.getTicksUsingItem();
            int requiredWindup = getAttackWindupTicks();
            if (usedTicks >= requiredWindup && this.attackCooldown <= 0) {
                this.mob.stopUsingItem();

                float velocity = 0.0F;
                if (isHoldingBow()) {
                    int scaledUseTicks = Math.max(1, Math.round((usedTicks * 20.0F) / Math.max(1, requiredWindup)));
                    velocity = BowItem.getPowerForTime(scaledUseTicks);
                } else if (isHoldingTrident()) {
                    velocity = 20.0F;
                }

                if (this.mob instanceof RangedAttackerUnit rangedAttackerUnit) {
                    rangedAttackerUnit.performUnitRangedAttack(target, velocity);
                }
                setToMaxAttackCooldown();
            }
            return;
        }

        if (distToTarget <= attackRange && this.attackCooldown <= 0 && this.seeTime >= -60) {
            this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob,
                    item -> item instanceof BowItem || item instanceof TridentItem));
        }
    }

    private int getAttackWindupTicks() {
        float cooldown = ((AttackerUnit) this.mob).getAttackCooldown();
        int scaled = Math.round(cooldown * WINDUP_FROM_COOLDOWN_MULT);
        return Math.max(MIN_WINDUP_TICKS, Math.min(MAX_WINDUP_TICKS, scaled));
    }

    private boolean isDoneMoving() {
        Unit unit = (Unit) this.mob;
        if (unit.getMoveGoal() instanceof com.solegendary.siege_of_settlements.unit.goals.FlyingMoveToTargetGoal flyingMoveGoal) {
            return flyingMoveGoal.isAtDestination();
        }
        return this.mob.getNavigation().isDone();
    }

    private void stopMoving() {
        Unit unit = (Unit) this.mob;
        if (unit.getMoveGoal() instanceof com.solegendary.siege_of_settlements.unit.goals.FlyingMoveToTargetGoal flyingMoveGoal) {
            flyingMoveGoal.stopMoving();
        } else {
            this.mob.getNavigation().stop();
        }
    }

    private void moveTo(LivingEntity target) {
        Unit unit = (Unit) this.mob;
        if (unit.getMoveGoal() instanceof com.solegendary.siege_of_settlements.unit.goals.FlyingMoveToTargetGoal flyingMoveGoal) {
            flyingMoveGoal.setMoveTarget(target.getOnPos());
        } else {
            this.mob.getNavigation().moveTo(target, 1.0F);
        }
    }
}

