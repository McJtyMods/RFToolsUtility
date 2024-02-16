package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsbase.api.screens.data.IModuleDataFactory;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public record PacketReturnScreenData(GlobalPos pos, Map<Integer, IModuleData> screenData) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(RFToolsUtility.MODID, "returnscreendata");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos.pos());
        buf.writeResourceLocation(pos.dimension().location());

        buf.writeInt(screenData.size());
        for (Map.Entry<Integer, IModuleData> me : screenData.entrySet()) {
            buf.writeInt(me.getKey());
            IModuleData c = me.getValue();
            buf.writeInt(RFToolsUtility.screenModuleRegistry.getShortId(c.getId()));
            c.writeToBuf(buf);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public Map<Integer, IModuleData> getScreenData() {
        return screenData;
    }

    public static PacketReturnScreenData create(FriendlyByteBuf buf) {
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

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ScreenTileEntity.screenData.put(getPos(), getScreenData());
        });
    }
}