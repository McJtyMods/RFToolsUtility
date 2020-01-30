package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.client.ScreenRenderType;
import mcjty.rftoolsutility.modules.screen.modules.StorageControlScreenModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class StorageControlClientScreenModule implements IClientScreenModule<StorageControlScreenModule.ModuleDataStacks> {
    private ItemStackList stacks = ItemStackList.create(9);

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return TransformMode.ITEM;
    }

    @Override
    public int getHeight() {
        return 114;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, StorageControlScreenModule.ModuleDataStacks screenData, ModuleRenderInfo renderInfo) {
        if (screenData == null) {
            return;
        }

        if (renderInfo.hitx >= 0) {
//            GlStateManager.disableLighting();
            matrixStack.push();
            matrixStack.translate(-0.5F, 0.5F, 0.07F);
            float f3 = 0.0105F;
            matrixStack.scale(f3 * renderInfo.factor, -f3 * renderInfo.factor, f3);
            // @todo 1.15
//            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
//            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            int y = currenty;
            int i = 0;

            for (int yy = 0 ; yy < 3 ; yy++) {
                for (int xx = 0 ; xx < 3 ; xx++) {
                    if (!stacks.get(i).isEmpty()) {
                        int x = xx * 40;
                        boolean hilighted = renderInfo.hitx >= x+8 && renderInfo.hitx <= x + 38 && renderInfo.hity >= y-7 && renderInfo.hity <= y + 22;
                        if (hilighted) {
//                            mcjty.lib.client.RenderHelper.drawBeveledBox(5 + xx * 30, 10 + yy * 24 - 4, 29 + xx * 30, 10 + yy * 24 + 20, 0xffffffff, 0xffffffff, 0xff333333);
                            RenderHelper.drawFlatButtonBox(matrixStack, buffer, (int) (5 + xx * 30.5f), 10 + yy * 24 - 4, (int) (29 + xx * 30.5f), 10 + yy * 24 + 20, 0xffffffff, 0xff333333, 0xffffffff);
                        }
                    }
                    i++;
                }
                y += 35;
            }
            matrixStack.push();
        }

//        RenderHelper.setupGui3DDiffuseLighting();
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

//        GlStateManager.depthMask(true);

//        GlStateManager.enableLighting();
//        GlStateManager.enableDepthTest();

        matrixStack.push();
        float f3 = 0.0105F;
        matrixStack.translate(-0.5F, 0.5F, 0.06F);
        float factor = renderInfo.factor;
        matrixStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        int y = currenty;
        int i = 0;

        for (int yy = 0 ; yy < 3 ; yy++) {
            for (int xx = 0 ; xx < 3 ; xx++) {
                if (!stacks.get(i).isEmpty()) {
                    int x = 7 + xx * 30;
                    renderSlot(y, stacks.get(i), x);
                }
                i++;
            }
            y += 24;
        }

        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(-0.5F, 0.5F, 0.08F);
        f3 = 0.0050F;
        matrixStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        y = currenty + 30;
        i = 0;

        for (int yy = 0 ; yy < 3 ; yy++) {
            for (int xx = 0 ; xx < 3 ; xx++) {
                if (!stacks.get(i).isEmpty()) {
                    renderSlotOverlay(matrixStack, buffer, fontRenderer, y, stacks.get(i), screenData.getAmount(i), 42 + xx * 64);
                }
                i++;
            }
            y += 52;
        }

//        GlStateManager.disableLighting();

        boolean insertStackActive = renderInfo.hitx >= 0 && renderInfo.hitx < 60 && renderInfo.hity > 98 && renderInfo.hity <= 120;
        fontRenderer.renderString("Insert Stack", 20, y - 20, insertStackActive ? 0xffffff : 0x666666, false, matrixStack.getLast().getPositionMatrix(), buffer, false, 0, 140);
        boolean insertAllActive = renderInfo.hitx >= 60 && renderInfo.hitx <= 120 && renderInfo.hity > 98 && renderInfo.hity <= 120;
        fontRenderer.renderString("Insert All", 120, y - 20, insertAllActive ? 0xffffff : 0x666666, false, matrixStack.getLast().getPositionMatrix(), buffer, false, 0, 140);

        matrixStack.pop();

//        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    private void renderSlot(int currenty, ItemStack stack, int x) {
        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
        itemRender.renderItemAndEffectIntoGUI(stack, x, currenty);
    }

    private void renderSlotOverlay(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontRenderer, int currenty, ItemStack stack, int amount, int x) {
//                itemRender.renderItemOverlayIntoGUI(fontRenderer, Minecraft.getInstance().getTextureManager(), itm, x, currenty);
        renderItemOverlayIntoGUI(matrixStack, buffer, fontRenderer, stack, amount, x, currenty);
    }

    private static void renderItemOverlayIntoGUI(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontRenderer, ItemStack itemStack, int size, int x, int y) {
        if (!itemStack.isEmpty()) {
            String s1;
            if (size < 10000) {
                s1 = String.valueOf(size);
            } else if (size < 1000000) {
                s1 = String.valueOf(size / 1000) + "k";
            } else if (size < 1000000000) {
                s1 = String.valueOf(size / 1000000) + "m";
            } else {
                s1 = String.valueOf(size / 1000000000) + "g";
            }
            // @todo 1.15
//            GlStateManager.disableLighting();
//            GlStateManager.disableBlend();
            fontRenderer.renderString(s1, x + 19 - 2 - fontRenderer.getStringWidth(s1), y + 6 + 3, 16777215, false, matrixStack.getLast().getPositionMatrix(), buffer, false, 0, 140);
//            GlStateManager.enableLighting();

            if (itemStack.getItem().showDurabilityBar(itemStack)) {
                double health = itemStack.getItem().getDurabilityForDisplay(itemStack);
                int j1 = (int) Math.round(13.0D - health * 13.0D);
                int k = (int) Math.round(255.0D - health * 255.0D);
//                GlStateManager.disableLighting();
//                GlStateManager.disableTexture();
//                GlStateManager.disableAlphaTest();
//                GlStateManager.disableBlend();
//                Tessellator tessellator = Tessellator.getInstance();
                int l = 255 - k << 16 | k << 8;
                int i1 = (255 - k) / 4 << 16 | 16128;
                IVertexBuilder builder = buffer.getBuffer(ScreenRenderType.QUADS_NOTEXTURE);    // @todo 1.15 check
                renderQuad(builder, x + 2, y + 13, 13, 2, 0, 0.0D);
                renderQuad(builder, x + 2, y + 13, 12, 1, i1, 0.02D);
                renderQuad(builder, x + 2, y + 13, j1, 1, l, 0.04D);
//                GlStateManager.enableAlphaTest();
//                GlStateManager.enableTexture();
//                GlStateManager.enableLighting();
//                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private static void renderQuad(IVertexBuilder builder, int x, int y, int width, int height, int color, double offset) {
//        tessellator.setColorOpaque_I(color);
        builder.pos(x, y, offset).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        builder.pos(x, (y + height), offset).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        builder.pos((x + width), (y + height), offset).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        builder.pos((x + width), y, offset).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
    }


    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionType dim, BlockPos pos) {
        if (tagCompound != null) {
            for (int i = 0 ; i < stacks.size() ; i++) {
                if (tagCompound.contains("stack"+i)) {
                    stacks.set(i, ItemStack.read(tagCompound.getCompound("stack" + i)));
                }
            }
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
