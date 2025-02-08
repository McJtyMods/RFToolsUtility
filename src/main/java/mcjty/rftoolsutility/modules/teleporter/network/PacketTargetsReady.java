package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.client.GuiAdvancedPorter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketTargetsReady(Integer target, int[] targets, String[] names) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "targetsready");
    public static final Type<PacketTargetsReady> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketTargetsReady> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.target);
                buf.writeInt(packet.targets.length);
                for (int i = 0 ; i < packet.targets.length ; i++) {
                    buf.writeInt(packet.targets[i]);
                    buf.writeUtf(packet.names[i]);
                }
            },
            buf -> {
                int target = buf.readInt();
                int size = buf.readInt();
                int[] targets = new int[size];
                String[] names = new String[size];
                for (int i = 0 ; i < size ; i++) {
                    targets[i] = buf.readInt();
                    names[i] = buf.readUtf(32767);
                }
                return new PacketTargetsReady(target, targets, names);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketTargetsReady create(int target, int[] targets, String[] names) {
        return new PacketTargetsReady(target, targets, names);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            GuiAdvancedPorter.setInfo(target, targets, names);
        });
    }
}