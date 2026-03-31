package com.solegendary.siege_of_settlements.mixin;

import com.solegendary.siege_of_settlements.orthoview.OrthoviewClientEvents;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class PlayerMixin {

    @Shadow public boolean noPhysics;
    @Shadow public abstract int getId();
    @Shadow public Level level;

    @Inject(
            method = "tick()V",
            at = @At("HEAD")
    )
    // noclip for orthoview players
    // tick() naturally reverses all of this so no need for reversing it here when leaving orthoView
    // only needed if orthoview players are in CREATIVE mode
    private void tick(CallbackInfo ci) {
        int id = this.getId();
        Entity entity = this.level.getEntity(id);
        List<Integer> orthoIds = new ArrayList<>();
        for (ServerPlayer orthoviewPlayer : PlayerServerEvents.orthoviewPlayers) {
            Integer serverPlayerId = orthoviewPlayer.getId();
            orthoIds.add(serverPlayerId);
        }
        if (entity instanceof Player player && player.isCreative() &&
            (orthoIds.contains(id) || (this.level.isClientSide() && OrthoviewClientEvents.isEnabled()))) {
            this.noPhysics = true;
            if (!player.getAbilities().flying) {
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }
    }
}
