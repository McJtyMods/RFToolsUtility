package mcjty.rftoolsutility.setup;

import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.network.PayloadContext;
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
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public class RFToolsUtilityMessages {

    public static void registerMessages() {

        // Server side
        PayloadTypeRegistry.serverboundPlay().register(PacketGetAllReceivers.TYPE, PacketGetAllReceivers.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGetAllReceivers.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));
        PayloadTypeRegistry.serverboundPlay().register(PacketSendRecipe.TYPE, PacketSendRecipe.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketSendRecipe.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));
        PayloadTypeRegistry.serverboundPlay().register(PacketGetScreenData.TYPE, PacketGetScreenData.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGetScreenData.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));
        PayloadTypeRegistry.serverboundPlay().register(PacketModuleUpdate.TYPE, PacketModuleUpdate.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketModuleUpdate.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));
        PayloadTypeRegistry.serverboundPlay().register(PacketRemoveChannel.TYPE, PacketRemoveChannel.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketRemoveChannel.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));
        PayloadTypeRegistry.serverboundPlay().register(PacketSetRedstone.TYPE, PacketSetRedstone.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketSetRedstone.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));
        PayloadTypeRegistry.serverboundPlay().register(PacketSetChannelName.TYPE, PacketSetChannelName.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketSetChannelName.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.server())));

        // Client side
        PayloadTypeRegistry.clientboundPlay().register(PacketAllReceiversReady.TYPE, PacketAllReceiversReady.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketTargetsReady.TYPE, PacketTargetsReady.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketSendBuffsToClient.TYPE, PacketSendBuffsToClient.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketReturnScreenData.TYPE, PacketReturnScreenData.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketReturnRfInRange.TYPE, PacketReturnRfInRange.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketSendRedstoneData.TYPE, PacketSendRedstoneData.CODEC);
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        Networking.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        Networking.sendToServer(new PacketSendServerCommand(RFToolsUtility.MODID, command, TypedMap.EMPTY));
    }

    public static void sendToClient(Player player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        Networking.sendToPlayer(new PacketSendClientCommand(RFToolsUtility.MODID, command, argumentBuilder.build()), player);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        Networking.sendToPlayer(packet, player);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        Networking.sendToServer(packet);
    }
}
