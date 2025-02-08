package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.client.GuiTeleportProbe;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record PacketAllReceiversReady(List<TeleportDestinationClientInfo> destinationList) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "allreceiversready");
    public static final Type<PacketAllReceiversReady> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketAllReceiversReady> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.destinationList.size());
                for (TeleportDestination destination : packet.destinationList) {
                    destination.toBytes(buf);
                }
            },
            buf -> {
                int size = buf.readInt();
                List<TeleportDestinationClientInfo> destinationList = new ArrayList<>(size);
                for (int i = 0 ; i < size ; i++) {
                    destinationList.add(new TeleportDestinationClientInfo(buf));
                }
                return new PacketAllReceiversReady(destinationList);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public PacketAllReceiversReady(List<TeleportDestinationClientInfo> destinationList) {
        this.destinationList = new ArrayList<>();
        this.destinationList.addAll(destinationList);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            GuiTeleportProbe.setReceivers(destinationList);
        });
    }
}
