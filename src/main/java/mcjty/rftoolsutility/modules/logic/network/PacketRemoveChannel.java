package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public record PacketRemoveChannel(Integer channel) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "removechannel");

    public static PacketRemoveChannel create(FriendlyByteBuf buf) {
        return new PacketRemoveChannel(buf.readInt());
    }

    public static PacketRemoveChannel create(int channel) {
        return new PacketRemoveChannel(channel);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(channel);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(playerEntity -> {
                ItemStack informationItem = RedstoneInformationContainer.getRedstoneInformationItem(playerEntity);
                if (informationItem.getItem() instanceof RedstoneInformationItem) {
                    RedstoneInformationItem.removeChannel(informationItem, channel);
                }
            });
        });
    }
}
