package com.solegendary.siege_of_settlements.unit.packets;

import com.solegendary.siege_of_settlements.registrars.PacketHandler;
import com.solegendary.siege_of_settlements.unit.UnitClientEvents;
import com.solegendary.siege_of_settlements.unit.UnitServerEvents;
import com.solegendary.siege_of_settlements.unit.interfaces.WorkerUnit;
import com.solegendary.siege_of_settlements.util.ArrayUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// send a list of worker unit ids that are idle at this point in time
public class UnitIdleWorkerClientBoundPacket {

    private final int[] oldUnitIds; // units to be controlled

    public static void sendIdleWorkerPacket() {
        var units = new LinkedList<Integer>();
        for (LivingEntity livingEntity : UnitServerEvents.getAllUnits()) {
            if (livingEntity instanceof WorkerUnit wu && WorkerUnit.isIdle(wu)) units.add(livingEntity.getId());
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
            new UnitIdleWorkerClientBoundPacket(ArrayUtil.intListToArray(units)));
    }

    // packet-handler functions
    public UnitIdleWorkerClientBoundPacket(
            int[] oldUnitIds
    ) {
        this.oldUnitIds = oldUnitIds;
    }

    public UnitIdleWorkerClientBoundPacket(FriendlyByteBuf buffer) {
        this.oldUnitIds = buffer.readVarIntArray();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(this.oldUnitIds);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            UnitClientEvents.syncIdleWorkers(oldUnitIds);
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
