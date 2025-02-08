package mcjty.rftoolsutility.modules.logic.network;

import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public record PacketSendRedstoneData(Map<Integer, Pair<String, Integer>> channelData) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "sendredstonedata");
    public static final CustomPacketPayload.Type<PacketSendRedstoneData> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, Pair<String, Integer>> PAIR_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Pair::getLeft, ByteBufCodecs.INT, Pair::getRight, Pair::of);

    public static final StreamCodec<FriendlyByteBuf, PacketSendRedstoneData> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, PAIR_CODEC), PacketSendRedstoneData::channelData,
            PacketSendRedstoneData::new);

    public static PacketSendRedstoneData create(Map<Integer, Pair<String, Integer>> values) {
        return new PacketSendRedstoneData(values);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
            if (container instanceof RedstoneInformationContainer) {
                ((RedstoneInformationContainer) container).sendData(channelData);
            }
        });
    }
}
