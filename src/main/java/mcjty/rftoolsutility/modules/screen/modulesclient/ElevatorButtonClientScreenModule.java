package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.DimensionId;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.modules.ElevatorButtonScreenModule;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import mcjty.rftoolsbase.api.screens.IClientScreenModule.TransformMode;

public class ElevatorButtonClientScreenModule implements IClientScreenModule<ElevatorButtonScreenModule.ModuleElevatorInfo> {

    public static final int LARGESIZE = 22;
    public static final int SMALLSIZE = 16;

    private int buttonColor = 0xffffff;
    private int currentLevelButtonColor = 0xffff00;
    private boolean vertical = false;
    private boolean large = false;
    private boolean lights = false;
    private boolean start1 = false;
    private String levels[] = new String[8];

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        if (vertical) {
            return large ? (LARGESIZE*5) : (SMALLSIZE *7);
        } else {
            return large ? LARGESIZE : SMALLSIZE;
        }
    }

    private int getDimension() {
        return large ? LARGESIZE : SMALLSIZE;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, ElevatorButtonScreenModule.ModuleElevatorInfo screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
//        GlStateManager.enableDepthTest();
//        GlStateManager.depthMask(false);

        if (screenData == null) {
            return;
        }
        int currentLevel = screenData.getLevel();
        int buttons = screenData.getMaxLevel();
        BlockPos pos = screenData.getPos();
        List<Integer> heights = screenData.getHeights();
        if (vertical) {
            renderButtonsVertical(matrixStack, buffer, renderHelper, currenty, currentLevel, buttons, pos, heights, renderInfo);
        } else {
            renderButtonsHorizontal(matrixStack, buffer, renderHelper, currenty, currentLevel, buttons, pos, heights, renderInfo);
        }
    }

    private void renderButtonsHorizontal(MatrixStack matrixStack, IRenderTypeBuffer buffer,
                                         IModuleRenderHelper renderHelper, int currenty, int currentLevel, int buttons,
                                         BlockPos pos,
                                         List<Integer> heights, ModuleRenderInfo renderInfo) {
        int xoffset = 5;
        int max = large ? 6 : 9;
        if (buttons > max) {
            buttons = max;
        }
        for (int i = 0; i < buttons; i++) {
            String text = getLevelText(i, pos, heights);
            boolean hasText = text != null;
            if (text == null) {
                text = String.valueOf(i + (start1 ? 1 : 0));
            }
            int col = i == currentLevel ? this.currentLevelButtonColor : this.buttonColor;
            int textoffset = large ? 3 : 0;
            int x = xoffset + 3 + textoffset;
            int y = currenty + 2 + textoffset;
            if (lights) {
                RenderHelper.drawBeveledBox(matrixStack, buffer, xoffset, currenty, xoffset + getDimension() - 4, currenty + getDimension() - 2, 0xffffffff, 0xffffffff, 0xff000000 + col,
                        renderInfo.getLightmapValue());
                if (hasText) {
                    renderHelper.renderTextTrimmed(matrixStack, buffer, x, y, 0xffffff, renderInfo, text, 480);
                }
            } else {
                RenderHelper.drawBeveledBox(matrixStack, buffer, xoffset, currenty, xoffset + getDimension() - 4, currenty + getDimension() - 2, 0xffeeeeee, 0xff333333, 0xff666666,
                        renderInfo.getLightmapValue());
                renderHelper.renderTextTrimmed(matrixStack, buffer, x, y, col, renderInfo, text, (getDimension() - 4) * 4);
            }
            xoffset += getDimension() - 2;
        }
    }

    private void renderButtonsVertical(MatrixStack matrixStack, IRenderTypeBuffer buffer,
                                       IModuleRenderHelper renderHelper, int currenty, int currentLevel, int buttons,
                                       BlockPos pos, List<Integer> heights, ModuleRenderInfo renderInfo) {
        int max = large ? 6 : 8;

        int y = currenty;
        int numcols = (buttons + max - 1) / max;
        int w = ElevatorButtonScreenModule.getColumnWidth(numcols);

        for (int i = 0; i < buttons; i++) {
            int xoffset;
            int level = buttons-i-1;
            int column = level / max;
            xoffset = 5 + (w+7) * column;

            String text = getLevelText(level, pos, heights);
            boolean hasText = text != null;
            if (text == null) {
                text = String.valueOf(level + (start1 ? 1 : 0));
            }

            int col = level == currentLevel ? this.currentLevelButtonColor : this.buttonColor;
            int textoffset = large ? 3 : 0;
            int x = xoffset + 3 + textoffset;
            int yy = y + 2 + textoffset;
            if (lights) {
                RenderHelper.drawBeveledBox(matrixStack, buffer, xoffset, y, xoffset + w, y + getDimension() - 2, 0xffffffff, 0xffffffff, 0xff000000 + col,
                        renderInfo.getLightmapValue());
                if (hasText) {
                    renderHelper.renderTextTrimmed(matrixStack, buffer, x, yy, 0xffffff, renderInfo, text, w * 4);
                }
            } else {
                RenderHelper.drawBeveledBox(matrixStack, buffer, xoffset, y, xoffset + w, y + getDimension() - 2, 0xffeeeeee, 0xff333333, 0xff666666,
                        renderInfo.getLightmapValue());
                renderHelper.renderTextTrimmed(matrixStack, buffer, x, yy, col, renderInfo, text, w * 4);
            }
            y += getDimension() - 2;
            if ((level % max) == 0) {
                y = currenty;
            }
        }
    }

    private String getLevelText(int level, BlockPos pos, List<Integer> heights) {
        if (hasLevelText(level)) {
            return levels[level];
        } else {
            if (level < heights.size()) {
                // @todo 1.14
//                BlockPos posY = ElevatorTileEntity.getPosAtY(pos, heights.get(level));
//                TileEntity te = Minecraft.getInstance().world.getTileEntity(posY);
//                if (te instanceof ElevatorTileEntity) {
//                    return ((ElevatorTileEntity) te).getName();
//                }
            }
        }
        return null;
    }

    private boolean hasLevelText(int i) {
        return i < levels.length && levels[i] != null && !levels[i].isEmpty();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {
    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionId dim, BlockPos pos) {
        if (tagCompound != null) {
            if (tagCompound.contains("buttonColor")) {
                buttonColor = tagCompound.getInt("buttonColor");
            } else {
                buttonColor = 0xffffff;
            }
            if (tagCompound.contains("curColor")) {
                currentLevelButtonColor = tagCompound.getInt("curColor");
            } else {
                currentLevelButtonColor = 0xffff00;
            }
            vertical = tagCompound.getBoolean("vertical");
            large = tagCompound.getBoolean("large");
            lights = tagCompound.getBoolean("lights");
            start1 = tagCompound.getBoolean("start1");
            for (int i = 0 ; i < levels.length ; i++) {
                if (tagCompound.contains("l" + i)) {
                    levels[i] = tagCompound.getString("l" + i);
                } else {
                    levels[i] = null;
                }
            }
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
