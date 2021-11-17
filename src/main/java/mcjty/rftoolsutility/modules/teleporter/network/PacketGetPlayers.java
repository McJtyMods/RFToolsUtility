package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.TypedMapTools;
import mcjty.lib.tileentity.GenericTileEntity;
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

    public PacketGetPlayers(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        params = TypedMapTools.readArguments(buf);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
    }

    public PacketGetPlayers() {
    }

    public PacketGetPlayers(BlockPos pos, String cmd) {
        this.pos = pos;
        this.command = cmd;
        this.params = TypedMap.EMPTY;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            World world = ctx.getSender().getCommandSenderWorld();
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity) {
                    List<String> list = ((GenericTileEntity) te).executeServerCommandList(command, ctx.getSender(), params, String.class);
                    RFToolsUtilityMessages.INSTANCE.sendTo(new PacketPlayersReady(pos, command, list),
                            ctx.getSender().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                } else {
                    Logging.log("Command not handled!");
                    return;
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
