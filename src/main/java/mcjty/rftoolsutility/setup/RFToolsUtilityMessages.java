package mcjty.rftoolsutility.setup;

import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.RFToolsUtility;
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
        // @todo 1.21
//        registrar.play(PacketGetAllReceivers.class, PacketGetAllReceivers::create, handler -> handler.server(PacketGetAllReceivers::handle));
//        registrar.play(PacketSendRecipe.class, PacketSendRecipe::create, handler -> handler.server(PacketSendRecipe::handle));
//        registrar.play(PacketGetScreenData.class, PacketGetScreenData::create, handler -> handler.server(PacketGetScreenData::handle));
//        registrar.play(PacketModuleUpdate.class, PacketModuleUpdate::create, handler -> handler.server(PacketModuleUpdate::handle));
//        registrar.play(PacketRemoveChannel.class, PacketRemoveChannel::create, handler -> handler.server(PacketRemoveChannel::handle));
//        registrar.play(PacketSetRedstone.class, PacketSetRedstone::create, handler -> handler.server(PacketSetRedstone::handle));
//        registrar.play(PacketSetChannelName.class, PacketSetChannelName::create, handler -> handler.server(PacketSetChannelName::handle));

        // Client side
//        registrar.play(PacketAllReceiversReady.class, PacketAllReceiversReady::create, handler -> handler.client(PacketAllReceiversReady::handle));
//        registrar.play(PacketTargetsReady.class, PacketTargetsReady::create, handler -> handler.client(PacketTargetsReady::handle));
//        registrar.play(PacketSendBuffsToClient.class, PacketSendBuffsToClient::create, handler -> handler.client(PacketSendBuffsToClient::handle));
//        registrar.play(PacketReturnScreenData.class, PacketReturnScreenData::create, handler -> handler.client(PacketReturnScreenData::handle));
//        registrar.play(PacketReturnRfInRange.class, PacketReturnRfInRange::create, handler -> handler.client(PacketReturnRfInRange::handle));
//        registrar.play(PacketSendRedstoneData.class, PacketSendRedstoneData::create, handler -> handler.client(PacketSendRedstoneData::handle));
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
