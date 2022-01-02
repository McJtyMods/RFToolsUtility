package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import mcjty.rftoolsbase.api.screens.IClientScreenModule.TransformMode;

public class TextClientScreenModule implements IClientScreenModule<IModuleData> {
    private String line = "";
    private int color = 0xffffff;

    private ITextRenderHelper cache = new ScreenTextHelper();

    @Override
    public TransformMode getTransformMode() {
        return cache.isLarge() ? TransformMode.TEXTLARGE : TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return cache.isLarge() ? 20 : 10;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleData screenData, ModuleRenderInfo renderInfo) {
        cache.setup(line, 512, renderInfo);
        int y = cache.isLarge() ? (currenty / 2 + 1) : currenty;
        cache.renderText(matrixStack, buffer, 0, y, color, renderInfo);
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked) {

    }

    public void setLine(String line) {
        this.line = line;
        cache.setDirty();
    }

    public void setColor(int color) {
        this.color = color;
        cache.setDirty();
    }

    public void setLarge(boolean large) {
        cache.large(large);
        cache.setDirty();
    }

    @Override
    public void setupFromNBT(CompoundTag tagCompound, ResourceKey<Level> dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.contains("color")) {
                color = tagCompound.getInt("color");
            } else {
                color = 0xffffff;
            }
            cache.large(tagCompound.getBoolean("large"));
            if (tagCompound.contains("align")) {
                String alignment = tagCompound.getString("align");
                cache.align(TextAlign.get(alignment));
            } else {
                cache.align(TextAlign.ALIGN_LEFT);
            }
        }
    }

    @Override
    public boolean needsServerData() {
        return false;
    }
}
