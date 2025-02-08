package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSetChannelName(BlockPos pos, String name) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "setchannelname");
    public static final CustomPacketPayload.Type<PacketSetChannelName> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketSetChannelName> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketSetChannelName::pos,
            ByteBufCodecs.STRING_UTF8, PacketSetChannelName::name,
            PacketSetChannelName::new);

    public static PacketSetChannelName create(BlockPos worldPosition, String channelName) {
        return new PacketSetChannelName(worldPosition, channelName);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof RedstoneTransmitterTileEntity transmitter) {
                    transmitter.setChannelName(name);
                }
            }
        });
    }
}
