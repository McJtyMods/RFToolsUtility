package mcjty.rftoolsutility.modules.screen.modulesclient;

import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ITextRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.items.modules.RedstoneModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.RedstoneScreenModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RedstoneClientScreenModule implements IClientScreenModule<IModuleDataInteger> {


    public RedstoneClientScreenModule() {
    }

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

    @Override
    public TransformMode getTransformMode(ItemStack moduleItem) {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight(ItemStack moduleItem) {
        return 10;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataInteger screenData, ModuleRenderInfo renderInfo) {
        // @todo 1.15
//        GlStateManager.disableLighting();
        RedstoneScreenModule data = RedstoneModuleItem.data(renderInfo.moduleStack);

        int xoffset;
        if (!data.getLine().isEmpty()) {
            labelCache.setup(data.getLine(), 160, renderInfo);
            labelCache.align(data.getAlign());
            labelCache.renderText(graphics, buffer, 0, currenty, data.getColor(), renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        String text;
        int col;
        if (screenData != null) {
            int power = screenData.get();
            boolean rs = power > 0;
            if (data.isAnalog()) {
                text = Integer.toString(power);
            } else {
                text = rs ? data.getYestext() : data.getNotext();
            }
            col = rs ? data.getYescolor() : data.getNocolor();
        } else {
            text = "<invalid>";
            col = 0xff0000;
        }
        renderHelper.renderText(graphics, buffer, xoffset, currenty, col, renderInfo, text);
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {

    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
