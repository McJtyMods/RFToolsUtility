package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.TypedMapTools;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketGetPlayers {

    protected BlockPos pos;
    protected String command;
    protected TypedMap params;
    private String clientcmd;

    public PacketGetPlayers(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readString(32767);
        params = TypedMapTools.readArguments(buf);
        clientcmd = buf.readString(32767);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeString(command);
        TypedMapTools.writeArguments(buf, params);
        buf.writeString(clientcmd);
    }

    public PacketGetPlayers() {
    }

    public PacketGetPlayers(BlockPos pos, String cmd, String clientcmd) {
        this.pos = pos;
        this.command = cmd;
        this.params = TypedMap.EMPTY;
        this.clientcmd = clientcmd;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getEntityWorld();
            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (!(te instanceof ICommandHandler)) {
                    Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                    return;
                }
                ICommandHandler commandHandler = (ICommandHandler) te;
                List<String> list = commandHandler.executeWithResultList(command, params, Type.STRING);
                RFToolsUtilityMessages.INSTANCE.sendTo(new PacketPlayersReady(pos, clientcmd, list),
                        ctx.getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.setPacketHandled(true);
    }
}
