package mcjty.rftoolsutility.modules.screen.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public final class ScreenRenderTypes {

    // Reuse vanilla's untextured, lightmapped pipeline, with quads instead of a triangle strip.
    private static final RenderPipeline FRAME_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withVertexShader("core/rendertype_leash")
            .withFragmentShader("core/rendertype_leash")
            .withSampler("Sampler2")
            .withCull(false)
            .withLocation(Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "pipeline/screen_frame"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS)
            .build();
    public static final RenderType FRAME = RenderType.create("rftoolsutility_screen_frame",
            RenderSetup.builder(FRAME_PIPELINE).useLightmap().createRenderSetup());

    private ScreenRenderTypes() {
    }

    public static void registerPipelines() {
        RenderPipelines.register(FRAME_PIPELINE);
    }
}
