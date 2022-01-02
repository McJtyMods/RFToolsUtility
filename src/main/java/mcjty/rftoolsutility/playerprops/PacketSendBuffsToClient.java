package mcjty.rftoolsutility.playerprops;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PacketSendBuffsToClient {
    private List<PlayerBuff> buffs;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(buffs.size());
        for (PlayerBuff buff : buffs) {
            buf.writeByte(buff.ordinal());
        }
    }

    public PacketSendBuffsToClient() {
        buffs = null;
    }

    public PacketSendBuffsToClient(FriendlyByteBuf buf) {
        int size = buf.readByte();
        buffs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            buffs.add(PlayerBuff.values()[buf.readByte()]);
        }
    }

    public PacketSendBuffsToClient(Map<PlayerBuff, Integer> buffs) {
        this.buffs = new ArrayList<>(buffs.keySet());
    }

    public List<PlayerBuff> getBuffs() {
        return buffs;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            SendBuffsToClientHelper.setBuffs(this);
        });
        ctx.setPacketHandled(true);
    }
}

