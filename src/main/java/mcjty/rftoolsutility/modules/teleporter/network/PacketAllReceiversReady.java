package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.modules.teleporter.client.GuiTeleportProbe;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketAllReceiversReady {
    private List<TeleportDestinationClientInfo> destinationList;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(destinationList.size());
        for (TeleportDestination destination : destinationList) {
            destination.toBytes(buf);
        }
    }

    public PacketAllReceiversReady() {
    }

    public PacketAllReceiversReady(FriendlyByteBuf buf) {
        int size = buf.readInt();
        destinationList = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            destinationList.add(new TeleportDestinationClientInfo(buf));
        }
    }

    public PacketAllReceiversReady(List<TeleportDestinationClientInfo> destinationList) {
        this.destinationList = new ArrayList<>();
        this.destinationList.addAll(destinationList);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            GuiTeleportProbe.setReceivers(destinationList);
        });
        ctx.setPacketHandled(true);
    }
}
