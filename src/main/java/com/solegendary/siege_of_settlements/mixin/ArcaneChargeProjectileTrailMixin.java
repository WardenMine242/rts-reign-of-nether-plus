package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.entities.ArcaneChargeProjectile;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractHurtingProjectile.class)
public abstract class ArcaneChargeProjectileTrailMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 1
            )
    )
    private void sos$skipDefaultTrailForArcaneCharge(Level level,
                                                      ParticleOptions particle,
                                                      double x,
                                                      double y,
                                                      double z,
                                                      double xSpeed,
                                                      double ySpeed,
                                                      double zSpeed) {
        if (!((Object) this instanceof ArcaneChargeProjectile)) {
            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
