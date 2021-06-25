package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcjty.rftoolsbase.api.screens.IClientScreenModule.TransformMode;

public class RedstoneClientScreenModule implements IClientScreenModule<IModuleDataInteger> {

    private String line = "";
    private String yestext = "on";
    private String notext = "off";
    private int color = 0xffffff;
    private int yescolor = 0xffffff;
    private int nocolor = 0xffffff;
    private int dim = 0;
    private boolean analog = false;

    private ITextRenderHelper labelCache = new ScreenTextHelper();

    @Override
    public TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, IModuleDataInteger screenData, ModuleRenderInfo renderInfo) {
        // @todo 1.15
//        GlStateManager.disableLighting();

        int xoffset;
        if (!line.isEmpty()) {
            labelCache.setup(line, 160, renderInfo);
            labelCache.renderText(matrixStack, buffer, 0, currenty, color, renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        String text;
        int col;
        if (screenData != null) {
            int power = screenData.get();
            boolean rs = power > 0;
            if(analog) {
                text = Integer.toString(power);
            } else {
                text = rs ? yestext : notext;
            }
            col = rs ? yescolor : nocolor;
        } else {
            text = "<invalid>";
            col = 0xff0000;
        }
        renderHelper.renderText(matrixStack, buffer, xoffset, currenty, col, renderInfo, text);
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionId dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.contains("yestext")) {
                yestext = tagCompound.getString("yestext");
            }
            if (tagCompound.contains("notext")) {
                notext = tagCompound.getString("notext");
            }
            if (tagCompound.contains("color")) {
                color = tagCompound.getInt("color");
            } else {
                color = 0xffffff;
            }
            if (tagCompound.contains("yescolor")) {
                yescolor = tagCompound.getInt("yescolor");
            } else {
                yescolor = 0xffffff;
            }
            if (tagCompound.contains("nocolor")) {
                nocolor = tagCompound.getInt("nocolor");
            } else {
                nocolor = 0xffffff;
            }
            if (tagCompound.contains("align")) {
                String alignment = tagCompound.getString("align");
                labelCache.align(TextAlign.get(alignment));
            } else {
                labelCache.align(TextAlign.ALIGN_LEFT);
            }
            analog = tagCompound.getBoolean("analog");
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
