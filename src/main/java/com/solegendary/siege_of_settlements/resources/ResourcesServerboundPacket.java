package com.solegendary.siege_of_settlements.resources;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.registrars.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ResourcesServerboundPacket {

    ResourcesAction action;
    public String senderName;
    public String receiverName;
    public int food;
    public int wood;
    public int ore;
    public int emerald;

    public static void sendResources(Resources resources, String senderName) {
        PacketHandler.INSTANCE.sendToServer(new ResourcesServerboundPacket(
                ResourcesAction.SEND_RESOURCES,
                senderName,
                resources.ownerName,
                resources.food,
                resources.wood,
                resources.ore,
                resources.emerald
        ));
    }

    public static void sendMarketplaceTrade(ResourcesAction action, String senderName) {
        PacketHandler.INSTANCE.sendToServer(new ResourcesServerboundPacket(
                action,
                senderName,
                "",
                0,
                0,
                0,
                0
        ));
    }

    public ResourcesServerboundPacket(ResourcesAction action, String senderName, String receiverName, int food, int wood, int ore, int emerald) {
        this.action = action;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.food = food;
        this.wood = wood;
        this.ore = ore;
        this.emerald = emerald;
    }

    public ResourcesServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(ResourcesAction.class);
        this.senderName = buffer.readUtf();
        this.receiverName = buffer.readUtf();
        this.food = buffer.readInt();
        this.wood = buffer.readInt();
        this.ore = buffer.readInt();
        this.emerald = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeUtf(this.senderName);
        buffer.writeUtf(this.receiverName);
        buffer.writeInt(this.food);
        buffer.writeInt(this.wood);
        buffer.writeInt(this.ore);
        buffer.writeInt(this.emerald);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                SiegeOfSettlements.LOGGER.warn("ResourcesServerboundPacket: Sender was null");
                success.set(false);
                return;
            }
            if (!player.getName().getString().equals(senderName)) {
                SiegeOfSettlements.LOGGER.warn("ResourcesServerboundPacket: Tried to process packet from " + player.getName() + " for: " + senderName);
                success.set(false);
                return;
            }
            if (action == ResourcesAction.SEND_RESOURCES) {
                ResourcesServerEvents.trySendingAnyResources(this.receiverName, new Resources(this.senderName, this.food, this.wood, this.ore, this.emerald));
            } else if (action == ResourcesAction.MARKET_SELL_FOOD ||
                    action == ResourcesAction.MARKET_SELL_WOOD ||
                    action == ResourcesAction.MARKET_SELL_ORE ||
                    action == ResourcesAction.MARKET_BUY_FOOD ||
                    action == ResourcesAction.MARKET_BUY_WOOD ||
                    action == ResourcesAction.MARKET_BUY_ORE) {
                ResourcesServerEvents.tryMarketplaceTrade(this.senderName, action);
            } else {
                success.set(false);
                return;
            }
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
