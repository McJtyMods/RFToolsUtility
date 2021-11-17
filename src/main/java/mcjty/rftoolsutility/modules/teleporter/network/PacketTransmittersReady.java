package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TransmitterInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketTransmittersReady {

    private BlockPos pos;
    private List<TransmitterInfo> list;
    private String command;

    public PacketTransmittersReady() {
    }

    public PacketTransmittersReady(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);

        int size = buf.readInt();
        if (size != -1) {
            list = new ArrayList<>(size);
            for (int i = 0 ; i < size ; i++) {
                TransmitterInfo item = new TransmitterInfo(buf);
                list.add(item);
            }
        } else {
            list = null;
        }
    }

    public PacketTransmittersReady(BlockPos pos, String command, List<TransmitterInfo> list) {
        this.pos = pos;
        this.command = command;
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);

        buf.writeUtf(command);

        if (list == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(list.size());
            for (TransmitterInfo item : list) {
                item.toBytes(buf);
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            GenericTileEntity.executeClientCommandHelper(pos, command, list);
        });
        ctx.setPacketHandled(true);
    }
}
