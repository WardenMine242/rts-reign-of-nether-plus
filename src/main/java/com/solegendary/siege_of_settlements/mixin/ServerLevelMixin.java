package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    private static double getSoundRange(float volume) {
        return volume > 1.0F ? 16.0D * volume : 16.0D;
    }

    private static Vec3 getViewPos(ServerPlayer player) {
        if (player == null) {
            return Vec3.ZERO;
        }
        return PlayerServerEvents.getOrthoviewViewPos(player);
    }

    @Inject(
            method = "sendParticles(Lnet/minecraft/server/level/ServerPlayer;ZDDDLnet/minecraft/network/protocol/Packet;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sendParticles(ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ,
                               Packet<?> pPacket, CallbackInfoReturnable<Boolean> cir) {
        if (pPlayer == null) {
            return;
        }

        Vec3 cameraPos = PlayerServerEvents.getOrthoviewCameraPos(pPlayer);
        if (cameraPos == null) {
            return;
        }

        Vec3 viewPos = getViewPos(pPlayer);
        Vec3 particlePos = new Vec3(pPosX, pPosY, pPosZ);
        double baseDistance = pLongDistance ? 512.0 : 32.0;

        // In RTS mode, particle visibility is based on camera position only.
        // This unloads particle traffic around the frozen player body and loads it around the RTS camera.
        if (viewPos.closerThan(particlePos, baseDistance)) {
            pPlayer.connection.send(pPacket);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At("TAIL")
    )
    private void playSeededSoundForOrthoviewPlayers(Player pPlayer, double pX, double pY, double pZ,
                                                    Holder<SoundEvent> pSound, SoundSource pSource,
                                                    float pVolume, float pPitch, long pSeed, CallbackInfo ci) {
        if (PlayerServerEvents.orthoviewPlayers.isEmpty()) {
            return;
        }

        ServerLevel level = (ServerLevel) (Object) this;
        Vec3 soundPos = new Vec3(pX, pY, pZ);
        double range = getSoundRange(pVolume);
        ClientboundSoundPacket packet = new ClientboundSoundPacket(pSound, pSource, pX, pY, pZ, pVolume, pPitch, pSeed);

        Set<UUID> sent = new HashSet<>();
        for (ServerPlayer orthoviewPlayer : PlayerServerEvents.orthoviewPlayers) {
            if (orthoviewPlayer == null || orthoviewPlayer.level() != level) {
                continue;
            }
            if (!sent.add(orthoviewPlayer.getUUID())) {
                continue;
            }
            if (pPlayer != null && orthoviewPlayer.getId() == pPlayer.getId()) {
                continue;
            }
            // if vanilla already sent this packet due normal range, skip to avoid duplicate sounds
            if (getViewPos(orthoviewPlayer).closerThan(soundPos, range)) {
                continue;
            }
            orthoviewPlayer.connection.send(packet);
        }
    }

    @Inject(
            method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
            at = @At("TAIL")
    )
    private void playSeededEntitySoundForOrthoviewPlayers(Player pPlayer, Entity pEntity,
                                                          Holder<SoundEvent> pSound, SoundSource pSource,
                                                          float pVolume, float pPitch, long pSeed, CallbackInfo ci) {
        if (PlayerServerEvents.orthoviewPlayers.isEmpty() || pEntity == null) {
            return;
        }

        ServerLevel level = (ServerLevel) (Object) this;
        Vec3 soundPos = pEntity.position();
        double range = getSoundRange(pVolume);
        ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(pSound, pSource, pEntity, pVolume, pPitch, pSeed);

        Set<UUID> sent = new HashSet<>();
        for (ServerPlayer orthoviewPlayer : PlayerServerEvents.orthoviewPlayers) {
            if (orthoviewPlayer == null || orthoviewPlayer.level() != level) {
                continue;
            }
            if (!sent.add(orthoviewPlayer.getUUID())) {
                continue;
            }
            if (pPlayer != null && orthoviewPlayer.getId() == pPlayer.getId()) {
                continue;
            }
            // if vanilla already sent this packet due normal range, skip to avoid duplicate sounds
            if (getViewPos(orthoviewPlayer).closerThan(soundPos, range)) {
                continue;
            }
            orthoviewPlayer.connection.send(packet);
        }
    }
}
