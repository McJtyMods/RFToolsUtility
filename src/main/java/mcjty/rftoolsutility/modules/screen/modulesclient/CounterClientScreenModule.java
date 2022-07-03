package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class CounterClientScreenModule implements IClientScreenModule<IModuleDataInteger> {

    private String line = "";
    private int color = 0xffffff;
    private int cntcolor = 0xffffff;
    protected RegistryKey<World> dim = World.OVERWORLD;
    private FormatStyle format = FormatStyle.MODE_FULL;
    protected BlockPos coordinate = BlockPosTools.INVALID;

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

        if (!BlockPosTools.INVALID.equals(coordinate)) {
            int current;
            if (screenData != null) {
                current = screenData.get();
            } else {
                current = 0;
            }
            String output = renderHelper.format(String.valueOf(current), format);
            renderHelper.renderText(matrixStack, buffer, xoffset, currenty, cntcolor, renderInfo, output);
        } else {
            renderHelper.renderText(matrixStack, buffer, xoffset, currenty, 0xff0000, renderInfo, "<invalid>");
        }
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.contains("color")) {
                color = tagCompound.getInt("color");
            } else {
                color = 0xffffff;
            }
            if (tagCompound.contains("cntcolor")) {
                cntcolor = tagCompound.getInt("cntcolor");
            } else {
                cntcolor = 0xffffff;
            }
            if (tagCompound.contains("align")) {
                String alignment = tagCompound.getString("align");
                labelCache.align(TextAlign.get(alignment));
            } else {
                labelCache.align(TextAlign.ALIGN_LEFT);
            }

            format = FormatStyle.values()[tagCompound.getInt("format")];

            setupCoordinateFromNBT(tagCompound, dim, pos);
        }
    }

    protected void setupCoordinateFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        coordinate = BlockPosTools.INVALID;
        if (tagCompound.contains("monitorx")) {
            this.dim = LevelTools.getId(tagCompound.getString("monitordim"));
            if (Objects.equals(dim, this.dim)) {
                BlockPos c = new BlockPos(tagCompound.getInt("monitorx"), tagCompound.getInt("monitory"), tagCompound.getInt("monitorz"));
                int dx = Math.abs(c.getX() - pos.getX());
                int dy = Math.abs(c.getY() - pos.getY());
                int dz = Math.abs(c.getZ() - pos.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                    coordinate = c;
                }
            }
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
