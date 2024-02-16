package mcjty.rftoolsutility.modules.logic.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record PacketSetChannelName(BlockPos pos, String name) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "setchannelname");

    public static PacketSetChannelName create(FriendlyByteBuf buf) {
        return new PacketSetChannelName(buf.readBlockPos(), buf.readUtf(32767));
    }

    public static PacketSetChannelName create(BlockPos worldPosition, String channelName) {
        return new PacketSetChannelName(worldPosition, channelName);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(name);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(playerEntity -> {
                Level world = playerEntity.getCommandSenderWorld();
                if (world.hasChunkAt(pos)) {
                    BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof RedstoneTransmitterTileEntity transmitter) {
                        transmitter.setChannelName(name);
                    }
                }
            });
        });
    }
}
