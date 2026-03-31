package com.solegendary.siege_of_settlements.guiscreen;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.player.PlayerServerEvents;
import com.solegendary.siege_of_settlements.registrars.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TopdownGuiServerboundPacket {
    public boolean topdownGuiOpen = false;
    public int playerId = -1; // to track

    // client-side helper functions
    public static void openTopdownGui(int playerId) {
        PacketHandler.INSTANCE.sendToServer(new TopdownGuiServerboundPacket(true, playerId));
    }
    public static void closeTopdownGui(int playerId) {
        Minecraft.getInstance().popGuiLayer();
        PacketHandler.INSTANCE.sendToServer(new TopdownGuiServerboundPacket(false, playerId));
    }


    // packet-handler functions
    public TopdownGuiServerboundPacket(Boolean pos, int playerId) {
        this.topdownGuiOpen = pos;
        this.playerId = playerId;
    }

    public TopdownGuiServerboundPacket(FriendlyByteBuf buffer) {
        this.topdownGuiOpen = buffer.readBoolean();
        this.playerId = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.topdownGuiOpen);
        buffer.writeInt(this.playerId);
    }


    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                SiegeOfSettlements.LOGGER.warn("TopdownGuiServerboundPacket: Sender was null");
                success.set(false);
                return;
            }

            int senderId = player.getId();
            if (senderId != playerId) {
                SiegeOfSettlements.LOGGER.warn("TopdownGuiServerboundPacket: packet id mismatch from " + player.getName()
                    + " (packet=" + this.playerId + ", sender=" + senderId + "), using sender id");
            }

            if (this.topdownGuiOpen)
                PlayerServerEvents.openTopdownGui(senderId);
            else
                PlayerServerEvents.closeTopdownGui(senderId);

            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
