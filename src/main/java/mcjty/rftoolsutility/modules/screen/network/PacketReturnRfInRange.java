package mcjty.rftoolsutility.modules.screen.network;

import mcjty.rftoolsutility.modules.screen.MachineInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketReturnRfInRange {
    private Map<BlockPos, MachineInfo> levels;

    // Clientside
    public static Map<BlockPos, MachineInfo> clientLevels;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(levels.size());
        for (Map.Entry<BlockPos, MachineInfo> entry : levels.entrySet()) {
            buf.writeBlockPos(entry.getKey());
            MachineInfo info = entry.getValue();
            buf.writeLong(info.getEnergy());
            buf.writeLong(info.getMaxEnergy());
            if (info.getEnergyPerTick() != null) {
                buf.writeBoolean(true);
                buf.writeLong(info.getEnergyPerTick());
            } else {
                buf.writeBoolean(false);
            }
        }
    }

    public Map<BlockPos, MachineInfo> getLevels() {
        return levels;
    }

    public PacketReturnRfInRange() {
    }

    public PacketReturnRfInRange(FriendlyByteBuf buf) {
        int size = buf.readInt();
        levels = new HashMap<>(size);
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
    }

    public PacketReturnRfInRange(Map<BlockPos, MachineInfo> levels) {
        this.levels = levels;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            clientLevels = levels;
        });
        ctx.setPacketHandled(true);
    }

}