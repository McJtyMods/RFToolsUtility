package mcjty.rftoolsutility.modules.screen.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.MachineInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record PacketReturnRfInRange(Map<BlockPos, MachineInfo> levels) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "returnrfinrange");

    // Clientside
    public static Map<BlockPos, MachineInfo> clientLevels;

    public static PacketReturnRfInRange create(Map<BlockPos, MachineInfo> result) {
        return new PacketReturnRfInRange(result);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(levels.size());
        for (Map.Entry<BlockPos, MachineInfo> entry : levels.entrySet()) {
            buf.writeBlockPos(entry.getKey());
            MachineInfo info = entry.getValue();
            buf.writeLong(info.energy());
            buf.writeLong(info.maxEnergy());
            if (info.energyPerTick() != null) {
                buf.writeBoolean(true);
                buf.writeLong(info.energyPerTick());
            } else {
                buf.writeBoolean(false);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public Map<BlockPos, MachineInfo> getLevels() {
        return levels;
    }

    public static PacketReturnRfInRange create(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<BlockPos, MachineInfo> levels = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            BlockPos pos = buf.readBlockPos();
            long e = buf.readLong();
            long m = buf.readLong();
            Long usage = null;
            if (buf.readBoolean()) {
                usage = buf.readLong();
            }
            levels.put(pos, new MachineInfo(e, m, usage));
        }
        return new PacketReturnRfInRange(levels);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            clientLevels = levels;
        });
    }

}