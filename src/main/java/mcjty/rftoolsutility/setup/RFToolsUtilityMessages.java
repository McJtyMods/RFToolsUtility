package mcjty.rftoolsutility.setup;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.jei.PacketSendRecipe;
import mcjty.rftoolsutility.modules.logic.network.PacketRemoveChannel;
import mcjty.rftoolsutility.modules.logic.network.PacketSendRedstoneData;
import mcjty.rftoolsutility.modules.logic.network.PacketSetChannelName;
import mcjty.rftoolsutility.modules.logic.network.PacketSetRedstone;
import mcjty.rftoolsutility.modules.screen.network.PacketGetScreenData;
import mcjty.rftoolsutility.modules.screen.network.PacketModuleUpdate;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnRfInRange;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnScreenData;
import mcjty.rftoolsutility.modules.teleporter.network.PacketAllReceiversReady;
import mcjty.rftoolsutility.modules.teleporter.network.PacketGetAllReceivers;
import mcjty.rftoolsutility.modules.teleporter.network.PacketTargetsReady;
import mcjty.rftoolsutility.playerprops.PacketSendBuffsToClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

import static mcjty.lib.network.PlayPayloadContext.wrap;

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
        net.registerMessage(id(), PacketGetAllReceivers.class, PacketGetAllReceivers::write, PacketGetAllReceivers::create, wrap(PacketGetAllReceivers::handle));
        net.registerMessage(id(), PacketSendRecipe.class, PacketSendRecipe::write, PacketSendRecipe::create, wrap(PacketSendRecipe::handle));
        net.registerMessage(id(), PacketGetScreenData.class, PacketGetScreenData::write, PacketGetScreenData::create, wrap(PacketGetScreenData::handle));
        net.registerMessage(id(), PacketModuleUpdate.class, PacketModuleUpdate::write, PacketModuleUpdate::create, wrap(PacketModuleUpdate::handle));
        net.registerMessage(id(), PacketRemoveChannel.class, PacketRemoveChannel::write, PacketRemoveChannel::create, wrap(PacketRemoveChannel::handle));
        net.registerMessage(id(), PacketSetRedstone.class, PacketSetRedstone::write, PacketSetRedstone::create, wrap(PacketSetRedstone::handle));
        net.registerMessage(id(), PacketSetChannelName.class, PacketSetChannelName::write, PacketSetChannelName::create, wrap(PacketSetChannelName::handle));

        // Client side
        net.registerMessage(id(), PacketAllReceiversReady.class, PacketAllReceiversReady::write, PacketAllReceiversReady::create, wrap(PacketAllReceiversReady::handle));
        net.registerMessage(id(), PacketTargetsReady.class, PacketTargetsReady::write, PacketTargetsReady::create, wrap(PacketTargetsReady::handle));
        net.registerMessage(id(), PacketSendBuffsToClient.class, PacketSendBuffsToClient::write, PacketSendBuffsToClient::create, wrap(PacketSendBuffsToClient::handle));
        net.registerMessage(id(), PacketReturnScreenData.class, PacketReturnScreenData::write, PacketReturnScreenData::create, wrap(PacketReturnScreenData::handle));
        net.registerMessage(id(), PacketReturnRfInRange.class, PacketReturnRfInRange::write, PacketReturnRfInRange::create, wrap(PacketReturnRfInRange::handle));
        net.registerMessage(id(), PacketSendRedstoneData.class, PacketSendRedstoneData::write, PacketSendRedstoneData::create, wrap(PacketSendRedstoneData::handle));

        PacketRequestDataFromServer.register(net, id());

        PacketHandler.registerStandardMessages(id(), net);
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY));
    }

    public static void sendToClient(Player player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsUtility.MODID, command, argumentBuilder.build()), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToClient(Player player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        INSTANCE.sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
