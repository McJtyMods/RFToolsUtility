package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketReturnScreenData {
    private GlobalCoordinate pos;
    private Map<Integer, IModuleData> screenData;

    public void toBytes(PacketBuffer buf) {
        NetworkTools.writePos(buf, pos.getCoordinate());
        buf.writeInt(pos.getDimension().getId());

        buf.writeInt(screenData.size());
        for (Map.Entry<Integer, IModuleData> me : screenData.entrySet()) {
            buf.writeInt(me.getKey());
            IModuleData c = me.getValue();
            // @todo 1.14 URGENT
            // @todo 1.14 URGENT
            // @todo 1.14 URGENT
            // @todo 1.14 URGENT
//            buf.writeInt(RFTools.screenModuleRegistry.getShortId(c.getId()));
            c.writeToBuf(buf);
        }
    }

    public GlobalCoordinate getPos() {
        return pos;
    }

    public Map<Integer, IModuleData> getScreenData() {
        return screenData;
    }

    public PacketReturnScreenData() {
    }

    public PacketReturnScreenData(PacketBuffer buf) {
        pos = new GlobalCoordinate(NetworkTools.readPos(buf), DimensionType.getById(buf.readInt()));
        int size = buf.readInt();
        screenData = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            int key = buf.readInt();
            int shortId = buf.readInt();
            // @todo 1.14 URGENT
//            String id = RFTools.screenModuleRegistry.getNormalId(shortId);
//            IModuleDataFactory<?> dataFactory = RFTools.screenModuleRegistry.getModuleDataFactory(id);
//            IModuleData data = dataFactory.createData(buf);
//            screenData.put(key, data);
        }
    }

    public PacketReturnScreenData(GlobalCoordinate pos, Map<Integer, IModuleData> screenData) {
        this.pos = pos;
        this.screenData = screenData;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ScreenTileEntity.screenData.put(getPos(), getScreenData());
        });
        ctx.setPacketHandled(true);
    }
}