package mcjty.rftoolsutility.modules.logic.network;

import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketSendRedstoneData {

    private Map<Integer, Pair<String, Integer>> channelData;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(channelData.size());
        for (Map.Entry<Integer, Pair<String, Integer>> entry : channelData.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeUtf(entry.getValue().getKey());
            buf.writeByte(entry.getValue().getRight());
        }
    }

    public PacketSendRedstoneData(FriendlyByteBuf buf) {
        channelData = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            int channel = buf.readInt();
            String name = buf.readUtf(32767);
            int value = buf.readByte();
            channelData.put(channel, Pair.of(name, value));
        }
    }

    public PacketSendRedstoneData(Map<Integer, Pair<String, Integer>> channelData) {
        this.channelData = channelData;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
            if (container instanceof RedstoneInformationContainer) {
                ((RedstoneInformationContainer) container).sendData(channelData);
            }
        });
        ctx.setPacketHandled(true);
    }
}
