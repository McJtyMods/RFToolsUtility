package mcjty.rftoolsutility.network;

import mcjty.lib.network.*;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.jei.PacketSendRecipe;
import mcjty.rftoolsutility.modules.crafter.network.PacketCrafter;
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
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketCrafter.class, PacketCrafter::toBytes, PacketCrafter::new, PacketCrafter::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketGetAllReceivers.class, PacketGetAllReceivers::toBytes, PacketGetAllReceivers::new, PacketGetAllReceivers::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketSendRecipe.class, PacketSendRecipe::toBytes, PacketSendRecipe::new, PacketSendRecipe::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketGetPlayers.class, PacketGetPlayers::toBytes, PacketGetPlayers::new, PacketGetPlayers::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketGetReceivers.class, PacketGetReceivers::toBytes, PacketGetReceivers::new, PacketGetReceivers::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketGetTransmitters.class, PacketGetTransmitters::toBytes, PacketGetTransmitters::new, PacketGetTransmitters::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketGetScreenData.class, PacketGetScreenData::toBytes, PacketGetScreenData::new, PacketGetScreenData::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketModuleUpdate.class, PacketModuleUpdate::toBytes, PacketModuleUpdate::new, PacketModuleUpdate::handle);

        // Client side
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketAllReceiversReady.class, PacketAllReceiversReady::toBytes, PacketAllReceiversReady::new, PacketAllReceiversReady::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketTargetsReady.class, PacketTargetsReady::toBytes, PacketTargetsReady::new, PacketTargetsReady::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketPlayersReady.class, PacketPlayersReady::toBytes, PacketPlayersReady::new, PacketPlayersReady::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketTransmittersReady.class, PacketTransmittersReady::toBytes, PacketTransmittersReady::new, PacketTransmittersReady::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketReceiversReady.class, PacketReceiversReady::toBytes, PacketReceiversReady::new, PacketReceiversReady::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketSendBuffsToClient.class, PacketSendBuffsToClient::toBytes, PacketSendBuffsToClient::new, PacketSendBuffsToClient::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketReturnScreenData.class, PacketReturnScreenData::toBytes, PacketReturnScreenData::new, PacketReturnScreenData::handle);
        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketReturnRfInRange.class, PacketReturnRfInRange::toBytes, PacketReturnRfInRange::new, PacketReturnRfInRange::handle);

        PacketHandler.debugRegister("RFToolsUtility", net, id(), PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new,
                new ChannelBoundHandler<>(net, PacketRequestDataFromServer::handle));

        PacketHandler.registerStandardMessages("RFToolsUtility - standard", id(), net);
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY));
    }

    public static void sendToClient(PlayerEntity player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsUtility.MODID, command, argumentBuilder.build()), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToClient(PlayerEntity player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
