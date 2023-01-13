package mcjty.rftoolsutility.setup;


import mcjty.rftoolsutility.client.RenderGameOverlayEventHandler;
import mcjty.rftoolsutility.modules.logic.client.DigitRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer.BLUEGLOW;
import static mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer.REDGLOW;
import static mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer.*;

public class ClientSetup {

    public static List<ResourceLocation> onTextureStitch() {
        List<ResourceLocation> result = new ArrayList<>();
        result.add(BEAM_OK);
        result.add(BEAM_WARN);
        result.add(BEAM_UNKNOWN);
        Collections.addAll(result, DigitRenderer.DIGITS);
        result.add(BLUEGLOW);
        result.add(REDGLOW);
        return result;
    }

    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText evt) {
        RenderGameOverlayEventHandler.onRender(evt);
    }

}
