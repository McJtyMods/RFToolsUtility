package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.network.NetworkTools;
import mcjty.rftoolsutility.modules.screen.MachineInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketReturnRfInRange {
    private Map<BlockPos, MachineInfo> levels;

    // Clientside
    public static Map<BlockPos, MachineInfo> clientLevels;

    public void toBytes(PacketBuffer buf) {
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

    public PacketReturnRfInRange(PacketBuffer buf) {
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