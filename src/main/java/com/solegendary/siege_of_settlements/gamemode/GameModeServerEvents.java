package com.solegendary.siege_of_settlements.gamemode;

import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.player.RTSPlayer;
import com.solegendary.siege_of_settlements.survival.SurvivalServerEvents;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GameModeServerEvents {

    public static GameMode getGameMode() {
        for (RTSPlayer rtsPlayer : PlayerServerEvents.rtsPlayers)
            if (rtsPlayer.faction == Faction.NONE)
                return GameMode.SANDBOX;

        if (SurvivalServerEvents.isEnabled())
            return GameMode.SURVIVAL;

        return GameMode.CLASSIC;
    }

    private static boolean isGameModeLocked() {
        return !PlayerServerEvents.rtsPlayers.isEmpty();
    }

    public static void syncGameModeForPlayer(ServerPlayer player) {
        if (isGameModeLocked()) {
            GameModeClientboundPacket.setAndLockGameMode(player, getGameMode());
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt) {
        if (isGameModeLocked())
            GameModeClientboundPacket.setAndLockAllClientGameModes(getGameMode());
    }
}
