package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.TypedMapTools;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
            World world = ctx.getSender().getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity) {
                    List<TeleportDestinationClientInfo> list = ((GenericTileEntity) te).executeServerCommandList(DialingDeviceTileEntity.CMD_GETRECEIVERS.getName(), ctx.getSender(), params, TeleportDestinationClientInfo.class);
                    PacketReceiversReady msg = new PacketReceiversReady(pos, DialingDeviceTileEntity.CMD_GETRECEIVERS.getName(), list);
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