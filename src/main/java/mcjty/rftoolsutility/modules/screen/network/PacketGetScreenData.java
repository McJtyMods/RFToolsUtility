package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.network.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Map;
import java.util.function.Supplier;

public class PacketGetScreenData {
    private String modid;
    private GlobalCoordinate pos;
    private long millis;

    public void toBytes(PacketBuffer buf) {
        buf.writeString(modid);
        buf.writeBlockPos(pos.getCoordinate());
        buf.writeInt(pos.getDimension().getId());
        buf.writeLong(millis);
    }

    public PacketGetScreenData() {
    }

    public PacketGetScreenData(PacketBuffer buf) {
        modid = buf.readString(32767);
        pos = new GlobalCoordinate(buf.readBlockPos(), DimensionType.getById(buf.readInt()));
        millis = buf.readLong();
    }

    public PacketGetScreenData(String modid, GlobalCoordinate pos, long millis) {
        this.modid = modid;
        this.pos = pos;
        this.millis = millis;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getEntityWorld();
            if (!pos.getDimension().equals(world.getDimension().getType())) {
                return;
            }
            TileEntity te = world.getTileEntity(pos.getCoordinate());
            if(!(te instanceof ScreenTileEntity)) {
                Logging.logError("PacketGetScreenData: TileEntity is not a SimpleScreenTileEntity!");
                return;
            }
            Map<Integer, IModuleData> screenData = ((ScreenTileEntity) te).getScreenData(millis);

            SimpleChannel wrapper = RFToolsUtilityMessages.INSTANCE;
            PacketReturnScreenData msg = new PacketReturnScreenData(pos, screenData);
            wrapper.sendTo(msg, ctx.getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }

}