package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.registrars.MobEffectRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.Relationship;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.units.villagers.IronGolemUnit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RedstoneMineUnit extends IronGolemUnit {

    public static final ResourceCost COST = ResourceCosts.REDSTONE_MINE;

    private static final float EXPLOSION_POWER = 1.8f;
    private static final float MAX_HEALTH = 30.0f;
    private static final float DISPLAY_ATTACK_DAMAGE = 30.0f;
    private static final float MOVEMENT_SPEED = 0.0f;
    private static final int LIFETIME_TICKS = 30 * ResourceCost.TICKS_PER_SECOND;

    public RedstoneMineUnit(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, DISPLAY_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, 0.0f)
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, 0.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        // Keep goal objects initialized for unit systems, but do not add combat/move AI goals.
        initialiseGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    public void tick() {
        if (!this.isNoAi()) {
            this.setNoAi(true);
        }
        if (!this.level().isClientSide() && this.getEffect(MobEffectRegistrar.UNCONTROLLABLE.get()) == null) {
            this.addEffect(new MobEffectInstance(MobEffectRegistrar.UNCONTROLLABLE.get(), Integer.MAX_VALUE, 0, false, false, false));
        }
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null
                && Math.abs(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - MOVEMENT_SPEED) > 1.0E-6D) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
        }

        super.tick();

        if (!this.level().isClientSide()) {
            if (this.tickCount >= LIFETIME_TICKS) {
                this.discard();
                return;
            }
            checkForHostileTrigger();
        }
    }

    private void checkForHostileTrigger() {
        AABB triggerBox = this.getBoundingBox().inflate(0.05D, 0.15D, 0.05D);
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, triggerBox, this::isHostileTrigger)) {
            this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                    EXPLOSION_POWER, Level.ExplosionInteraction.NONE);
            this.discard();
            break;
        }
    }

    private boolean isHostileTrigger(LivingEntity livingEntity) {
        if (livingEntity == this || !livingEntity.isAlive() || livingEntity.isSpectator()) {
            return false;
        }
        if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }

        Relationship relationship = UnitServerEvents.getUnitToEntityRelationship(this, livingEntity);
        return relationship == Relationship.HOSTILE;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(@NotNull Entity entity) {
        // no-op
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
        // no-op
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return effectInstance.getEffect() == MobEffectRegistrar.UNCONTROLLABLE.get();
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        return false;
    }

    @Override
    public boolean hasEffectWithDuration(MobEffect mobEffect) {
        if (mobEffect == MobEffectRegistrar.UNCONTROLLABLE.get()) {
            // keep the unit uncontrollable without showing warning particles each tick
            return false;
        }
        MobEffectInstance effectInstance = this.getEffect(mobEffect);
        return effectInstance != null && effectInstance.getDuration() > 0;
    }

    @Override
    public boolean getWillRetaliate() {
        return false;
    }

    @Override
    public boolean getAggressiveWhenIdle() {
        return false;
    }

    @Override
    public float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }

    @Override
    public float getUnitAttackDamage() {
        return DISPLAY_ATTACK_DAMAGE;
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return COST;
    }
}
