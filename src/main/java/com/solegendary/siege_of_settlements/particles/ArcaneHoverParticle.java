package com.solegendary.siege_of_settlements.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArcaneHoverParticle extends TextureSheetParticle {
    private static final int MIN_LIFETIME_TICKS = 40;
    private static final int MAX_EXTRA_LIFETIME_TICKS = 10;
    private static final int FADE_OUT_TICKS = 10;

    protected ArcaneHoverParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.friction = 1.0F;
        this.quadSize = 0.24F + this.random.nextFloat() * 0.10F;
        this.lifetime = MIN_LIFETIME_TICKS + this.random.nextInt(MAX_EXTRA_LIFETIME_TICKS + 1);
        this.alpha = 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        int ticksLeft = this.lifetime - this.age;
        if (ticksLeft <= FADE_OUT_TICKS) {
            this.alpha = Math.max(0.0F, ticksLeft / (float) FADE_OUT_TICKS);
        } else {
            this.alpha = 1.0F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type,
                                       ClientLevel level,
                                       double x,
                                       double y,
                                       double z,
                                       double xSpeed,
                                       double ySpeed,
                                       double zSpeed) {
            ArcaneHoverParticle particle = new ArcaneHoverParticle(level, x, y, z);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
