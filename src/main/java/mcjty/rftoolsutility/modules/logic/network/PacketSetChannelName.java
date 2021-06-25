package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.modules.logic.blocks.RedstoneTransmitterTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetChannelName {

    private BlockPos pos;
    private String name;

    public PacketSetChannelName(PacketBuffer buf) {
        pos = buf.readBlockPos();
        name = buf.readUtf(32767);
    }

    public PacketSetChannelName(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(name);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity playerEntity = ctx.getSender();
            World world = playerEntity.getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof RedstoneTransmitterTileEntity) {
                    ((RedstoneTransmitterTileEntity) te).setChannelName(name);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
