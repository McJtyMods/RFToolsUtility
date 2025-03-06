package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.client.GuiTeleportProbe;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record PacketAllReceiversReady(List<TeleportDestinationClientInfo> destinationList) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "allreceiversready");
    public static final Type<PacketAllReceiversReady> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAllReceiversReady> CODEC = StreamCodec.composite(
            TeleportDestinationClientInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), d -> d.destinationList,
            PacketAllReceiversReady::new
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
