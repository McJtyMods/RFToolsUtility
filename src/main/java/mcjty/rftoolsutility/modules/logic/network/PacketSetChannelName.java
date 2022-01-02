package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetChannelName {

    private BlockPos pos;
    private String name;

    public PacketSetChannelName(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        name = buf.readUtf(32767);
    }

    public PacketSetChannelName(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(name);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Player playerEntity = ctx.getSender();
            Level world = playerEntity.getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof RedstoneTransmitterTileEntity) {
                    ((RedstoneTransmitterTileEntity) te).setChannelName(name);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
