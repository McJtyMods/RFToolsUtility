package mcjty.rftoolsutility.modules.screen.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class ScreenRenderType extends RenderType {

    // Dummy
    public ScreenRenderType(String name, VertexFormat format, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable setup, Runnable clear) {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, setup, clear);
    }

    public static final RenderType QUADS_NOTEXTURE = get("quads_notexture",
            DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 2097152,
            RenderType.State.builder()
                    .layer(PROJECTION_LAYERING)
                    .texture(NO_TEXTURE)
                    .writeMask(COLOR_WRITE)
                    .build(false));
}
