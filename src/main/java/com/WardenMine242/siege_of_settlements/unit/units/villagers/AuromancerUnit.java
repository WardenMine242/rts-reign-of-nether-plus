package com.WardenMine242.siege_of_settlements.unit.units.villagers;

import com.solegendary.siege_of_settlements.alliance.AlliancesServerEvents;
import com.solegendary.siege_of_settlements.registrars.MobEffectRegistrar;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceCosts;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import com.solegendary.siege_of_settlements.unit.units.villagers.EvokerUnit;
import com.solegendary.siege_of_settlements.unit.units.villagers.VindicatorUnit;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.List;

public class AuromancerUnit extends VindicatorUnit {
    private static final float AURA_RADIUS = 5.0F;
    private static final int AURA_REFRESH_TICKS = 20;
    private static final int AURA_REGEN_INTERVAL_TICKS = 80;
    private static final int AURA_REGEN_DURATION_TICKS = 60;
    private static final int AURA_EFFECT_DURATION_TICKS = 40;
    private static final int AURA_PARTICLE_TICKS = 5;
    private static final int AURA_PARTICLE_COUNT = 5;
    private static final int AURA_PARTICLE_CLUSTER_SIZE = 6;
    private static final double AURA_PARTICLE_CLUSTER_SPREAD = 0.22D;
    private static final double AURA_PARTICLE_SPEED = 0.035D;
    private static final float AURA_RADIUS_SQR = AURA_RADIUS * AURA_RADIUS;

    public AuromancerUnit(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, EvokerUnit.movementSpeed)
                .add(Attributes.ATTACK_DAMAGE, 0.0f)
                .add(Attributes.ARMOR, 0.0f)
                .add(Attributes.MAX_HEALTH, VindicatorUnit.maxHealth)
                .add(Attributes.FOLLOW_RANGE, Unit.getFollowRange());
    }

    @Override
    @Nullable
    public ResourceCost getCost() {
        return ResourceCosts.AUROMANCER;
    }

    @Override
    public float getUnitAttackDamage() {
        return 0.0f;
    }

    @Override
    public float getMovementSpeed() {
        return EvokerUnit.movementSpeed;
    }

    @Override
    public double getUnitPhysicalArmorPercentage() {
        return 0.0d;
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
    public boolean canAttackBuildings() {
        return false;
    }

    @Override
    public void setupEquipmentAndUpgradesServer() {
        // Staff is part of the custom model, so keep hand slot empty.
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        tickAura();
    }

    private void tickAura() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.tickCount % AURA_PARTICLE_TICKS == 0) {
            spawnAuraParticles(serverLevel);
        }

        if (this.tickCount % AURA_REFRESH_TICKS == 0) {
            applyAuraEffectsToNearbyUnits();
        }
    }

    private void spawnAuraParticles(ServerLevel level) {
        if (this.random.nextFloat() > 0.8F) {
            return;
        }

        for (int i = 0; i < AURA_PARTICLE_COUNT; i++) {
            double dx = (this.random.nextDouble() * 2.0D - 1.0D) * AURA_RADIUS;
            double dy = (this.random.nextDouble() * 2.0D - 1.0D) * AURA_RADIUS;
            double dz = (this.random.nextDouble() * 2.0D - 1.0D) * AURA_RADIUS;

            // Keep particles inside a sphere of radius 5 (including Y axis).
            if (dx * dx + dy * dy + dz * dz > AURA_RADIUS_SQR) {
                continue;
            }

            level.sendParticles(
                    ParticleTypes.PORTAL,
                    this.getX() + dx,
                    this.getY() + 1.0D + dy,
                    this.getZ() + dz,
                    AURA_PARTICLE_CLUSTER_SIZE,
                    AURA_PARTICLE_CLUSTER_SPREAD,
                    AURA_PARTICLE_CLUSTER_SPREAD,
                    AURA_PARTICLE_CLUSTER_SPREAD,
                    AURA_PARTICLE_SPEED
            );
        }
    }

    private void applyAuraEffectsToNearbyUnits() {
        String ownerName = getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) {
            return;
        }

        List<LivingEntity> nearby = MiscUtil.getEntitiesWithinRange(
                new Vector3d(this.getX(), this.getY(), this.getZ()),
                AURA_RADIUS,
                LivingEntity.class,
                this.level()
        );

        boolean applyRegenNow = (this.tickCount % AURA_REGEN_INTERVAL_TICKS) == 0;
        for (LivingEntity livingEntity : nearby) {
            if (livingEntity == this) {
                continue;
            }
            if (!(livingEntity instanceof Unit unit)) {
                continue;
            }

            String unitOwner = unit.getOwnerName();
            if (unitOwner == null || unitOwner.isEmpty()) {
                continue;
            }

            if (!unitOwner.equals(ownerName) && !AlliancesServerEvents.isAllied(ownerName, unitOwner)) {
                continue;
            }

            // One aura effect instance, so buffs from multiple Auromancers do not stack.
            livingEntity.addEffect(new MobEffectInstance(
                    MobEffectRegistrar.AUROMANCER_AURA.get(),
                    AURA_EFFECT_DURATION_TICKS,
                    0,
                    true,
                    false,
                    true
            ));

            if (applyRegenNow) {
                // Slow regeneration cadence: roughly 1 HP per 4 seconds.
                livingEntity.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        AURA_REGEN_DURATION_TICKS,
                        0,
                        true,
                        false,
                        true
                ));
            }
        }
    }
}
