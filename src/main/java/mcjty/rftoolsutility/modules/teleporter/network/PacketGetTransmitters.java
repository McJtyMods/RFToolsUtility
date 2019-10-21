package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.TypedMapTools;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.teleporter.data.TransmitterInfo;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.network.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
            TileEntity te = ctx.getSender().getEntityWorld().getTileEntity(pos);
            if(!(te instanceof ICommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            ICommandHandler commandHandler = (ICommandHandler) te;
            List<TransmitterInfo> list = commandHandler.executeWithResultList(DialingDeviceTileEntity.CMD_GETTRANSMITTERS, params, Type.create(TransmitterInfo.class));
            PacketTransmittersReady msg = new PacketTransmittersReady(pos, DialingDeviceTileEntity.CLIENTCMD_GETTRANSMITTERS, list);
            RFToolsUtilityMessages.INSTANCE.sendTo(msg, ctx.getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}
