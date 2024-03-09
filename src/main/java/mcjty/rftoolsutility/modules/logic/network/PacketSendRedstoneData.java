package mcjty.rftoolsutility.modules.logic.network;

import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public record PacketSendRedstoneData(Map<Integer, Pair<String, Integer>> channelData) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "sendredstonedata");

    public static PacketSendRedstoneData create(Map<Integer, Pair<String, Integer>> values) {
        return new PacketSendRedstoneData(values);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(channelData.size());
        for (Map.Entry<Integer, Pair<String, Integer>> entry : channelData.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeUtf(entry.getValue().getKey());
            buf.writeByte(entry.getValue().getRight());
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSendRedstoneData create(FriendlyByteBuf buf) {
        Map<Integer, Pair<String, Integer>> channelData = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            int channel = buf.readInt();
            String name = buf.readUtf(32767);
            int value = buf.readByte();
            channelData.put(channel, Pair.of(name, value));
        }
        return new PacketSendRedstoneData(channelData);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
            if (container instanceof RedstoneInformationContainer) {
                ((RedstoneInformationContainer) container).sendData(channelData);
            }
        });
    }
}
