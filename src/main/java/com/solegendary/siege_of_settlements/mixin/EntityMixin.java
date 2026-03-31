package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.orthoview.OrthoviewClientEvents;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private static double viewScale;
    @Shadow private AABB bb;
    @Shadow public abstract EntityType<?> getType();

    @Inject(
            method = "shouldRenderAtSqrDistance(D)Z",
            at = @At("HEAD"),
            cancellable=true
    )
    private void shouldRenderAtSqrDistance(
            double pDistance, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!OrthoviewClientEvents.isEnabled())
            return;

        Entity self = (Entity) (Object) this;
        double rangeMult;
        if (this.getType() == EntityType.ITEM) {
            rangeMult = 4.0D;
        } else if (self instanceof Unit) {
            // RTS camera needs to keep units visible much farther than vanilla entity culling.
            rangeMult = 12.0D;
        } else {
            return;
        }

        double d0 = this.bb.getSize();
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * viewScale * rangeMult;
        cir.setReturnValue(pDistance < d0 * d0);
    }

    private Vec3 getOrthoviewCameraPosIfServerPlayer() {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof ServerPlayer serverPlayer)) {
            return null;
        }
        return PlayerServerEvents.getOrthoviewCameraPos(serverPlayer);
    }

    @Inject(method = "getBlockX", at = @At("HEAD"), cancellable = true)
    private void getBlockXFromOrthoviewCamera(CallbackInfoReturnable<Integer> cir) {
        Vec3 cameraPos = getOrthoviewCameraPosIfServerPlayer();
        if (cameraPos != null) {
            cir.setReturnValue(BlockPos.containing(cameraPos).getX());
        }
    }

    @Inject(method = "getBlockZ", at = @At("HEAD"), cancellable = true)
    private void getBlockZFromOrthoviewCamera(CallbackInfoReturnable<Integer> cir) {
        Vec3 cameraPos = getOrthoviewCameraPosIfServerPlayer();
        if (cameraPos != null) {
            cir.setReturnValue(BlockPos.containing(cameraPos).getZ());
        }
    }

    @Inject(method = "blockPosition", at = @At("HEAD"), cancellable = true)
    private void blockPositionFromOrthoviewCamera(CallbackInfoReturnable<BlockPos> cir) {
        Vec3 cameraPos = getOrthoviewCameraPosIfServerPlayer();
        if (cameraPos != null) {
            cir.setReturnValue(BlockPos.containing(cameraPos));
        }
    }

    @Inject(method = "chunkPosition", at = @At("HEAD"), cancellable = true)
    private void chunkPositionFromOrthoviewCamera(CallbackInfoReturnable<ChunkPos> cir) {
        Vec3 cameraPos = getOrthoviewCameraPosIfServerPlayer();
        if (cameraPos != null) {
            cir.setReturnValue(new ChunkPos(BlockPos.containing(cameraPos)));
        }
    }

    // use this mixin if you want a mob to avoid damage and not even register a damage animation
    @Inject(
            method = "isInvulnerableTo",
            at = @At("HEAD"),
            cancellable=true
    )
    private void isInvulnerableTo(DamageSource pSource, CallbackInfoReturnable<Boolean> cir) {
        if (pSource == damageSources().inWall())
            cir.setReturnValue(true);
    }

    @Shadow public int getTicksRequiredToFreeze() { return 140; }
    @Shadow public int getTicksFrozen() { return 0; }

    @Shadow public abstract DamageSources damageSources();
    @Shadow public abstract Component getName();
    @Shadow public abstract void remove(Entity.RemovalReason pReason);
    @Shadow public abstract AABB getBoundingBox();
    @Shadow public abstract BlockPos getOnPos();
    @Shadow public abstract Level level();

    @Inject(
            method = "getPercentFrozen",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void getPercentFrozen(CallbackInfoReturnable<Float> cir) {
        int i = this.getTicksRequiredToFreeze();
        float percent = (float)Math.min(this.getTicksFrozen(), 140) / (float)i;
        cir.setReturnValue(Math.min(percent, 0.5f));
    }
}
