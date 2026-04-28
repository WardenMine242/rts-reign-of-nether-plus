package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.interfaces.AttackerUnit;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.PillagerUnit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class ArcherUnit extends PillagerUnit implements GeoEntity {

    public static float STAT_ATTACK_DAMAGE = 4.0F;
    public static float STAT_MOVEMENT_SPEED = 0.26F;

    public static final float STAT_ATTACKS_PER_SECOND = 0.6F;
    public static final float STAT_MAX_HEALTH = 40.0F;
    public static final float STAT_ARMOR = 0.0F;
    public static final float STAT_ATTACK_RANGE = 16.0F;
    public static final float STAT_AGGRO_RANGE = 16.0F;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final double ATTACK_ANIMATION_SPEED = 1.0D / 3.0D;
    private static final int ATTACK_ANIM_TICKS = 63;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean wasUsingItemLastTick = false;
    private int forcedAttackTicks = 0;
    private int attackPoseTicks = -1;
    private boolean resetAttackAnimationPending = false;

    public ArcherUnit(EntityType<? extends Pillager> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, STAT_MOVEMENT_SPEED)
                .add(Attributes.MAX_HEALTH, STAT_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, com.solegendary.siege_of_settlements.unit.interfaces.Unit.getFollowRange())
                .add(Attributes.ARMOR, STAT_ARMOR);
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.ARCHER;
    }

    @Override
    public float getAttackCooldown() {
        return (20.0F / STAT_ATTACKS_PER_SECOND) * getAttackCooldownMultiplier();
    }

    @Override
    public float getAttacksPerSecond() {
        return STAT_ATTACKS_PER_SECOND / getAttackCooldownMultiplier();
    }

    @Override
    public float getBaseAttacksPerSecond() {
        return STAT_ATTACKS_PER_SECOND;
    }

    @Override
    public float getAggroRange() {
        return STAT_AGGRO_RANGE;
    }

    @Override
    public float getAttackRange() {
        return STAT_ATTACK_RANGE;
    }

    @Override
    public float getMovementSpeed() {
        if (this.isPassenger() && this.getVehicle() instanceof Unit mountedUnit) {
            return mountedUnit.getMovementSpeed();
        }
        return STAT_MOVEMENT_SPEED;
    }

    @Override
    public float getUnitAttackDamage() {
        return STAT_ATTACK_DAMAGE + getTowerDamageResearchBonus();
    }

    @Override
    public float getUnitMaxHealth() {
        return STAT_MAX_HEALTH;
    }

    @Override
    public void tick() {
        ensureCrossbowEquippedServer();
        super.tick();

        boolean isUsingItemNow = this.isUsingItem();
        if (isUsingItemNow && !wasUsingItemLastTick) {
            forcedAttackTicks = ATTACK_ANIM_TICKS;
            attackPoseTicks = 0;
            resetAttackAnimationPending = true;
        } else if (isUsingItemNow || this.forcedAttackTicks > 0) {
            attackPoseTicks++;
        } else {
            attackPoseTicks = -1;
        }
        wasUsingItemLastTick = isUsingItemNow;

        if (forcedAttackTicks > 0) {
            forcedAttackTicks--;
        }

    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        ensureCrossbowEquippedServer();
    }

    private void ensureCrossbowEquippedServer() {
        if (this.level().isClientSide()) {
            return;
        }
        ItemStack mainHand = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!(mainHand.getItem() instanceof CrossbowItem)) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "archer_controller", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<ArcherUnit> state) {
        if (state.isMoving()) {
            state.getController().setAnimationSpeed(1.0D);
            state.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.isUsingItem() || this.forcedAttackTicks > 0) {
            if (this.resetAttackAnimationPending) {
                state.getController().forceAnimationReset();
                this.resetAttackAnimationPending = false;
            }
            state.getController().setAnimationSpeed(ATTACK_ANIMATION_SPEED);
            state.setAndContinue(ATTACK_ANIM);
            return PlayState.CONTINUE;
        }

        state.getController().setAnimationSpeed(1.0D);
        state.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public boolean isInArcherAttackPose() {
        return this.isUsingItem() || this.forcedAttackTicks > 0;
    }

    public boolean isMoveOrderActive() {
        if (this.getAttackMoveTarget() != null || this.getFollowTarget() != null) {
            return true;
        }

        boolean hasMoveTarget = this.getMoveGoal() != null && this.getMoveGoal().getMoveTarget() != null;
        boolean hasCombatTarget = this.hasLivingTarget() || AttackerUnit.isAttackingBuilding(this);
        return hasMoveTarget && !hasCombatTarget;
    }

    public boolean shouldUseAttackFacing() {
        return isInArcherAttackPose() && !isMoveOrderActive();
    }

    public boolean shouldRenderArrow2() {
        return this.shouldUseAttackFacing()
                && this.attackPoseTicks >= 8
                && this.attackPoseTicks < 45;
    }

    public boolean canReleaseProjectileNow() {
        return this.attackPoseTicks >= 45;
    }
}
