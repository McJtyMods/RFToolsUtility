package mcjty.rftoolsutility.setup;


import mcjty.rftoolsutility.modules.logic.client.DigitRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer.BLUEGLOW;
import static mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer.REDGLOW;
import static mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer.*;

public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }
        event.addSprite(BEAM_OK);
        event.addSprite(BEAM_WARN);
        event.addSprite(BEAM_UNKNOWN);
        for (ResourceLocation digit : DigitRenderer.DIGITS) {
            event.addSprite(digit);
        }

        event.addSprite(BLUEGLOW);
        event.addSprite(REDGLOW);
    }

//    @SubscribeEvent
//    public static void onModelBake(ModelBakeEvent event) {
//        TankBakedModel model = new TankBakedModel();
//        event.getModelRegistry().put(new ModelResourceLocation(new ResourceLocation(RFToolsUtility.MODID, "tank"), ""), model);
//    }
}
