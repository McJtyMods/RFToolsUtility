package mcjty.rftoolsutility.modules.screen.modulesclient.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.rftoolsbase.api.screens.ITextRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class ScreenTextHelper implements ITextRenderHelper {

    private boolean large = false;
    private TextAlign align = TextAlign.ALIGN_LEFT;

    private boolean dirty = true;
    private int textx;
    private String text;
    private boolean truetype = false;

    public int getTextx() {
        return textx;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    @Override
    public boolean isLarge() {
        return large;
    }

    @Override
    public ITextRenderHelper large(boolean large) {
        dirty = true;
        this.large = large;
        return this;
    }

    @Override
    public TextAlign getAlign() {
        return align;
    }

    @Override
    public ITextRenderHelper align(TextAlign align) {
        dirty = true;
        this.align = align;
        return this;
    }

    @Override
    public void setup(String line, int width, ModuleRenderInfo renderInfo) {
        if ((!dirty) && truetype == renderInfo.truetype) {
            return;
        }
        dirty = false;
        truetype = renderInfo.truetype;
        FontRenderer renderer = getFontRenderer(truetype);

        textx = large ? 4 : 7;
        text = renderer.trimStringToWidth(line, (large ? (width/8) : (width/4))-textx);
//            int w = large ? 58 : 115;
        int w = large ? (int) (width / 8.8f) : (int) (width / 4.45f);
        switch (align) {
            case ALIGN_LEFT:
                break;
            case ALIGN_CENTER:
                textx += (w - renderer.getStringWidth(text)) / 2;
                break;
            case ALIGN_RIGHT:
                textx += w - renderer.getStringWidth(text);
                break;
        }
    }

    // @todo truetype
    public static FontRenderer getFontRenderer(boolean truetype) {
        FontRenderer renderer;
        if (truetype) {
            renderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(new ResourceLocation(RFToolsUtility.MODID, "ubuntu"));
        } else {
            renderer = Minecraft.getInstance().fontRenderer;
        }
        return renderer;
    }

    @Override
    public void renderText(int x, int y, int color, ModuleRenderInfo renderInfo) {
        FontRenderer renderer = getFontRenderer(truetype);
        if (truetype) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(.5f, .5f, .5f);
            renderer.drawString(text, textx + x*2, y*2, color);
            GlStateManager.popMatrix();
        } else {
            renderer.drawString(text, textx + x, y, color);
        }
    }
}
