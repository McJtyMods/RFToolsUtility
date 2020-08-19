package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

public class ClockClientScreenModule implements IClientScreenModule<IModuleData> {
    private int color = 0xffffff;
    private String line = "";
    private boolean large = false;

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return large ? TransformMode.TEXTLARGE : TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return large ? 20 : 10;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, IModuleData screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
        Minecraft minecraft = Minecraft.getInstance();

        final long time = minecraft.world.getGameTime();
        long hour = (time / 1000 + 6) % 24;
        final long minute = (time % 1000) * 60 / 1000;
        String timeString = String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);

        int xoffset;
        int y;
        if (large) {
            xoffset = 4;
            y = currenty / 2 + 1;
        } else {
            xoffset = 7;
            y = currenty;
        }

        renderHelper.renderText(matrixStack, buffer, xoffset, y, color, renderInfo, line + " " + timeString);
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionId dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.contains("color")) {
                color = tagCompound.getInt("color");
            } else {
                color = 0xffffff;
            }
            large = tagCompound.getBoolean("large");
        }
    }

    @Override
    public boolean needsServerData() {
        return false;
    }
}
