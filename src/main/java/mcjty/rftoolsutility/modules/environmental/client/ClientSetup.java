package mcjty.rftoolsutility.modules.environmental.client;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get(), RenderType.translucent());
        MinecraftForge.EVENT_BUS.addListener(EnvironmentalRenderer::renderEnvironmentals);
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }

        event.addSprite(EnvironmentalRenderer.HALO);
    }

}
