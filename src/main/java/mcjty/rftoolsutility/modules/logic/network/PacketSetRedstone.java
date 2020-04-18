package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetRedstone {

    private int channel;
    private int redstone;

    public PacketSetRedstone(PacketBuffer buf) {
        channel = buf.readInt();
        redstone = buf.readInt();
    }

    public PacketSetRedstone(int channel, int redstone) {
        this.channel = channel;
        this.redstone = redstone;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(channel);
        buf.writeInt(redstone);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity playerEntity = ctx.getSender();
            RedstoneChannels channels = RedstoneChannels.getChannels(playerEntity.getEntityWorld());
            RedstoneChannels.RedstoneChannel channel = channels.getChannel(this.channel);
            channel.setValue(redstone);
            channels.markDirty();
        });
        ctx.setPacketHandled(true);
    }
}
