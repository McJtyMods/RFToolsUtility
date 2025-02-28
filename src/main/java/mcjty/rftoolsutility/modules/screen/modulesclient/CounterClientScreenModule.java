package mcjty.rftoolsutility.modules.screen.modulesclient;

import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ITextRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.items.modules.CounterModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.CounterScreenModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CounterClientScreenModule implements IClientScreenModule<IModuleDataInteger> {

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

    public CounterClientScreenModule() {
    }

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

        CounterScreenModule data = CounterModuleItem.data(renderInfo.moduleStack);

        int xoffset;
        if (!data.getLine().isEmpty()) {
            labelCache.setup(data.getLine(), 160, renderInfo);
            labelCache.align(data.getAlign());
            labelCache.renderText(graphics, buffer, 0, currenty, data.getColor(), renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        if (!BlockPosTools.INVALID.equals(data.getPos().pos())) {
            int current;
            if (screenData != null) {
                current = screenData.get();
            } else {
                current = 0;
            }
            String output = renderHelper.format(String.valueOf(current), data.getFormat());
            renderHelper.renderText(graphics, buffer, xoffset, currenty, data.getCntcolor(), renderInfo, output);
        } else {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, 0xff0000, renderInfo, "<invalid>");
        }
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
