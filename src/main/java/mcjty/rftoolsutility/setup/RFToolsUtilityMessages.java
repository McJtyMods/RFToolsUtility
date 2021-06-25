package mcjty.rftoolsutility.setup;

import mcjty.lib.network.*;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.jei.PacketSendRecipe;
import mcjty.rftoolsutility.modules.crafter.network.PacketCrafter;
import mcjty.rftoolsutility.modules.logic.network.PacketRemoveChannel;
import mcjty.rftoolsutility.modules.logic.network.PacketSendRedstoneData;
import mcjty.rftoolsutility.modules.logic.network.PacketSetChannelName;
import mcjty.rftoolsutility.modules.logic.network.PacketSetRedstone;
import mcjty.rftoolsutility.modules.screen.network.PacketGetScreenData;
import mcjty.rftoolsutility.modules.screen.network.PacketModuleUpdate;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnRfInRange;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnScreenData;
import mcjty.rftoolsutility.modules.teleporter.network.*;
import mcjty.rftoolsutility.playerprops.PacketSendBuffsToClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

public class RFToolsUtilityMessages {
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RFToolsUtility.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Server side
        net.registerMessage(id(), PacketCrafter.class, PacketCrafter::toBytes, PacketCrafter::new, PacketCrafter::handle);
        net.registerMessage(id(), PacketGetAllReceivers.class, PacketGetAllReceivers::toBytes, PacketGetAllReceivers::new, PacketGetAllReceivers::handle);
        net.registerMessage(id(), PacketSendRecipe.class, PacketSendRecipe::toBytes, PacketSendRecipe::new, PacketSendRecipe::handle);
        net.registerMessage(id(), PacketGetPlayers.class, PacketGetPlayers::toBytes, PacketGetPlayers::new, PacketGetPlayers::handle);
        net.registerMessage(id(), PacketGetReceivers.class, PacketGetReceivers::toBytes, PacketGetReceivers::new, PacketGetReceivers::handle);
        net.registerMessage(id(), PacketGetTransmitters.class, PacketGetTransmitters::toBytes, PacketGetTransmitters::new, PacketGetTransmitters::handle);
        net.registerMessage(id(), PacketGetScreenData.class, PacketGetScreenData::toBytes, PacketGetScreenData::new, PacketGetScreenData::handle);
        net.registerMessage(id(), PacketModuleUpdate.class, PacketModuleUpdate::toBytes, PacketModuleUpdate::new, PacketModuleUpdate::handle);
        net.registerMessage(id(), PacketRemoveChannel.class, PacketRemoveChannel::toBytes, PacketRemoveChannel::new, PacketRemoveChannel::handle);
        net.registerMessage(id(), PacketSetRedstone.class, PacketSetRedstone::toBytes, PacketSetRedstone::new, PacketSetRedstone::handle);
        net.registerMessage(id(), PacketSetChannelName.class, PacketSetChannelName::toBytes, PacketSetChannelName::new, PacketSetChannelName::handle);

        // Client side
        net.registerMessage(id(), PacketAllReceiversReady.class, PacketAllReceiversReady::toBytes, PacketAllReceiversReady::new, PacketAllReceiversReady::handle);
        net.registerMessage(id(), PacketTargetsReady.class, PacketTargetsReady::toBytes, PacketTargetsReady::new, PacketTargetsReady::handle);
        net.registerMessage(id(), PacketPlayersReady.class, PacketPlayersReady::toBytes, PacketPlayersReady::new, PacketPlayersReady::handle);
        net.registerMessage(id(), PacketTransmittersReady.class, PacketTransmittersReady::toBytes, PacketTransmittersReady::new, PacketTransmittersReady::handle);
        net.registerMessage(id(), PacketReceiversReady.class, PacketReceiversReady::toBytes, PacketReceiversReady::new, PacketReceiversReady::handle);
        net.registerMessage(id(), PacketSendBuffsToClient.class, PacketSendBuffsToClient::toBytes, PacketSendBuffsToClient::new, PacketSendBuffsToClient::handle);
        net.registerMessage(id(), PacketReturnScreenData.class, PacketReturnScreenData::toBytes, PacketReturnScreenData::new, PacketReturnScreenData::handle);
        net.registerMessage(id(), PacketReturnRfInRange.class, PacketReturnRfInRange::toBytes, PacketReturnRfInRange::new, PacketReturnRfInRange::handle);
        net.registerMessage(id(), PacketSendRedstoneData.class, PacketSendRedstoneData::toBytes, PacketSendRedstoneData::new, PacketSendRedstoneData::handle);

        net.registerMessage(id(), PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new, new ChannelBoundHandler<>(net, PacketRequestDataFromServer::handle));

        PacketHandler.registerStandardMessages(id(), net);
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY));
    }

    public static void sendToClient(PlayerEntity player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsUtility.MODID, command, argumentBuilder.build()), ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToClient(PlayerEntity player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY), ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
