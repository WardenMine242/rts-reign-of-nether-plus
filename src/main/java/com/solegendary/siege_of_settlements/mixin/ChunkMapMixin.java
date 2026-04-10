package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    private static BlockPos getCameraBlockPos(ServerPlayer player) {
        Vec3 cameraPos = PlayerServerEvents.getOrthoviewCameraPos(player);
        return cameraPos != null ? BlockPos.containing(cameraPos) : null;
    }

    @Redirect(
            method = "move",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getBlockX()I")
    )
    private int useOrthoviewCameraBlockX(ServerPlayer player) {
        BlockPos cameraBlockPos = getCameraBlockPos(player);
        return cameraBlockPos != null ? cameraBlockPos.getX() : player.getBlockX();
    }

    @Redirect(
            method = "move",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getBlockZ()I")
    )
    private int useOrthoviewCameraBlockZ(ServerPlayer player) {
        BlockPos cameraBlockPos = getCameraBlockPos(player);
        return cameraBlockPos != null ? cameraBlockPos.getZ() : player.getBlockZ();
    }

    @Redirect(
            method = {"move", "updatePlayerPos"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;of(Lnet/minecraft/world/level/entity/EntityAccess;)Lnet/minecraft/core/SectionPos;")
    )
    private SectionPos useOrthoviewCameraSectionPos(EntityAccess entityAccess) {
        if (entityAccess instanceof ServerPlayer player) {
            BlockPos cameraBlockPos = getCameraBlockPos(player);
            if (cameraBlockPos != null) {
                return SectionPos.of(cameraBlockPos);
            }
        }
        return SectionPos.of(entityAccess);
    }
}

