package mcjty.rftoolsutility.setup;


import mcjty.rftoolsutility.client.RenderGameOverlayEventHandler;
import mcjty.rftoolsutility.modules.logic.client.DigitRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import static mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer.BLUEGLOW;
import static mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer.REDGLOW;
import static mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer.*;

public class ClientSetup {

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
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

    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText evt) {
        RenderGameOverlayEventHandler.onRender(evt);
    }

}
