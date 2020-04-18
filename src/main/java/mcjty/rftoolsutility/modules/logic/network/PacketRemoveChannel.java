package mcjty.rftoolsutility.modules.logic.network;

import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRemoveChannel {

    private int channel;

    public PacketRemoveChannel(PacketBuffer buf) {
        channel = buf.readInt();
    }

    public PacketRemoveChannel(int channel) {
        this.channel = channel;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(channel);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity playerEntity = ctx.getSender();
            ItemStack informationItem = RedstoneInformationContainer.getRedstoneInformationItem(playerEntity);
            if (informationItem.getItem() instanceof RedstoneInformationItem) {
                RedstoneInformationItem.removeChannel(informationItem, channel);
            }
        });
        ctx.setPacketHandled(true);
    }
}
