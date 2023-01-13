package mcjty.rftoolsutility.modules.environmental.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;

public class ClientSetup {
    public static void initClient() {
        MinecraftForge.EVENT_BUS.addListener(EnvironmentalRenderer::renderEnvironmentals);
    }

    public static List<ResourceLocation> onTextureStitch() {
        return Collections.singletonList(EnvironmentalRenderer.HALO);
    }
}
