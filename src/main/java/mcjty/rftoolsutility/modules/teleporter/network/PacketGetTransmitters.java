package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.TypedMapTools;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TransmitterInfo;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketGetTransmitters {

    protected BlockPos pos;
    protected TypedMap params;

    public PacketGetTransmitters() {
    }

    public PacketGetTransmitters(PacketBuffer buf) {
        pos = buf.readBlockPos();
        params = TypedMapTools.readArguments(buf);
    }

    public PacketGetTransmitters(BlockPos pos) {
        this.pos = pos;
        this.params = TypedMap.EMPTY;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        TypedMapTools.writeArguments(buf, params);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity) {
                    List<TransmitterInfo> list = ((GenericTileEntity) te).executeServerCommandList(DialingDeviceTileEntity.CMD_GETTRANSMITTERS.getName(), ctx.getSender(), params, TransmitterInfo.class);
                    PacketTransmittersReady msg = new PacketTransmittersReady(pos, DialingDeviceTileEntity.CMD_GETTRANSMITTERS.getName(), list);
                    RFToolsUtilityMessages.INSTANCE.sendTo(msg, ctx.getSender().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                } else {
                    Logging.log("Command not handled!");
                    return;
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
