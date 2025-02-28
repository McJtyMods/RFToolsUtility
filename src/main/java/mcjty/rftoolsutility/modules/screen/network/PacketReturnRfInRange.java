package mcjty.rftoolsutility.modules.screen.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.MachineInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record PacketReturnRfInRange(Map<BlockPos, MachineInfo> levels) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "returnrfinrange");
    public static final CustomPacketPayload.Type<PacketReturnRfInRange> TYPE = new Type<>(ID);

    // Clientside
    public static Map<BlockPos, MachineInfo> clientLevels;

    public static final StreamCodec<FriendlyByteBuf, PacketReturnRfInRange> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, BlockPos.STREAM_CODEC, MachineInfo.STREAM_CODEC), PacketReturnRfInRange::levels,
            PacketReturnRfInRange::new);


    public static PacketReturnRfInRange create(Map<BlockPos, MachineInfo> result) {
        return new PacketReturnRfInRange(result);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public Map<BlockPos, MachineInfo> getLevels() {
        return levels;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            clientLevels = levels;
        });
    }

}