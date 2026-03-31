package com.solegendary.siege_of_settlements.unit.packets;

import com.solegendary.siege_of_settlements.registrars.PacketHandler;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.util.ArrayUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// send a list of the old units ids that have been converted into new units (with new ids) so the client can retain
// these units' selections, goals and continue the same actions they were taking
public class UnitConvertClientboundPacket {

    private final String ownerName; // the player that owns these units
    private final int[] oldUnitIds; // units to be controlled
    private final int[] newUnitIds; // units to be controlled

    public static void syncConvertedUnits(String ownerName, List<Integer> oldUnitIds, List<Integer> newUnitIds) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
            new UnitConvertClientboundPacket(
                ownerName,
                ArrayUtil.intListToArray(oldUnitIds),
                ArrayUtil.intListToArray(newUnitIds)
            ));
    }

    // packet-handler functions
    public UnitConvertClientboundPacket(
            String ownerName,
            int[] oldUnitIds,
            int[] newUnitIds
    ) {
        this.ownerName = ownerName;
        this.oldUnitIds = oldUnitIds;
        this.newUnitIds = newUnitIds;
    }

    public UnitConvertClientboundPacket(FriendlyByteBuf buffer) {
        this.ownerName = buffer.readUtf();
        this.oldUnitIds = buffer.readVarIntArray();
        this.newUnitIds = buffer.readVarIntArray();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.ownerName);
        buffer.writeVarIntArray(this.oldUnitIds);
        buffer.writeVarIntArray(this.newUnitIds);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            UnitClientEvents.syncConvertedUnits(ownerName, oldUnitIds, newUnitIds);
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
