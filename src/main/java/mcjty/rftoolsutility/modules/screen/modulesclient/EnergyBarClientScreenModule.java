package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.items.modules.EnergyModuleItem;
import mcjty.rftoolsutility.modules.screen.modules.EnergyBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenLevelHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnergyBarClientScreenModule implements IClientScreenModule<IModuleDataContents> {

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

    public EnergyBarClientScreenModule() {
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
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataContents screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();

        EnergyBarScreenModule data = EnergyModuleItem.data(renderInfo.moduleStack);

        int xoffset;
        if (!data.getLine().isEmpty()) {
            labelCache.setup(data.getLine(), 160, renderInfo);
            labelCache.align(data.getAlign());
            labelCache.renderText(graphics, buffer, 0, currenty, data.getColor(), renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        if (BlockPosTools.isValid(data.getPos().pos())) {
            data.getRfRenderer().render(graphics, buffer, xoffset, currenty, screenData, renderInfo);
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
