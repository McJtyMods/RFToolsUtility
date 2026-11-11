package mcjty.rftoolsutility.setup;

import mcjty.lib.network.PayloadContext;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.client.RenderGameOverlayEventHandler;
import mcjty.rftoolsutility.client.UtilityItemProperties;
import mcjty.rftoolsutility.modules.logic.network.PacketSendRedstoneData;
import mcjty.rftoolsutility.modules.screen.client.ScreenRenderTypes;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnRfInRange;
import mcjty.rftoolsutility.modules.screen.network.PacketReturnScreenData;
import mcjty.rftoolsutility.modules.tank.client.TankModelLoader;
import mcjty.rftoolsutility.modules.teleporter.network.PacketAllReceiversReady;
import mcjty.rftoolsutility.modules.teleporter.network.PacketTargetsReady;
import mcjty.rftoolsutility.playerprops.PacketSendBuffsToClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RFToolsUtility.instance.modules.initClient();
        UtilityItemProperties.register();
        ScreenRenderTypes.registerPipelines();
        TankModelLoader.register();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "buffs"),
                (graphics, delta) -> RenderGameOverlayEventHandler.onRender(graphics));
        ClientPlayNetworking.registerGlobalReceiver(PacketAllReceiversReady.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.client())));
        ClientPlayNetworking.registerGlobalReceiver(PacketTargetsReady.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.client())));
        ClientPlayNetworking.registerGlobalReceiver(PacketSendBuffsToClient.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.client())));
        ClientPlayNetworking.registerGlobalReceiver(PacketReturnScreenData.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.client())));
        ClientPlayNetworking.registerGlobalReceiver(PacketReturnRfInRange.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.client())));
        ClientPlayNetworking.registerGlobalReceiver(PacketSendRedstoneData.TYPE,
                (packet, context) -> packet.handle(new PayloadContext(context.player(), context.client())));
    }
}
