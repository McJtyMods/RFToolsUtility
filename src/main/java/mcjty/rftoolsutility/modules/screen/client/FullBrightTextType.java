package mcjty.rftoolsutility.modules.screen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;

public class FullBrightTextType extends RenderType {

    private final RenderType wrapped;

    public FullBrightTextType(String name, RenderType wrapped) {
        super(name, wrapped.getVertexFormat(), wrapped.getDrawMode(), wrapped.getBufferSize(), false, true, wrapped::setupRenderState, wrapped::clearRenderState);
        this.wrapped = wrapped;
    }

    @Override
    public void setupRenderState() {
        super.setupRenderState();
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
    }

    @Override
    public void clearRenderState() {
        super.clearRenderState();
        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
    }
}
