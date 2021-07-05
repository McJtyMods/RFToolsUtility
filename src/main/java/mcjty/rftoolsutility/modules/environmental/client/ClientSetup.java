package mcjty.rftoolsutility.modules.environmental.client;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get(), RenderType.translucent());
    }
}
