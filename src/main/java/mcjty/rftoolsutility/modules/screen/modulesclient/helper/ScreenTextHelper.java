package mcjty.rftoolsutility.modules.screen.modulesclient.helper;

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
        if (truetype) {
            // @todo 1.14 truetype
            FontRenderer renderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(new ResourceLocation(RFToolsUtility.MODID, "ubuntu"));
            textx = large ? 3 : 7;
            text = renderer.trimStringToWidth(line, (large ? (width/2) : width)-textx);
//            int w = large ? 240 : 472;
            int w = large ? (int) (width / 2.13f) : (int) (width / 1.084f);
            switch (align) {
                case ALIGN_LEFT:
                    break;
                case ALIGN_CENTER:
                    textx += ((w -textx - renderer.getStringWidth(text)) / 2) / 4;
                    break;
                case ALIGN_RIGHT:
                    textx += (w -textx - renderer.getStringWidth(text)) / 4;
                    break;
            }
        } else {
            textx = large ? 4 : 7;
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            text = fontRenderer.trimStringToWidth(line, (large ? (width/8) : (width/4))-textx);
//            int w = large ? 58 : 115;
            int w = large ? (int) (width / 8.8f) : (int) (width / 4.45f);
            switch (align) {
                case ALIGN_LEFT:
                    break;
                case ALIGN_CENTER:
                    textx += (w - fontRenderer.getStringWidth(text)) / 2;
                    break;
                case ALIGN_RIGHT:
                    textx += w - fontRenderer.getStringWidth(text);
                    break;
            }
        }
    }

    @Override
    public void renderText(int x, int y, int color, ModuleRenderInfo renderInfo) {
        if (renderInfo.truetype) {
//            float r = (color >> 16 & 255) / 255.0f;
//            float g = (color >> 8 & 255) / 255.0f;
//            float b = (color & 255) / 255.0f;
            // @todo 1.14 truetype!
            FontRenderer renderer = Minecraft.getInstance().getFontResourceManager().getFontRenderer(new ResourceLocation(RFToolsUtility.MODID, "ubuntu"));
            renderer.drawString(text, textx + x, y, color);
//            renderer.drawString(textx + x, 128 - y, text, 0.25f, 0.25f, -512f-40f, r, g, b, 1.0f);
        } else {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            fontRenderer.drawString(text, textx + x, y, color);
        }
    }
}
