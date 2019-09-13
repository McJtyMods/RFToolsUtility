package mcjty.rftoolsutility.network;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.blocks.crafter.CrafterConfiguration;
import mcjty.rftoolsutility.blocks.crafter.PacketCrafter;
import mcjty.rftoolsutility.blocks.teleporter.PacketGetReceivers;
import mcjty.rftoolsutility.blocks.teleporter.PacketGetTransmitters;
import mcjty.rftoolsutility.blocks.teleporter.PacketReceiversReady;
import mcjty.rftoolsutility.blocks.teleporter.PacketTransmittersReady;
import mcjty.rftoolsutility.compat.jei.PacketSendRecipe;
import mcjty.rftoolsutility.items.teleportprobe.PacketAllReceiversReady;
import mcjty.rftoolsutility.items.teleportprobe.PacketGetAllReceivers;
import mcjty.rftoolsutility.items.teleportprobe.PacketTargetsReady;
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

    private static int id() {
        return PacketHandler.nextPacketID();
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
        if (CrafterConfiguration.enabled.get()) {
            net.registerMessage(id(), PacketCrafter.class, PacketCrafter::toBytes, PacketCrafter::new, PacketCrafter::handle);
        }
        net.registerMessage(id(), PacketGetAllReceivers.class, PacketGetAllReceivers::toBytes, PacketGetAllReceivers::new, PacketGetAllReceivers::handle);
        net.registerMessage(id(), PacketSendRecipe.class, PacketSendRecipe::toBytes, PacketSendRecipe::new, PacketSendRecipe::handle);
        net.registerMessage(id(), PacketGetPlayers.class, PacketGetPlayers::toBytes, PacketGetPlayers::new, PacketGetPlayers::handle);
        net.registerMessage(id(), PacketGetReceivers.class, PacketGetReceivers::toBytes, PacketGetReceivers::new, PacketGetReceivers::handle);
        net.registerMessage(id(), PacketGetTransmitters.class, PacketGetTransmitters::toBytes, PacketGetTransmitters::new, PacketGetTransmitters::handle);

        // Client side
        net.registerMessage(id(), PacketAllReceiversReady.class, PacketAllReceiversReady::toBytes, PacketAllReceiversReady::new, PacketAllReceiversReady::handle);
        net.registerMessage(id(), PacketTargetsReady.class, PacketTargetsReady::toBytes, PacketTargetsReady::new, PacketTargetsReady::handle);
        net.registerMessage(id(), PacketPlayersReady.class, PacketPlayersReady::toBytes, PacketPlayersReady::new, PacketPlayersReady::handle);
        net.registerMessage(id(), PacketTransmittersReady.class, PacketTransmittersReady::toBytes, PacketTransmittersReady::new, PacketTransmittersReady::handle);
        net.registerMessage(id(), PacketReceiversReady.class, PacketReceiversReady::toBytes, PacketReceiversReady::new, PacketReceiversReady::handle);
        net.registerMessage(id(), PacketSendBuffsToClient.class, PacketSendBuffsToClient::toBytes, PacketSendBuffsToClient::new, PacketSendBuffsToClient::handle);

        PacketHandler.registerStandardMessages(net);
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
