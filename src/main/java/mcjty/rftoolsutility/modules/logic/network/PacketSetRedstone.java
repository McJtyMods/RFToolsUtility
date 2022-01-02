package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetRedstone {

    private int channel;
    private int redstone;

    public PacketSetRedstone(FriendlyByteBuf buf) {
        channel = buf.readInt();
        redstone = buf.readInt();
    }

    public PacketSetRedstone(int channel, int redstone) {
        this.channel = channel;
        this.redstone = redstone;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(channel);
        buf.writeInt(redstone);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Player playerEntity = ctx.getSender();
            RedstoneChannels channels = RedstoneChannels.getChannels(playerEntity.getCommandSenderWorld());
            RedstoneChannels.RedstoneChannel channel = channels.getChannel(this.channel);
            channel.setValue(redstone);
            channels.setDirty();
        });
        ctx.setPacketHandled(true);
    }
}
