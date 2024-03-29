package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.modules.teleporter.client.GuiAdvancedPorter;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTargetsReady {

    private int target;
    private int[] targets;
    private String[] names;

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(target);
        buf.writeInt(targets.length);
        for (int i = 0 ; i < targets.length ; i++) {
            buf.writeInt(targets[i]);
            buf.writeUtf(names[i]);
        }
    }

    public PacketTargetsReady() {
    }

    public PacketTargetsReady(PacketBuffer buf) {
        target = buf.readInt();
        int size = buf.readInt();
        targets = new int[size];
        names = new String[size];
        for (int i = 0 ; i < size ; i++) {
            targets[i] = buf.readInt();
            names[i] = buf.readUtf(32767);
        }
    }

    public PacketTargetsReady(int target, int[] targets, String[] names) {
        this.target = target;
        this.targets = targets;
        this.names = names;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            GuiAdvancedPorter.setInfo(target, targets, names);
        });
        ctx.setPacketHandled(true);
    }
}