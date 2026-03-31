package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.orthoview.OrthoviewClientEvents;
import com.solegendary.siege_of_settlements.time.NightUtils;
import com.solegendary.siege_of_settlements.time.TimeClientEvents;
import com.solegendary.siege_of_settlements.time.TimeUtils;
import com.solegendary.siege_of_settlements.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// prevent syncing time from serverside under some conditions

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin {

    @Shadow Minecraft minecraft;

    @Inject(
            method = "handleSetTime",
            at = @At("HEAD"),
            cancellable = true
    )
    private void handleSetTime(ClientboundSetTimePacket pPacket, CallbackInfo ci) {
        Vec3 pos;
        if (OrthoviewClientEvents.isEnabled())
            pos = MiscUtil.getOrthoviewCentreWorldPos(this.minecraft);
        else if (this.minecraft.player != null && this.minecraft.level != null)
            pos = this.minecraft.player.position();
        else
            return;

        ci.cancel();

        TimeClientEvents.serverTime = TimeUtils.normaliseTime(pPacket.getDayTime());

        if (NightUtils.isInRangeOfNightSource(pos, true))
            TimeClientEvents.targetClientTime = 18000; // midnight
        else
            TimeClientEvents.targetClientTime = TimeClientEvents.serverTime;

    }
}
