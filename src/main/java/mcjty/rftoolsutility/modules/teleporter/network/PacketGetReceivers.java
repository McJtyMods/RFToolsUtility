package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.TypedMapTools;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity.PARAM_PLAYER_UUID;

public class PacketGetReceivers {

    protected BlockPos pos;
    protected TypedMap params;

    public PacketGetReceivers() {
    }

    public PacketGetReceivers(PacketBuffer buf) {
        pos = buf.readBlockPos();
        params = TypedMapTools.readArguments(buf);
    }

    public PacketGetReceivers(BlockPos pos, UUID player) {
        this.pos = pos;
        this.params = TypedMap.builder().put(PARAM_PLAYER_UUID, player).build();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        TypedMapTools.writeArguments(buf, params);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = ctx.getSender().getEntityWorld().getTileEntity(pos);
            if (!(te instanceof ICommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            ICommandHandler commandHandler = (ICommandHandler) te;
            List<TeleportDestinationClientInfo> list = commandHandler.executeWithResultList(DialingDeviceTileEntity.CMD_GETRECEIVERS, params, Type.create(TeleportDestinationClientInfo.class));
            PacketReceiversReady msg = new PacketReceiversReady(pos, DialingDeviceTileEntity.CLIENTCMD_GETRECEIVERS, list);
            RFToolsUtilityMessages.INSTANCE.sendTo(msg, ctx.getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}