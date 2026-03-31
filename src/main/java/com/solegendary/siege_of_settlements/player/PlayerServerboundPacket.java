package com.solegendary.siege_of_settlements.player;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.building.BuildingUtils;
import com.solegendary.siege_of_settlements.building.buildings.placements.BeaconPlacement;
import com.solegendary.siege_of_settlements.gamemode.ClientGameModeHelper;
import com.solegendary.siege_of_settlements.gamemode.GameMode;
import com.solegendary.siege_of_settlements.gamemode.GameModeServerboundPacket;
import com.solegendary.siege_of_settlements.hud.HudClientEvents;
import com.solegendary.siege_of_settlements.registrars.PacketHandler;
import com.solegendary.siege_of_settlements.startpos.StartPosServerEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalClientEvents;
import com.solegendary.siege_of_settlements.survival.SurvivalServerboundPacket;
import com.solegendary.siege_of_settlements.survival.WaveDifficulty;
import com.solegendary.siege_of_settlements.faction.Faction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PlayerServerboundPacket {
    PlayerAction action;
    public int playerId;
    public double x;
    public double y;
    public double z;

    public static void teleportPlayer(Double x, Double y, Double z) {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null) {
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.TELEPORT,
                MC.player.getId(),
                x,
                y,
                z
            ));
        }
    }

    public static void enableOrthoview() {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null) {
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.ENABLE_ORTHOVIEW,
                MC.player.getId(),
                0d,
                0d,
                0d
            ));
        }
    }

    public static void disableOrthoview() {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null) {
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.DISABLE_ORTHOVIEW,
                MC.player.getId(),
                0d,
                0d,
                0d
            ));
        }
    }

    public static void syncOrthoviewCamera(double x, double y, double z) {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null) {
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(
                PlayerAction.SYNC_ORTHOVIEW_CAMERA,
                MC.player.getId(),
                x,
                y,
                z
            ));
        }
    }

    public static void startRTS(Faction faction, Double x, Double y, Double z) {
        Minecraft MC = Minecraft.getInstance();

        if (MC.player != null && MC.level != null) {
            BlockState bs = MC.level.getBlockState(new BlockPos(x.intValue(), y.intValue(), z.intValue()));
            if (!bs.getFluidState().isEmpty() && faction != Faction.NONE) {
                HudClientEvents.showTemporaryMessage(I18n.get("hud.siege_of_settlements.invalid_start_location"));
                return;
            }
            PlayerAction playerAction = switch (faction) {
                case VILLAGERS -> PlayerAction.START_RTS_VILLAGERS;
                case MONSTERS -> PlayerAction.START_RTS_MONSTERS;
                case PIGLINS -> PlayerAction.START_RTS_PIGLINS;
                case NONE -> PlayerAction.START_RTS_SANDBOX;
            };
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(playerAction, MC.player.getId(), x, y, z));
            GameModeServerboundPacket.setAndLockAllClientGameModes(ClientGameModeHelper.gameMode);
            if (ClientGameModeHelper.gameMode == GameMode.SURVIVAL) {
                SurvivalServerboundPacket.startSurvivalMode(SurvivalClientEvents.difficulty);

                CompletableFuture.delayedExecutor(2000, TimeUnit.MILLISECONDS).execute(() -> {
                    WaveDifficulty diff = SurvivalClientEvents.difficulty;
                    String diffMsg = I18n.get("hud.gamemode.siege_of_settlements.survival5",
                            diff, SurvivalClientEvents.getMinutesPerDay()).toLowerCase();
                    diffMsg = diffMsg.substring(0,1).toUpperCase() + diffMsg.substring(1);
                    MC.player.sendSystemMessage(Component.literal(""));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.survival1")
                            .withStyle(Style.EMPTY.withBold(true)));
                    MC.player.sendSystemMessage(Component.literal(diffMsg));
                    MC.player.sendSystemMessage(Component.literal(new String(new char[diffMsg.length()]).replace("\0", "-")));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.survival2"));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.survival3"));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.survival4"));
                    MC.player.sendSystemMessage(Component.literal(""));
                });
            } else if (ClientGameModeHelper.gameMode == GameMode.CLASSIC) {
                BeaconPlacement beacon = BuildingUtils.getBeacon(true);
                boolean isKotB = beacon != null && beacon.getBuilding().capturable;
                if (isKotB) {
                    CompletableFuture.delayedExecutor(2000, TimeUnit.MILLISECONDS).execute(() -> {
                        MC.player.sendSystemMessage(Component.literal(""));
                        MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.kotb1")
                                .withStyle(Style.EMPTY.withBold(true)));
                        MC.player.sendSystemMessage(Component.literal("--------"));
                        MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.kotb2"));
                        MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.kotb3"));
                        MC.player.sendSystemMessage(Component.literal(""));
                    });
                } else {
                    CompletableFuture.delayedExecutor(2000, TimeUnit.MILLISECONDS).execute(() -> {
                        MC.player.sendSystemMessage(Component.literal(""));
                        MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.classic1")
                                .withStyle(Style.EMPTY.withBold(true)));
                        MC.player.sendSystemMessage(Component.literal("--------"));
                        MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.classic2"));
                        MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.classic3"));
                        MC.player.sendSystemMessage(Component.literal(""));
                    });
                }
            } else if (ClientGameModeHelper.gameMode == GameMode.SANDBOX) {
                CompletableFuture.delayedExecutor(2000, TimeUnit.MILLISECONDS).execute(() -> {
                    MC.player.sendSystemMessage(Component.literal(""));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.sandbox1")
                            .withStyle(Style.EMPTY.withBold(true)));
                    MC.player.sendSystemMessage(Component.literal("--------"));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.sandbox2"));
                    MC.player.sendSystemMessage(Component.translatable("hud.gamemode.siege_of_settlements.sandbox3"));
                    MC.player.sendSystemMessage(Component.literal(""));
                });
            }
        }
    }

    public static void startRTSEveryone() {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null && MC.level != null) {
            GameModeServerboundPacket.setAndLockAllClientGameModes(ClientGameModeHelper.gameMode);
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.START_RTS_EVERYONE, MC.player.getId(), 0d,0d,0d));
        }
    }

    public static void cancelStartRTSEveryone() {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null && MC.level != null) {
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.CANCEL_START_RTS_EVERYONE, MC.player.getId(), 0d,0d,0d));
        }
    }

    public static void resetRTS() {
        PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.RESET_RTS, -1, 0d, 0d, 0d));
    }

    // resets and also removes all neutral units and buildings
    public static void resetRTSHard() {
        PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.RESET_RTS_HARD, -1, 0d, 0d, 0d));
    }

    public static void surrender() {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null) {
            PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(
                PlayerAction.DEFEAT,
                MC.player.getId(),
                0d,
                0d,
                0d
            ));
        }
    }

    public static void lockRTS() {
        PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.LOCK_RTS, -1, 0d, 0d, 0d));
    }

    public static void unlockRTS() {
        PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(PlayerAction.UNLOCK_RTS, -1, 0d, 0d, 0d));
    }

    public static void enableRTSSyncing() {
        PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(
            PlayerAction.ENABLE_RTS_SYNCING,
            -1,
            0d,
            0d,
            0d
        ));
    }

    public static void disableRTSSyncing() {
        PacketHandler.INSTANCE.sendToServer(new PlayerServerboundPacket(
            PlayerAction.DISABLE_RTS_SYNCING,
            -1,
            0d,
            0d,
            0d
        ));
    }

    // packet-handler functions
    public PlayerServerboundPacket(PlayerAction action, int playerId, Double x, Double y, Double z) {
        this.action = action;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlayerServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(PlayerAction.class);
        this.playerId = buffer.readInt();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeInt(this.playerId);
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                SiegeOfSettlements.LOGGER.warn("PlayerServerboundPacket: Sender was null");
                success.set(false);
                return;
            }

            int senderId = player.getId();
            if (playerId != -1 && senderId != playerId) {
                SiegeOfSettlements.LOGGER.warn("PlayerServerboundPacket: packet id mismatch from " + player.getName()
                    + " (packet=" + this.playerId + ", sender=" + senderId + "), using sender id");
            }

            switch (action) {
                case TELEPORT -> PlayerServerEvents.movePlayer(senderId, this.x, this.y, this.z);
                case ENABLE_ORTHOVIEW -> PlayerServerEvents.enableOrthoview(senderId);
                case DISABLE_ORTHOVIEW -> PlayerServerEvents.disableOrthoview(senderId);
                case SYNC_ORTHOVIEW_CAMERA -> PlayerServerEvents.syncOrthoviewCamera(senderId, this.x, this.y, this.z);
                case START_RTS_VILLAGERS ->
                    PlayerServerEvents.startRTS(senderId, new Vec3(this.x, this.y, this.z), Faction.VILLAGERS);
                case START_RTS_MONSTERS ->
                    PlayerServerEvents.startRTS(senderId, new Vec3(this.x, this.y, this.z), Faction.MONSTERS);
                case START_RTS_PIGLINS ->
                    PlayerServerEvents.startRTS(senderId, new Vec3(this.x, this.y, this.z), Faction.PIGLINS);
                case START_RTS_SANDBOX ->
                    PlayerServerEvents.startRTS(senderId, new Vec3(this.x, this.y, this.z), Faction.NONE);
                case START_RTS_EVERYONE -> StartPosServerEvents.startGameCountdown();
                case CANCEL_START_RTS_EVERYONE -> StartPosServerEvents.cancelStartGameCountdown(false);
                case DEFEAT -> PlayerServerEvents.defeat(senderId, Component.translatable("server.siege_of_settlements.surrendered").getString());
                case RESET_RTS -> PlayerServerEvents.resetRTS(false);
                case RESET_RTS_HARD -> PlayerServerEvents.resetRTS(true);
                case LOCK_RTS -> PlayerServerEvents.setRTSLock(true);
                case UNLOCK_RTS -> PlayerServerEvents.setRTSLock(false);
                case ENABLE_RTS_SYNCING -> PlayerServerEvents.setRTSSyncingEnabled(true);
                case DISABLE_RTS_SYNCING -> PlayerServerEvents.setRTSSyncingEnabled(false);
            }
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
