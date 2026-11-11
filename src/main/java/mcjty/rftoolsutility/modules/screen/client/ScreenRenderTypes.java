package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = RFToolsUtility.MODID, value = Dist.CLIENT)
public final class ScreenRenderTypes {

    // Reuse vanilla's untextured, lightmapped pipeline, with quads instead of a triangle strip.
    private static final RenderPipeline FRAME_PIPELINE = RenderPipelines.LEASH.toBuilder()
            .withLocation(Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "pipeline/screen_frame"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS)
            .build();
    public static final RenderType FRAME = RenderType.create("rftoolsutility_screen_frame",
            RenderSetup.builder(FRAME_PIPELINE).useLightmap().createRenderSetup());

    private ScreenRenderTypes() {
    }

    @SubscribeEvent
    public static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(FRAME_PIPELINE);
    }
}
