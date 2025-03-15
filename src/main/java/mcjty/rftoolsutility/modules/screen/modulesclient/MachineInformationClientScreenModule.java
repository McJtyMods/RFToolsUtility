package mcjty.rftoolsutility.modules.screen.modulesclient;

import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ITextRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleDataString;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.items.modules.MachineInformationModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.MachineInformationScreenModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MachineInformationClientScreenModule implements IClientScreenModule<IModuleDataString> {

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

    public MachineInformationClientScreenModule() {
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
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataString screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
        MachineInformationScreenModule data = MachineInformationModuleItem.data(renderInfo.moduleStack);
        int xoffset;
        String line = screenData.get();
        if (!line.isEmpty()) {
            labelCache.setup(line, 160, renderInfo);
            labelCache.renderText(graphics, buffer,0, currenty, data.getLabcolor(), renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        if ((BlockPosTools.isValid(data.getPos().pos())) && screenData != null) {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, data.getTxtcolor(), renderInfo, line);
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
