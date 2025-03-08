package mcjty.rftoolsutility.setup;

import mcjty.lib.network.Networking;
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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nonnull;

public class RFToolsUtilityMessages {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(RFToolsUtility.MODID)
                .versioned("1.0")
                .optional();

        // Server side
        registrar.playToServer(PacketGetAllReceivers.TYPE, PacketGetAllReceivers.CODEC, PacketGetAllReceivers::handle);
        registrar.playToServer(PacketSendRecipe.TYPE, PacketSendRecipe.CODEC, PacketSendRecipe::handle);
        registrar.playToServer(PacketGetScreenData.TYPE, PacketGetScreenData.CODEC, PacketGetScreenData::handle);
        registrar.playToServer(PacketModuleUpdate.TYPE, PacketModuleUpdate.CODEC, PacketModuleUpdate::handle);
        registrar.playToServer(PacketRemoveChannel.TYPE, PacketRemoveChannel.CODEC, PacketRemoveChannel::handle);
        registrar.playToServer(PacketSetRedstone.TYPE, PacketSetRedstone.CODEC, PacketSetRedstone::handle);
        registrar.playToServer(PacketSetChannelName.TYPE, PacketSetChannelName.CODEC, PacketSetChannelName::handle);

        // Client side
        registrar.playToClient(PacketAllReceiversReady.TYPE, PacketAllReceiversReady.CODEC, PacketAllReceiversReady::handle);
        registrar.playToClient(PacketTargetsReady.TYPE, PacketTargetsReady.CODEC, PacketTargetsReady::handle);
        registrar.playToClient(PacketSendBuffsToClient.TYPE, PacketSendBuffsToClient.CODEC, PacketSendBuffsToClient::handle);
        registrar.playToClient(PacketReturnScreenData.TYPE, PacketReturnScreenData.CODEC, PacketReturnScreenData::handle);
        registrar.playToClient(PacketReturnRfInRange.TYPE, PacketReturnRfInRange.CODEC, PacketReturnRfInRange::handle);
        registrar.playToClient(PacketSendRedstoneData.TYPE, PacketSendRedstoneData.CODEC, PacketSendRedstoneData::handle);
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
        PacketDistributor.sendToPlayer((ServerPlayer)player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}
