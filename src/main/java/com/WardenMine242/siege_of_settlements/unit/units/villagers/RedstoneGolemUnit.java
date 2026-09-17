package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.ability.Abilities;
import com.solegendary.siege_of_settlements.ability.abilities.Minefield;
import com.solegendary.siege_of_settlements.keybinds.Keybindings;
import com.solegendary.siege_of_settlements.registrars.MobEffectRegistrar;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.IronGolemUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static com.solegendary.siege_of_settlements.util.MiscUtil.fcs;

public class RedstoneGolemUnit extends IronGolemUnit implements GeoEntity {
    public static final Abilities ABILITIES = new Abilities();
    static {
        ABILITIES.add(new Minefield(), Keybindings.keyQ);
    }

    public static float STAT_ATTACK_DAMAGE = 60.0F;
    public static float STAT_ATTACKS_PER_SECOND = 0.6F;
    public static float STAT_MAX_HEALTH = 900.0F;
    public static float STAT_ARMOR = 0.0F;
    public static float STAT_MOVEMENT_SPEED = 0.25F; // HUD shows this as 25
    public static float STAT_ATTACK_RANGE = 3.0F;
    public static float STAT_AGGRO_RANGE = 10.0F;
    public static double STAT_ATTACK_KNOCKBACK = 1.5D;
    public static double STAT_KNOCKBACK_RESISTANCE = 1.0D;
    private static final double ATTACK_ANIMATION_SPEED = 0.7D;
    private static final int ATTACK_ANIM_TICKS = 15;
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_LEFT_ANIM = RawAnimation.begin().thenPlay("attack_left");
    private static final RawAnimation ATTACK_RIGHT_ANIM = RawAnimation.begin().thenPlay("attack_right");
    private static final EntityDataAccessor<Integer> ATTACK_SWING_INDEX =
            SynchedEntityData.defineId(RedstoneGolemUnit.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int forcedAttackTicks = 0;
    private boolean resetAttackAnimationPending = false;
    private boolean currentAttackRightHand = false;
    private int lastSeenAttackSwingIndex = 0;
    private Abilities abilities = ABILITIES.clone();

    public RedstoneGolemUnit(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void updateAbilityButtons() {
        abilities = ABILITIES.clone();
    }

    @Override
    public Abilities getAbilities() {
        return abilities;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffectRegistrar.AUROMANCER_AURA.get()) {
            return true;
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_SWING_INDEX, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "redstone_golem_controller", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<RedstoneGolemUnit> state) {
        if (this.forcedAttackTicks > 0) {
            if (this.resetAttackAnimationPending) {
                state.getController().forceAnimationReset();
                this.resetAttackAnimationPending = false;
            }
            state.getController().setAnimationSpeed(ATTACK_ANIMATION_SPEED);
            state.setAndContinue(this.currentAttackRightHand ? ATTACK_RIGHT_ANIM : ATTACK_LEFT_ANIM);
            return PlayState.CONTINUE;
        }
        if (state.isMoving()) {
            double walkSpeedMultiplier = STAT_MOVEMENT_SPEED / IronGolemUnit.movementSpeed;
            state.getController().setAnimationSpeed(walkSpeedMultiplier);
            state.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        }
        state.getController().setAnimationSpeed(1.0D);
        state.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, STAT_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, STAT_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, STAT_ARMOR)
                .add(Attributes.MAX_HEALTH, STAT_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange())
                .add(Attributes.ATTACK_KNOCKBACK, STAT_ATTACK_KNOCKBACK)
                .add(Attributes.KNOCKBACK_RESISTANCE, STAT_KNOCKBACK_RESISTANCE);
    }

    @Override
    public float getMovementSpeed() {
        return STAT_MOVEMENT_SPEED;
    }

    @Override
    public float getAttackCooldown() {
        return (20.0F / STAT_ATTACKS_PER_SECOND) * getAttackCooldownMultiplier();
    }

    @Override
    public float getBaseAttacksPerSecond() {
        return STAT_ATTACKS_PER_SECOND;
    }

    @Override
    public float getUnitAttackDamage() {
        return STAT_ATTACK_DAMAGE;
    }

    @Override
    public float getUnitMaxHealth() {
        return STAT_MAX_HEALTH;
    }

    @Override
    public float getAttackRange() {
        return STAT_ATTACK_RANGE;
    }

    @Override
    public float getAggroRange() {
        return STAT_AGGRO_RANGE;
    }

    @Override
    public float getBuildingDamageMultiplier() {
        return 1.0f;
    }

    @Override
    public List<FormattedCharSequence> getAttackDamageStatTooltip() {
        return List.of(fcs(I18n.get("unitstats.siege_of_settlements.attack_damage"), true));
    }

    @Override
    public boolean hasBonusDamage() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        lockFacingToAttackTarget();

        int swingIndex = this.entityData.get(ATTACK_SWING_INDEX);
        if (swingIndex != this.lastSeenAttackSwingIndex) {
            startAttackAnimationFromSwingIndex(swingIndex);
            this.lastSeenAttackSwingIndex = swingIndex;
        }
        if (this.forcedAttackTicks > 0) {
            this.forcedAttackTicks -= 1;
        }
    }

    private void lockFacingToAttackTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        // Respect explicit move orders: don't hard-lock facing while walking to a commanded location.
        if (this.getMoveGoal() != null && this.getMoveGoal().getMoveTarget() != null) {
            return;
        }

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        if (dx * dx + dz * dz < 1.0E-6D) {
            return;
        }

        float yawToTarget = (float) (Mth.atan2(dz, dx) * 57.2957763671875D) - 90.0F;
        this.setYRot(yawToTarget);
        this.yBodyRot = yawToTarget;
        this.yHeadRot = yawToTarget;
        this.getLookControl().setLookAt(target, 360.0F, 360.0F);
    }

    private void startAttackAnimationFromSwingIndex(int swingIndex) {
        // odd index -> left, even index -> right: guarantees strict hand alternation
        this.currentAttackRightHand = (swingIndex & 1) == 0;
        this.forcedAttackTicks = ATTACK_ANIM_TICKS;
        this.resetAttackAnimationPending = true;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            int nextSwingIndex = this.entityData.get(ATTACK_SWING_INDEX) + 1;
            this.entityData.set(ATTACK_SWING_INDEX, nextSwingIndex);
            startAttackAnimationFromSwingIndex(nextSwingIndex);
            this.lastSeenAttackSwingIndex = nextSwingIndex;
        }
        return hurtTargetWithConfiguredDamage(target);
    }

    private boolean hurtTargetWithConfiguredDamage(Entity target) {
        boolean hurt = target.hurt(this.damageSources().mobAttack(this), getAttributeAttackDamage());
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

    private float getAttributeAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
