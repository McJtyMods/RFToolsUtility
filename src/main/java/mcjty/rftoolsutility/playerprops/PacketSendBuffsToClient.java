package mcjty.rftoolsutility.playerprops;

import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record PacketSendBuffsToClient(List<PlayerBuff> buffs) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "sendbuffstoclient");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeByte(buffs.size());
        for (PlayerBuff buff : buffs) {
            buf.writeByte(buff.ordinal());
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSendBuffsToClient create(FriendlyByteBuf buf) {
        int size = buf.readByte();
        List<PlayerBuff> buffs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            buffs.add(PlayerBuff.values()[buf.readByte()]);
        }
        return new PacketSendBuffsToClient(buffs);
    }

    public static PacketSendBuffsToClient create(Map<PlayerBuff, Integer> buffs) {
        return new PacketSendBuffsToClient(new ArrayList<>(buffs.keySet()));
    }

    public List<PlayerBuff> getBuffs() {
        return buffs;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            SendBuffsToClientHelper.setBuffs(this);
        });
    }
}

