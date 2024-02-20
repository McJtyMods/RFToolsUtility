package mcjty.rftoolsutility.setup;

import mcjty.lib.network.IPayloadRegistrar;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nonnull;

public class RFToolsUtilityMessages {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = Networking.registrar(RFToolsUtility.MODID)
                .versioned("1.0")
                .optional();

        // Server side
        registrar.play(PacketGetAllReceivers.class, PacketGetAllReceivers::create, handler -> handler.server(PacketGetAllReceivers::handle));
        registrar.play(PacketSendRecipe.class, PacketSendRecipe::create, handler -> handler.server(PacketSendRecipe::handle));
        registrar.play(PacketGetScreenData.class, PacketGetScreenData::create, handler -> handler.server(PacketGetScreenData::handle));
        registrar.play(PacketModuleUpdate.class, PacketModuleUpdate::create, handler -> handler.server(PacketModuleUpdate::handle));
        registrar.play(PacketRemoveChannel.class, PacketRemoveChannel::create, handler -> handler.server(PacketRemoveChannel::handle));
        registrar.play(PacketSetRedstone.class, PacketSetRedstone::create, handler -> handler.server(PacketSetRedstone::handle));
        registrar.play(PacketSetChannelName.class, PacketSetChannelName::create, handler -> handler.server(PacketSetChannelName::handle));

        // Client side
        registrar.play(PacketAllReceiversReady.class, PacketAllReceiversReady::create, handler -> handler.client(PacketAllReceiversReady::handle));
        registrar.play(PacketTargetsReady.class, PacketTargetsReady::create, handler -> handler.client(PacketTargetsReady::handle));
        registrar.play(PacketSendBuffsToClient.class, PacketSendBuffsToClient::create, handler -> handler.client(PacketSendBuffsToClient::handle));
        registrar.play(PacketReturnScreenData.class, PacketReturnScreenData::create, handler -> handler.client(PacketReturnScreenData::handle));
        registrar.play(PacketReturnRfInRange.class, PacketReturnRfInRange::create, handler -> handler.client(PacketReturnRfInRange::handle));
        registrar.play(PacketSendRedstoneData.class, PacketSendRedstoneData::create, handler -> handler.client(PacketSendRedstoneData::handle));
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

    public static <T> void sendToPlayer(T packet, Player player) {
        registrar.getChannel().sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToServer(T packet) {
        registrar.getChannel().sendToServer(packet);
    }
}
