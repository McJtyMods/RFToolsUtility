package mcjty.rftoolsutility.modules.screen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;

public class FullBrightTextType extends RenderType {

    public FullBrightTextType(String name, RenderType wrapped) {
        super(name, wrapped.format(), wrapped.mode(), wrapped.bufferSize(), false, true, wrapped::setupRenderState, wrapped::clearRenderState);
    }

    @Override
    public void setupRenderState() {
        super.setupRenderState();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
    }

    @Override
    public void clearRenderState() {
        super.clearRenderState();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
    }
}
