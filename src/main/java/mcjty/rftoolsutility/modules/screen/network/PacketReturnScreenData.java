package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsbase.api.screens.data.IModuleDataFactory;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record PacketReturnScreenData(GlobalPos pos, Map<Integer, IModuleData> screenData) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "returnscreendata");
    public static final CustomPacketPayload.Type<PacketReturnScreenData> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketReturnScreenData> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBlockPos(packet.pos().pos());
                buf.writeResourceLocation(packet.pos().dimension().location());

                buf.writeInt(packet.screenData().size());
                for (Map.Entry<Integer, IModuleData> me : packet.screenData().entrySet()) {
                    buf.writeInt(me.getKey());
                    IModuleData c = me.getValue();
                    buf.writeInt(RFToolsUtility.screenModuleRegistry.getShortId(c.getId()));
                    c.writeToBuf(buf);
                }
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                ResourceKey<Level> dim = LevelTools.getId(buf.readResourceLocation());
                int size = buf.readInt();
                Map<Integer, IModuleData> screenData = new HashMap<>(size);
                for (int i = 0 ; i < size ; i++) {
                    int key = buf.readInt();
                    int shortId = buf.readInt();
                    String id = RFToolsUtility.screenModuleRegistry.getNormalId(shortId);
                    IModuleDataFactory<?> dataFactory = RFToolsUtility.screenModuleRegistry.getModuleDataFactory(id);
                    IModuleData data = dataFactory.createData(buf);
                    screenData.put(key, data);
                }
                return new PacketReturnScreenData(GlobalPos.of(dim, pos), screenData);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public Map<Integer, IModuleData> getScreenData() {
        return screenData;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ScreenTileEntity.screenData.put(getPos(), getScreenData());
        });
    }
}