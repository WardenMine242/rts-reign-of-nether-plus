package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class ChunkMapTrackedEntityMixin {

    @Shadow @Final private ServerEntity serverEntity;
    @Shadow @Final private Entity entity;
    @Shadow @Final private int range;
    @Shadow @Final private Set<ServerPlayerConnection> seenBy;

    @Inject(
            method = "updatePlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void updatePlayerWithOrthoviewCamera(ServerPlayer player, CallbackInfo ci) {
        Vec3 cameraPos = PlayerServerEvents.getOrthoviewCameraPos(player);
        if (cameraPos == null) {
            return;
        }
        if (player == this.entity) {
            ci.cancel();
            return;
        }

        Vec3 delta = cameraPos.subtract(this.entity.position());
        // vanilla limits tracking distance by player view distance; in RTS we track by camera,
        // so keep the entity-type tracking range without clamping to the frozen player's chunk window
        double maxDistance = this.range;
        double horizontalDistanceSqr = (delta.x * delta.x) + (delta.z * delta.z);
        boolean shouldTrack = horizontalDistanceSqr <= (maxDistance * maxDistance) && this.entity.broadcastToPlayer(player);

        if (shouldTrack) {
            if (this.seenBy.add(player.connection)) {
                this.serverEntity.addPairing(player);
            }
        } else if (this.seenBy.remove(player.connection)) {
            this.serverEntity.removePairing(player);
        }
        ci.cancel();
    }
}
