package mcjty.rftoolsutility.modules.screen.modulesclient;

import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataBoolean;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.items.modules.ButtonModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.ButtonScreenModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ButtonClientScreenModule implements IClientScreenModule<IModuleDataBoolean> {
    private boolean activated = false;

    private final ITextRenderHelper labelCache = new ScreenTextHelper();
    private final ITextRenderHelper buttonCache = new ScreenTextHelper();

    public ButtonClientScreenModule() {
        labelCache.align(TextAlign.ALIGN_LEFT);
        buttonCache.setDirty();
    }

    public String getAlign() {
        return labelCache.getAlign().name();
    }

    public void setAlign(String align) {
        labelCache.align(TextAlign.get(align));
    }

    @Override
    public TransformMode getTransformMode(ItemStack moduleItem) {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight(ItemStack moduleItem) {
        return 14;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataBoolean screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
//        GlStateManager.enableDepthTest();
//        GlStateManager.depthMask(false);
        ButtonScreenModule data = ButtonModuleItem.data(renderInfo.moduleStack);

        int xoffset;
        int buttonWidth;
        if (!data.getLine().isEmpty()) {
            labelCache.setup(data.getLine(), 316, renderInfo);
            labelCache.align(data.getAlign());
            labelCache.renderText(graphics, buffer, 0, currenty + 2, data.getColor(), renderInfo);
            xoffset = 7 + 80;
            buttonWidth = 170;
        } else {
            xoffset = 7 + 5;
            buttonWidth = 490;
        }

        boolean act = false;
        if (data.isToggle()) {
            if (screenData != null) {
                act = screenData.get();
            }
        } else {
            act = activated;
        }

        RenderHelper.drawBeveledBox(graphics, buffer, xoffset - 5, currenty, 130 - 7, currenty + 12, act ? 0xff333333 : 0xffeeeeee, act ? 0xffeeeeee : 0xff333333, 0xff666666,
                renderInfo.getLightmapValue());
        buttonCache.setup(data.getButton(), buttonWidth, renderInfo);
        buttonCache.renderText(graphics, buffer, xoffset -10 + (act ? 1 : 0), currenty + 2, data.getButtonColor(), renderInfo);
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {
        int xoffset;
        ButtonScreenModule data = ButtonModuleItem.data(moduleStack);
        if (!data.getLine().isEmpty()) {
            xoffset = 80;
        } else {
            xoffset = 5;
        }
        activated = false;
        if (x >= xoffset) {
            activated = clicked;
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
