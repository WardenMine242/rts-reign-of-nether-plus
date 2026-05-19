package com.solegendary.siege_of_settlements.entities;

import com.solegendary.siege_of_settlements.registrars.EntityRegistrar;
import com.solegendary.siege_of_settlements.registrars.ParticleRegistrar;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ArcaneChargeProjectile extends AbstractHurtingProjectile {
    public static final double MAX_TRAVEL_DISTANCE = 40.0D;
    public static final double GHAST_CHARGE_SPEED = 4.0D;
    private static final double MAX_TRAVEL_DISTANCE_SQR = MAX_TRAVEL_DISTANCE * MAX_TRAVEL_DISTANCE;
    private static final float DEFAULT_PROJECTILE_DAMAGE = 14.0F;
    private static final int TRAIL_PARTICLE_INTERVAL_TICKS = 1;
    private static final double TRAIL_PARTICLE_SPACING = 1.0D;
    private static final double TRAIL_BACK_OFFSET = 0.12D;

    private boolean startPosInitialized = false;
    private double startX;
    private double startY;
    private double startZ;
    private float projectileDamage = DEFAULT_PROJECTILE_DAMAGE;
    private double trailDistanceUntilNextParticle = 0.0D;

    public ArcaneChargeProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.setInvisible(true);
        this.noPhysics = true;
    }

    public ArcaneChargeProjectile(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityRegistrar.ARCANE_CHARGE_PROJECTILE.get(), shooter, offsetX, offsetY, offsetZ, level);
        this.setNoGravity(true);
        this.setInvisible(true);
        this.noPhysics = true;
    }

    public void setProjectileDamage(float damage) {
        this.projectileDamage = Math.max(0.0F, damage);
    }

    @Override
    public void tick() {
        initialiseStartPosIfNeeded();
        this.setNoGravity(true);
        this.setInvisible(true);
        this.noPhysics = true;

        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-8D) {
            this.setDeltaMovement(motion.normalize().scale(GHAST_CHARGE_SPEED));
        }

        super.tick();

        if (this.level().isClientSide()) {
            spawnDenseArcaneTrail();
        }

        if (this.distanceToSqr(startX, startY, startZ) >= MAX_TRAVEL_DISTANCE_SQR) {
            this.discard();
        }
    }

    private void initialiseStartPosIfNeeded() {
        if (!startPosInitialized) {
            this.startPosInitialized = true;
            this.startX = this.getX();
            this.startY = this.getY();
            this.startZ = this.getZ();
        }
    }

    private void spawnDenseArcaneTrail() {
        if (this.tickCount % TRAIL_PARTICLE_INTERVAL_TICKS != 0) {
            return;
        }
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() <= 1.0E-8D) {
            return;
        }
        Vec3 direction = motion.normalize();
        Vec3 previousPos = new Vec3(this.xo, this.yo, this.zo).subtract(direction.scale(TRAIL_BACK_OFFSET));
        Vec3 currentPos = this.position().subtract(direction.scale(TRAIL_BACK_OFFSET));
        double segmentLength = currentPos.distanceTo(previousPos);
        if (segmentLength <= 1.0E-8D) {
            return;
        }

        double distanceAlongSegment = trailDistanceUntilNextParticle;
        if (distanceAlongSegment <= 0.0D) {
            distanceAlongSegment = TRAIL_PARTICLE_SPACING;
        }

        while (distanceAlongSegment <= segmentLength + 1.0E-8D) {
            Vec3 trailPos = previousPos.add(direction.scale(distanceAlongSegment));
            this.level().addParticle(
                    ParticleRegistrar.ARCANE_PARTICLE.get(),
                    trailPos.x,
                    trailPos.y,
                    trailPos.z,
                    0.0D,
                    0.0D,
                    0.0D
            );
            distanceAlongSegment += TRAIL_PARTICLE_SPACING;
        }
        trailDistanceUntilNextParticle = distanceAlongSegment - segmentLength;
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            super.onHit(result);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Intentionally ignored: this projectile passes through blocks.
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {
            result.getEntity().hurt(this.damageSources().magic(), projectileDamage);
        }
        super.onHitEntity(result);
        this.discard();
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleRegistrar.ARCANE_PARTICLE.get();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}
