package mcjty.rftoolsutility.modules.logic.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PacketSetRedstone(Integer channel, Integer redstone) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "setredstone");

    public static PacketSetRedstone create(FriendlyByteBuf buf) {
        return new PacketSetRedstone(buf.readInt(), buf.readInt());
    }

    public static PacketSetRedstone create(int channel, int i) {
        return new PacketSetRedstone(channel, i);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(channel);
        buf.writeInt(redstone);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(playerEntity -> {
                RedstoneChannels channels = RedstoneChannels.getChannels(playerEntity.getCommandSenderWorld());
                RedstoneChannels.RedstoneChannel channel = channels.getChannel(this.channel);
                channel.setValue(redstone);
                channels.setDirty();
            });
        });
    }
}
