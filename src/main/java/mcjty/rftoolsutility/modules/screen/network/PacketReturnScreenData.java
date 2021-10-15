package mcjty.rftoolsutility.modules.screen.network;

import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsbase.api.screens.data.IModuleDataFactory;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketReturnScreenData {
    private GlobalPos pos;
    private Map<Integer, IModuleData> screenData;

    public void toBytes(PacketBuffer buf) {
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

    public GlobalPos getPos() {
        return pos;
    }

    public Map<Integer, IModuleData> getScreenData() {
        return screenData;
    }

    public PacketReturnScreenData() {
    }

    public PacketReturnScreenData(PacketBuffer buf) {
        pos = GlobalPos.of(RegistryKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation()), buf.readBlockPos());
        int size = buf.readInt();
        screenData = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            int key = buf.readInt();
            int shortId = buf.readInt();
            String id = RFToolsUtility.screenModuleRegistry.getNormalId(shortId);
            IModuleDataFactory<?> dataFactory = RFToolsUtility.screenModuleRegistry.getModuleDataFactory(id);
            IModuleData data = dataFactory.createData(buf);
            screenData.put(key, data);
        }
    }

    public PacketReturnScreenData(GlobalPos pos, Map<Integer, IModuleData> screenData) {
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