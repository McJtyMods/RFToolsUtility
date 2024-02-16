package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.client.GuiAdvancedPorter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PacketTargetsReady(Integer target, int[] targets, String[] names) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "targetsready");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(target);
        buf.writeInt(targets.length);
        for (int i = 0 ; i < targets.length ; i++) {
            buf.writeInt(targets[i]);
            buf.writeUtf(names[i]);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketTargetsReady create(FriendlyByteBuf buf) {
        int target = buf.readInt();
        int size = buf.readInt();
        int[] targets = new int[size];
        String[] names = new String[size];
        for (int i = 0 ; i < size ; i++) {
            targets[i] = buf.readInt();
            names[i] = buf.readUtf(32767);
        }
        return new PacketTargetsReady(target, targets, names);
    }

    public static PacketTargetsReady create(int target, int[] targets, String[] names) {
        return new PacketTargetsReady(target, targets, names);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            GuiAdvancedPorter.setInfo(target, targets, names);
        });
    }
}