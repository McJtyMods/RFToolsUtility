package mcjty.rftoolsutility.modules.screen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;

public class FullBrightTextType extends RenderType {

    private final RenderType wrapped;

    public FullBrightTextType(String name, RenderType wrapped) {
        super(name, wrapped.getVertexFormat(), wrapped.getGlMode(), wrapped.defaultBufferSize(), false, true, wrapped::enable, wrapped::disable);
        this.wrapped = wrapped;
    }



    @Override
    public void enable() {
        super.enable();
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
    }

    @Override
    public void disable() {
        super.disable();
        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
    }
}
