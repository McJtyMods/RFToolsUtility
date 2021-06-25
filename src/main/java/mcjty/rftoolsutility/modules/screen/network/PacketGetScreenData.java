package mcjty.rftoolsutility.modules.screen.network;

import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PacketGetScreenData {
    private String modid;
    private GlobalCoordinate pos;
    private long millis;

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(modid);
        buf.writeBlockPos(pos.getCoordinate());
        pos.getDimension().toBytes(buf);
        buf.writeLong(millis);
    }

    public PacketGetScreenData() {
    }

    public PacketGetScreenData(PacketBuffer buf) {
        modid = buf.readUtf(32767);
        pos = new GlobalCoordinate(buf.readBlockPos(), DimensionId.fromPacket(buf));
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
            World world = ctx.getSender().getCommandSenderWorld();
//            if (!pos.getDimension().equals(world.getDimension().getType())) {
//                return;
//            }
            world = WorldTools.getWorld(world, pos.getDimension());
            if (world.hasChunkAt(pos.getCoordinate())) {
                TileEntity te = world.getBlockEntity(pos.getCoordinate());
                if (!(te instanceof ScreenTileEntity)) {
                    Logging.logError("PacketGetScreenData: TileEntity is not a SimpleScreenTileEntity!");
                    return;
                }
                Map<Integer, IModuleData> screenData = ((ScreenTileEntity) te).getScreenData(millis);

                PacketReturnScreenData msg = new PacketReturnScreenData(pos, screenData);
                RFToolsUtilityMessages.INSTANCE.sendTo(msg, ctx.getSender().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.setPacketHandled(true);
    }

}