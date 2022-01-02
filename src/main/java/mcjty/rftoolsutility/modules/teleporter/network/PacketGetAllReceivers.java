package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketGetAllReceivers {

    public void toBytes(FriendlyByteBuf buf) {
    }

    public PacketGetAllReceivers() {
    }

    public PacketGetAllReceivers(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            TeleportDestinations destinations = TeleportDestinations.get(player.getLevel());
            List<TeleportDestinationClientInfo> destinationList = new ArrayList<> (destinations.getValidDestinations(player.getCommandSenderWorld(), null));
            addDimensions(player.level, destinationList);
            addRfToolsDimensions(player.getCommandSenderWorld(), destinationList);
            PacketAllReceiversReady msg = new PacketAllReceiversReady(destinationList);
            RFToolsUtilityMessages.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }

    private void addDimensions(Level worldObj, List<TeleportDestinationClientInfo> destinationList) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (ServerLevel world : server.getAllLevels()) {
            ResourceKey<Level> id = world.dimension();
            TeleportDestination destination = new TeleportDestination(new BlockPos(0, 70, 0), id);
            destination.setName("Dimension: " + id.location().getPath());    // @todo 1.16 check
            TeleportDestinationClientInfo teleportDestinationClientInfo = new TeleportDestinationClientInfo(destination);
            String dimName = id.location().getPath();
            teleportDestinationClientInfo.setDimensionName(dimName);
            destinationList.add(teleportDestinationClientInfo);
        }
    }

    private void addRfToolsDimensions(Level world, List<TeleportDestinationClientInfo> destinationList) {
//        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
//        for (Map.Entry<Integer,DimensionDescriptor> me : dimensionManager.getDimensions().entrySet()) {
//            int id = me.getKey();
//            TeleportDestination destination = new TeleportDestination(new Coordinate(0, 70, 0), id);
//            destination.setName("RfTools Dim: " + id);
//            TeleportDestinationClientInfo teleportDestinationClientInfo = new TeleportDestinationClientInfo(destination);
//            destinationList.add(teleportDestinationClientInfo);
//        }
    }
}
