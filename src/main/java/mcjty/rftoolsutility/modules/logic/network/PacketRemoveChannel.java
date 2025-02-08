package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketRemoveChannel(Integer channel) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "removechannel");
    public static final CustomPacketPayload.Type<PacketRemoveChannel> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketRemoveChannel> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketRemoveChannel::channel,
            PacketRemoveChannel::new);

    public static PacketRemoveChannel create(int channel) {
        return new PacketRemoveChannel(channel);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack informationItem = RedstoneInformationContainer.getRedstoneInformationItem(player);
            if (informationItem.getItem() instanceof RedstoneInformationItem) {
                RedstoneInformationItem.removeChannel(informationItem, channel);
            }
        });
    }
}
