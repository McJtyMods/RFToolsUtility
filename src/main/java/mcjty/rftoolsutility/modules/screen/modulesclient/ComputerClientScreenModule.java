package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.modules.ComputerScreenModule;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ComputerClientScreenModule implements IClientScreenModule<ComputerScreenModule.ModuleComputerInfo> {

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty,
                       ComputerScreenModule.ModuleComputerInfo screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
        if (screenData != null) {
            int x = 7;
            for (ComputerScreenModule.ColoredText ct : screenData) {
                fontRenderer.renderString(ct.getText(), x, currenty, ct.getColor(), false, matrixStack.getLast().getMatrix(), buffer, false, 0, 140);
                x += fontRenderer.getStringWidth(ct.getText());
            }
        }
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionType dim, BlockPos pos) {
    }

    @Override
    public boolean needsServerData() {
        return true;
    }

}
