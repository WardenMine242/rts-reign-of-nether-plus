package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.config.SiegeOfSettlementsCommonConfigs;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.units.villagers.RavagerUnit;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class ArmoredRavagerUnit extends RavagerUnit implements GeoEntity {
    private static final int ATTACK_ANIMATION_TICKS = 10;
    private static final EntityDataAccessor<Integer> ATTACK_ANIMATION_INDEX =
            SynchedEntityData.defineId(ArmoredRavagerUnit.class, EntityDataSerializers.INT);

    public static final float DEFAULT_ATTACK_DAMAGE = 40.0F;
    public static final float DEFAULT_ATTACKS_PER_SECOND = 0.6F;
    public static final float DEFAULT_MAX_HEALTH = 700.0F;
    public static final float DEFAULT_ARMOR = 30.0F;
    public static final float DEFAULT_MOVEMENT_SPEED = 0.25F;
    public static final float DEFAULT_ATTACK_RANGE = 2.0F;
    public static final float DEFAULT_AGGRO_RANGE = 10.0F;
    public static final double DEFAULT_ATTACK_KNOCKBACK = 1.5D;
    public static final double DEFAULT_KNOCKBACK_RESISTANCE = 0.75D;
    private static final double LEGACY_ATTACK_DAMAGE = RavagerUnit.attackDamage;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int attackAnimationTicks = 0;
    private int lastSeenAttackAnimationIndex = 0;

    public ArmoredRavagerUnit(EntityType<? extends Ravager> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, DEFAULT_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, DEFAULT_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, DEFAULT_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, DEFAULT_ARMOR)
                .add(Attributes.ATTACK_KNOCKBACK, DEFAULT_ATTACK_KNOCKBACK)
                .add(Attributes.KNOCKBACK_RESISTANCE, DEFAULT_KNOCKBACK_RESISTANCE)
                .add(Attributes.FOLLOW_RANGE, com.solegendary.siege_of_settlements.unit.interfaces.Unit.getFollowRange());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_ANIMATION_INDEX, 0);
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.RAVAGER;
    }

    @Override
    public float getAttackCooldown() {
        return (20.0F / getConfiguredAttacksPerSecond()) * getAttackCooldownMultiplier();
    }

    @Override
    public float getAttacksPerSecond() {
        return getConfiguredAttacksPerSecond() / getAttackCooldownMultiplier();
    }

    @Override
    public float getBaseAttacksPerSecond() {
        return getConfiguredAttacksPerSecond();
    }

    @Override
    public float getAggroRange() {
        return getConfiguredAggroRange();
    }

    @Override
    public float getAttackRange() {
        return getConfiguredAttackRange();
    }

    @Override
    public float getMovementSpeed() {
        return getConfiguredMovementSpeed();
    }

    @Override
    public float getUnitAttackDamage() {
        return getConfiguredAttackDamage();
    }

    @Override
    public float getUnitMaxHealth() {
        return getConfiguredMaxHealth();
    }

    @Override
    public void tick() {
        super.tick();
        syncConfiguredAttributes();

        int attackIndex = this.entityData.get(ATTACK_ANIMATION_INDEX);
        if (attackIndex != lastSeenAttackAnimationIndex) {
            attackAnimationTicks = ATTACK_ANIMATION_TICKS;
            lastSeenAttackAnimationIndex = attackIndex;
        }
        if (attackAnimationTicks > 0) {
            attackAnimationTicks -= 1;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        applyConfiguredAttribute(Attributes.MOVEMENT_SPEED, getConfiguredMovementSpeed());
    }

    public int getArmoredAttackTick() {
        return attackAnimationTicks;
    }

    public int getArmoredStunnedTick() {
        return 0;
    }

    public int getArmoredRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            int nextAttackIndex = this.entityData.get(ATTACK_ANIMATION_INDEX) + 1;
            this.entityData.set(ATTACK_ANIMATION_INDEX, nextAttackIndex);
            attackAnimationTicks = ATTACK_ANIMATION_TICKS;
            lastSeenAttackAnimationIndex = nextAttackIndex;
        }
        return hurtTargetWithConfiguredDamage(target);
    }

    private boolean hurtTargetWithConfiguredDamage(Entity target) {
        boolean hurt = target.hurt(this.damageSources().mobAttack(this), getUnitAttackDamage());
        if (hurt) {
            double knockback = this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
            if (knockback > 0.0D && target instanceof LivingEntity livingTarget) {
                livingTarget.knockback((float) knockback * 0.5F, Mth.sin(this.getYRot() * 0.017453292F), -Mth.cos(this.getYRot() * 0.017453292F));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }
            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }
        return hurt;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "armored_ravager_controller", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    private static float getConfiguredAttackDamage() {
        try {
            double configured = SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.ATTACK_DAMAGE.get().doubleValue();
            if (Math.abs(configured - LEGACY_ATTACK_DAMAGE) < 1.0E-6D) {
                return DEFAULT_ATTACK_DAMAGE;
            }
            return (float) configured;
        } catch (IllegalStateException ignored) {
            return DEFAULT_ATTACK_DAMAGE;
        }
    }

    private static float getConfiguredMaxHealth() {
        try {
            return (float) SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.MAX_HEALTH.get().doubleValue();
        } catch (IllegalStateException ignored) {
            return DEFAULT_MAX_HEALTH;
        }
    }

    private static float getConfiguredArmor() {
        try {
            return (float) SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.ARMOR.get().doubleValue();
        } catch (IllegalStateException ignored) {
            return DEFAULT_ARMOR;
        }
    }

    private static float getConfiguredMovementSpeed() {
        try {
            return (float) SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.MOVEMENT_SPEED.get().doubleValue();
        } catch (IllegalStateException ignored) {
            return DEFAULT_MOVEMENT_SPEED;
        }
    }

    private static float getConfiguredAttacksPerSecond() {
        try {
            return (float) Math.max(0.01D, SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.ATTACKS_PER_SECOND.get().doubleValue());
        } catch (IllegalStateException ignored) {
            return DEFAULT_ATTACKS_PER_SECOND;
        }
    }

    private static float getConfiguredAttackRange() {
        try {
            return (float) SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.ATTACK_RANGE.get().doubleValue();
        } catch (IllegalStateException ignored) {
            return DEFAULT_ATTACK_RANGE;
        }
    }

    private static float getConfiguredAggroRange() {
        try {
            return (float) SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.AGGRO_RANGE.get().doubleValue();
        } catch (IllegalStateException ignored) {
            return DEFAULT_AGGRO_RANGE;
        }
    }

    private static double getConfiguredAttackKnockback() {
        try {
            return SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.ATTACK_KNOCKBACK.get();
        } catch (IllegalStateException ignored) {
            return DEFAULT_ATTACK_KNOCKBACK;
        }
    }

    private static double getConfiguredKnockbackResistance() {
        try {
            return SiegeOfSettlementsCommonConfigs.ArmoredRavagerStats.KNOCKBACK_RESISTANCE.get();
        } catch (IllegalStateException ignored) {
            return DEFAULT_KNOCKBACK_RESISTANCE;
        }
    }

    private void syncConfiguredAttributes() {
        applyConfiguredAttribute(Attributes.MOVEMENT_SPEED, getConfiguredMovementSpeed());
        applyConfiguredAttribute(Attributes.ATTACK_DAMAGE, getConfiguredAttackDamage());
        applyConfiguredAttribute(Attributes.MAX_HEALTH, getConfiguredMaxHealth());
        applyConfiguredAttribute(Attributes.ARMOR, getConfiguredArmor());
        applyConfiguredAttribute(Attributes.ATTACK_KNOCKBACK, getConfiguredAttackKnockback());
        applyConfiguredAttribute(Attributes.KNOCKBACK_RESISTANCE, getConfiguredKnockbackResistance());

        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void applyConfiguredAttribute(Attribute attribute, double value) {
        if (this.getAttribute(attribute) != null && Math.abs(this.getAttribute(attribute).getBaseValue() - value) > 1.0E-6D) {
            this.getAttribute(attribute).setBaseValue(value);
        }
    }
}
