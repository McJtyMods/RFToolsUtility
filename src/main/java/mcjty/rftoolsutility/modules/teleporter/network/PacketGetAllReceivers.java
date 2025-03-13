package mcjty.rftoolsutility.modules.teleporter.network;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public record PacketGetAllReceivers() implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "getallreceivers");
    public static final Type<PacketGetAllReceivers> TYPE = new Type<>(ID);

    public static final PacketGetAllReceivers INSTANCE = new PacketGetAllReceivers();
    public static final StreamCodec<FriendlyByteBuf, PacketGetAllReceivers> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            TeleportDestinations destinations = TeleportDestinations.get(player.level());
            List<TeleportDestinationClientInfo> destinationList = new ArrayList<>(destinations.getValidDestinations(player.getCommandSenderWorld(), null));
            addDimensions(player.level(), destinationList);
            addRfToolsDimensions(player.getCommandSenderWorld(), destinationList);
            PacketAllReceiversReady msg = new PacketAllReceiversReady(destinationList);
            RFToolsUtilityMessages.sendToPlayer(msg, player);
        });
    }

    private void addDimensions(Level worldObj, List<TeleportDestinationClientInfo> destinationList) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (ServerLevel world : server.getAllLevels()) {
            ResourceKey<Level> id = world.dimension();
            TeleportDestination destination = new TeleportDestination(new BlockPos(0, 70, 0), id);
            destination = destination.withName("Dimension: " + id.location().getPath());    // @todo 1.16 check
            TeleportDestinationClientInfo teleportDestinationClientInfo = new TeleportDestinationClientInfo(destination);
            String dimName = id.location().getPath();
            teleportDestinationClientInfo = teleportDestinationClientInfo.withDimensionName(dimName);
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
