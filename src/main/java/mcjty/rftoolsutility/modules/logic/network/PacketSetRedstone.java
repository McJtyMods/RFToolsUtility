package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSetRedstone(Integer channel, Integer redstone) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "setredstone");
    public static final CustomPacketPayload.Type<PacketSetRedstone> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketSetRedstone> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketSetRedstone::channel,
            ByteBufCodecs.INT, PacketSetRedstone::redstone,
            PacketSetRedstone::new);

    public static PacketSetRedstone create(int channel, int i) {
        return new PacketSetRedstone(channel, i);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            RedstoneChannels channels = RedstoneChannels.getChannels(player.getCommandSenderWorld());
            RedstoneChannels.RedstoneChannel channel = channels.getChannel(this.channel);
            channel.setValue(redstone);
            channels.setDirty();
        });
    }
}
