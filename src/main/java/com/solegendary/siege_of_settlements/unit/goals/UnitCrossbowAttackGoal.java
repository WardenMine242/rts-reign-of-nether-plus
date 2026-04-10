package com.solegendary.siege_of_settlements.unit.goals;

import com.solegendary.siege_of_settlements.building.BuildingPlacement;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.GarrisonableBuilding;
import com.solegendary.siege_of_settlements.registrars.MobEffectRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.AttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.piglins.GhastUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.PillagerUnit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherUnit;
import net.WardenMine242.siege_of_settlements.unit.units.villagers.HorseUnit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Random;

import static com.solegendary.siege_of_settlements.unit.goals.UnitCrossbowAttackGoal.CrossbowState.*;
// - has an attack cooldown parameter in the constructor
// - has no pathfinding delay
// - stops when the target is dead

public class UnitCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends Goal {
    enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }

    private final Random random = new Random();

    private final T mob;
    private UnitCrossbowAttackGoal.CrossbowState crossbowState = UNCHARGED;
    private int seeTime;
    private int attackCooldown;
    private int attackCooldownMax;
    private int windupTime = random.nextInt(0,6);
    private int eliteArcherChargeTicks = 0;
    private static final int ELITE_ARCHER_EXTRA_WINDUP = 4; // 25 + 4 = 29 ticks

    private static final int GARRISON_BONUS_RANGE_TO_GHASTS = 10;

    public UnitCrossbowAttackGoal(T mob, int attackCooldown) {
        this.mob = mob;
        this.attackCooldownMax = attackCooldown;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public void setToMaxAttackCooldown() {
        this.attackCooldown = this.attackCooldownMax;
    }

    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
    }

    public boolean canContinueToUse() {
        if (!isValidTarget() || !this.isHoldingCrossbow())
            return false;
        if (!this.canUse() && this.mob.getNavigation().isDone())
            return false;

        return true;
    }

    private boolean isValidTarget() {
        return (this.mob.getTarget() != null && this.mob.getTarget().isAlive()) || getBuildingTarget() != null;
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        if (!isEliteArcher()) {
            this.mob.setTarget(null);
        }
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
        }
        this.mob.setChargingCrossbow(false);
        this.crossbowState = UNCHARGED;
        this.seeTime = 0;
        this.attackCooldown = attackCooldownMax;
        windupTime = isEliteArcher() ? ELITE_ARCHER_EXTRA_WINDUP : random.nextInt(0,6);
        this.eliteArcherChargeTicks = 0;
    }

    private BuildingPlacement getBuildingTarget() {
        if (this.mob instanceof PillagerUnit pUnit &&
                pUnit.getAttackBuildingGoal() instanceof RangedAttackBuildingGoal<?> rabg) {
            return rabg.getBuildingTarget();
        }
        return null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tickChargeCrossbow() {
        ItemStack itemstack = this.mob.getItemBySlot(EquipmentSlot.MAINHAND);
        if (this.crossbowState == UNCHARGED) {
            windupTime = isEliteArcher() ? ELITE_ARCHER_EXTRA_WINDUP : random.nextInt(0,6);
            int ticks = CrossbowItem.getChargeDuration(itemstack);
            // Elite archer should not be slowed while moving under player order.
            if (!isEliteArcher() || this.mob.getNavigation().isDone()) {
                this.mob.addEffect(new MobEffectInstance(MobEffectRegistrar.MINOR_MOVEMENT_SLOWDOWN.get(), ticks, 3, true, false));
            }
            this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
            this.crossbowState = CHARGING;
            this.mob.setChargingCrossbow(true);
            this.eliteArcherChargeTicks = 0;
        }
        else if (this.crossbowState == CHARGING) {
            if (isEliteArcher() && !this.mob.getNavigation().isDone()) {
                this.mob.removeEffect(MobEffectRegistrar.MINOR_MOVEMENT_SLOWDOWN.get());
            }
            int requiredChargeTicks = CrossbowItem.getChargeDuration(itemstack) + windupTime;

            int i;
            if (isEliteArcher()) {
                if (!this.mob.isUsingItem()) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                }
                this.eliteArcherChargeTicks += 1;
                i = this.eliteArcherChargeTicks;
            } else {
                i = this.mob.getTicksUsingItem();
            }

            if (i >= requiredChargeTicks) {
                this.mob.releaseUsingItem();
                this.crossbowState = CHARGED;
                this.attackCooldown = isEliteArcher() ? 0 : attackCooldownMax;
                this.mob.setChargingCrossbow(false);
                windupTime = isEliteArcher() ? ELITE_ARCHER_EXTRA_WINDUP : random.nextInt(0,6);
                this.eliteArcherChargeTicks = 0;
            }
        }
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        BuildingPlacement buildTarget = getBuildingTarget();

        if ((target != null && target.isAlive()) || buildTarget != null) {

            GarrisonableBuilding garr = GarrisonableBuilding.getGarrison((Unit) this.mob);
            GarrisonableBuilding targetGarr = null;
            if (target instanceof Unit unit)
                targetGarr = GarrisonableBuilding.getGarrison(unit);

            boolean isGarrisoned = garr != null;
            boolean isTargetGarrisoned = targetGarr != null;
            boolean isInsideAnyBuilding = this.mob instanceof Unit &&
                    BuildingUtils.isPosInsideAnyBuilding(this.mob.level().isClientSide(), this.mob.getOnPos().above());
            // Fallback: if garrison detection fails, still treat units inside buildings like garrisoned
            // for line-of-sight and distance checks.
            boolean treatAsGarrison = isGarrisoned || isInsideAnyBuilding;

            boolean canSeeTarget = true;
            if (target != null)
                canSeeTarget = this.mob.getSensing().hasLineOfSight(target) || treatAsGarrison || isTargetGarrisoned;
            boolean flag = this.seeTime > 0;

            if (canSeeTarget != flag) {
                this.seeTime = 0;
            }
            if (canSeeTarget) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }
            double distToTarget;
            double horizontalDistToTarget;
            if (target != null)
                distToTarget = this.mob.distanceTo(target);
            else
                distToTarget = Math.sqrt(this.mob.distanceToSqr(buildTarget.centrePos.getX(), buildTarget.centrePos.getY(), buildTarget.centrePos.getZ()));

            if (target != null) {
                double dx = target.getX() - this.mob.getX();
                double dz = target.getZ() - this.mob.getZ();
                horizontalDistToTarget = Math.sqrt(dx * dx + dz * dz);
            } else {
                double dx = buildTarget.centrePos.getX() - this.mob.getX();
                double dz = buildTarget.centrePos.getZ() - this.mob.getZ();
                horizontalDistToTarget = Math.sqrt(dx * dx + dz * dz);
            }

            float attackRange = ((AttackerUnit) this.mob).getAttackRange();

            if (isGarrisoned) {
                attackRange = garr.getAttackRange();
                if (target instanceof GhastUnit)
                    attackRange += GARRISON_BONUS_RANGE_TO_GHASTS;
            } else if (treatAsGarrison) {
                // Watchtower-like fallback range when hard garrison lookup fails.
                attackRange = Math.max(attackRange, 24.0f);
            }
            else if (isTargetGarrisoned)
                attackRange += targetGarr.getExternalAttackRangeBonus();
            else if (target instanceof GhastUnit ghastUnit)
                attackRange += ghastUnit.getAttackerRangeBonus(this.mob);

            // Garrisoned ranged units should use horizontal distance for firing checks.
            // Vertical offset inside towers can otherwise block shots at valid targets.
            double firingDist = treatAsGarrison ? horizontalDistToTarget : distToTarget;
            boolean inFiringRange = firingDist <= attackRange + 0.25f;
            alignHorseWithTarget(target, buildTarget, canSeeTarget, inFiringRange);

            // Elite Archer behaves like a bow user: he starts drawing only when actually attacking,
            // not pre-charging long before a shot.
            if (isEliteArcher()) {
                if (canSeeTarget && inFiringRange) {
                    this.tickChargeCrossbow();
                } else if (this.crossbowState == CHARGING) {
                    if (this.mob.isUsingItem()) {
                        this.mob.stopUsingItem();
                    }
                    this.mob.setChargingCrossbow(false);
                    this.crossbowState = UNCHARGED;
                    this.eliteArcherChargeTicks = 0;
                }
            }

            // dont consider garrison range here so the unit still moves towards the edge of the building
            if (!this.mob.isPassenger()) {
                if ((distToTarget > attackRange || !canSeeTarget) &&
                        !((Unit) this.mob).getHoldPosition()) {
                    if (target != null)
                        this.mob.getNavigation().moveTo(target, 1.0f);
                    else
                        this.mob.getNavigation().moveTo(buildTarget.centrePos.getX(), buildTarget.centrePos.getY(), buildTarget.centrePos.getZ(), 1.0f);
                } else {
                    this.mob.getNavigation().stop();
                }
            }

            if (target != null)
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            else
                this.mob.getLookControl().setLookAt(buildTarget.centrePos.getX(), buildTarget.centrePos.getY(), buildTarget.centrePos.getZ(), 30.0F, 30.0F);

            if (this.crossbowState == CHARGED && isEliteArcher() && canSeeTarget && inFiringRange) {
                this.mob.performCrossbowAttack(this.mob, 1.6F);
                ItemStack itemstack1 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                CrossbowItem.setCharged(itemstack1, false);
                this.crossbowState = UNCHARGED;
            }
            else if (this.crossbowState == CHARGED) {
                --this.attackCooldown;
                if (this.attackCooldown <= 0) {
                    this.crossbowState = READY_TO_ATTACK;
                }
            } else if (this.crossbowState == READY_TO_ATTACK && canSeeTarget && inFiringRange) {
                this.mob.performCrossbowAttack(this.mob, 1.6F);
                ItemStack itemstack1 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                CrossbowItem.setCharged(itemstack1, false);
                this.crossbowState = UNCHARGED;
            }
        }
    }

    private boolean canRun() {
        return this.crossbowState == UNCHARGED;
    }

    private boolean isEliteArcher() {
        return this.mob instanceof EliteArcherUnit;
    }

    private void alignHorseWithTarget(LivingEntity target, BuildingPlacement buildTarget, boolean canSeeTarget, boolean inFiringRange) {
        if (isEliteArcher()) {
            return;
        }
        if (!canSeeTarget || !inFiringRange || !(this.mob.getVehicle() instanceof HorseUnit horse)) {
            return;
        }

        double targetX;
        double targetZ;
        if (target != null) {
            targetX = target.getX();
            targetZ = target.getZ();
        } else if (buildTarget != null) {
            targetX = buildTarget.centrePos.getX();
            targetZ = buildTarget.centrePos.getZ();
        } else {
            return;
        }

        double dx = targetX - horse.getX();
        double dz = targetZ - horse.getZ();
        if (dx * dx + dz * dz < 1.0E-6D) {
            return;
        }

        float yawToTarget = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        horse.setYRot(yawToTarget);
        horse.yRotO = yawToTarget;
        horse.setYBodyRot(yawToTarget);
        horse.setYHeadRot(yawToTarget);
        horse.yBodyRotO = yawToTarget;
        horse.yHeadRotO = yawToTarget;

        this.mob.setYRot(yawToTarget);
        this.mob.yRotO = yawToTarget;
        this.mob.setYBodyRot(yawToTarget);
        this.mob.setYHeadRot(yawToTarget);
        this.mob.yBodyRotO = yawToTarget;
        this.mob.yHeadRotO = yawToTarget;
    }
}
