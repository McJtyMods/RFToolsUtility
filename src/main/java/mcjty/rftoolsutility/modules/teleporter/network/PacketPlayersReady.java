package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.network.IClientCommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Logging;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketPlayersReady {

    private BlockPos pos;
    private List<String> list;
    private String command;

    public PacketPlayersReady() {
    }

    public PacketPlayersReady(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        list = NetworkTools.readStringList(buf);
    }

    public PacketPlayersReady(BlockPos pos, String command, List<String> list) {
        this.pos = pos;
        this.command = command;
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        NetworkTools.writeStringList(buf, list);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = McJtyLib.proxy.getClientWorld().getBlockEntity(pos);
            if(!(te instanceof IClientCommandHandler)) {
                Logging.log("createInventoryReadyPacket: TileEntity is not a ClientCommandHandler!");
                return;
            }
            IClientCommandHandler clientCommandHandler = (IClientCommandHandler) te;
            if (!clientCommandHandler.receiveListFromServer(command, list, Type.STRING)) {
                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }

}
