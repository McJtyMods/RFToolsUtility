package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.lib.network.AbstractPacketGetListFromServer;
import mcjty.lib.network.PacketSendResultToClient;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;

public class PacketGetPlayers extends AbstractPacketGetListFromServer<String> {

    public PacketGetPlayers(PacketBuffer buf) {
        super(buf);
    }

    public PacketGetPlayers(BlockPos pos, String cmd) {
        super(pos, cmd, TypedMap.EMPTY);
    }

    @Override
    protected SimpleChannel getChannel() {
        return RFToolsUtilityMessages.INSTANCE;
    }

    @Override
    protected Class<String> getType() {
        return String.class;
    }

    @Override
    protected Object createReturnPacket(List<String> list) {
        return new PacketSendResultToClient(pos, command, list);
    }
}
