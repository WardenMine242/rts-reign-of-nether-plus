package com.WardenMine242.siege_of_settlements.network;

import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.solegendary.siege_of_settlements.api.SiegeOfSettlementsRegistries;
import com.solegendary.siege_of_settlements.building.production.ProductionItem;
import com.solegendary.siege_of_settlements.registrars.PacketHandler;
import com.WardenMine242.siege_of_settlements.research.ResearchTreePrerequisites;
import com.solegendary.siege_of_settlements.research.ResearchServerEvents;
import com.solegendary.siege_of_settlements.resources.ResourceCost;
import com.solegendary.siege_of_settlements.resources.ResourceName;
import com.solegendary.siege_of_settlements.resources.Resources;
import com.solegendary.siege_of_settlements.resources.ResourcesClientboundPacket;
import com.solegendary.siege_of_settlements.resources.ResourcesServerEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ResearchTreeServerboundPacket {

    public String playerName;
    public ResourceLocation researchId;

    public static void sendResearchRequest(ResourceLocation researchId) {
        if (net.minecraft.client.Minecraft.getInstance().player != null) {
            PacketHandler.INSTANCE.sendToServer(new ResearchTreeServerboundPacket(
                    net.minecraft.client.Minecraft.getInstance().player.getName().getString(),
                    researchId
            ));
        }
    }

    public ResearchTreeServerboundPacket(String playerName, ResourceLocation researchId) {
        this.playerName = playerName;
        this.researchId = researchId;
    }

    public ResearchTreeServerboundPacket(FriendlyByteBuf buffer) {
        this.playerName = buffer.readUtf();
        this.researchId = buffer.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.playerName);
        buffer.writeResourceLocation(this.researchId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !player.getName().getString().equals(this.playerName)) {
                success.set(false);
                return;
            }

            ProductionItem item = SiegeOfSettlementsRegistries.PRODUCTION_ITEM.get(this.researchId);
            if (item == null) {
                success.set(false);
                return;
            }

            if (ResearchServerEvents.playerHasResearch(this.playerName, item)) {
                success.set(false);
                return;
            }

            if (!ResearchTreePrerequisites.serverMeetsPrerequisites(this.playerName, this.researchId)) {
                success.set(false);
                return;
            }

            if (item.canAfford(player.level(), this.playerName)) {
                ResourceCost cost = item.getCost(false, this.playerName);
                if (cost.food > 0 || cost.wood > 0 || cost.ore > 0 || cost.emerald > 0) {
                    ResourcesServerEvents.addSubtractResources(new Resources(
                            this.playerName,
                            -cost.food,
                            -cost.wood,
                            -cost.ore,
                            -cost.emerald
                    ));
                }
                ResearchServerEvents.addResearch(this.playerName, item);
                ResearchServerEvents.syncResearch(this.playerName);
                success.set(true);
            } else {
                ResourcesClientboundPacket.warnInsufficientResources(this.playerName,
                        ResourcesServerEvents.canAfford(this.playerName, ResourceName.FOOD, item.getCost(false, this.playerName).food),
                        ResourcesServerEvents.canAfford(this.playerName, ResourceName.WOOD, item.getCost(false, this.playerName).wood),
                        ResourcesServerEvents.canAfford(this.playerName, ResourceName.ORE, item.getCost(false, this.playerName).ore),
                        ResourcesServerEvents.canAfford(this.playerName, ResourceName.EMERALDS, item.getCost(false, this.playerName).emerald)
                );
                success.set(false);
            }
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
