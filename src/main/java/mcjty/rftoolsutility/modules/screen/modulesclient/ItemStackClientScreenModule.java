package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.client.ScreenRenderType;
import mcjty.rftoolsutility.modules.screen.modules.ItemStackScreenModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ItemStackClientScreenModule implements IClientScreenModule<ItemStackScreenModule.ModuleDataStacks> {
    private int slot1 = -1;
    private int slot2 = -1;
    private int slot3 = -1;
    private int slot4 = -1;

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return TransformMode.ITEM;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, ItemStackScreenModule.ModuleDataStacks screenData, ModuleRenderInfo renderInfo) {
        if (screenData == null) {
            return;
        }

        // @todo 1.15
//        RenderHelper.setupGui3DDiffuseLighting();
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        GlStateManager.depthMask(true);
//
//        GlStateManager.enableLighting();
//        GlStateManager.enableDepthTest();

        matrixStack.push();
        float f3 = 0.0075F;
        matrixStack.translate(-0.5F, 0.5F, 0.06F);
        float factor = renderInfo.factor;
        matrixStack.scale(f3 * factor, -f3 * factor, 0.0001f);

//        short short1 = 240;
//        short short2 = 240;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
        int x = 10;
        x = renderSlot(matrixStack, buffer, currenty, screenData, slot1, 0, x);
        x = renderSlot(matrixStack, buffer, currenty, screenData, slot2, 1, x);
        x = renderSlot(matrixStack, buffer, currenty, screenData, slot3, 2, x);
        renderSlot(matrixStack, buffer, currenty, screenData, slot4, 3, x);

        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(-0.5F, 0.5F, 0.08F);
        matrixStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        x = 10;
        x = renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot1, 0, x);
        x = renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot2, 1, x);
        x = renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot3, 2, x);
        renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot4, 3, x);
        matrixStack.pop();

//        GlStateManager.disableLighting();
//        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    private int renderSlot(MatrixStack matrixStack, IRenderTypeBuffer buffer, int currenty, ItemStackScreenModule.ModuleDataStacks screenData, int slot, int index, int x) {
        if (slot != -1) {
            ItemStack itm = ItemStack.EMPTY;
            try {
                itm = screenData.getStack(index);
            } catch (Exception e) {
                // Ignore this.
            }
            if (!itm.isEmpty()) {
                ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
//                itemRender.renderItemAndEffectIntoGUI(itm, x, currenty);
                IBakedModel ibakedmodel = itemRender.getItemModelWithOverrides(itm, Minecraft.getInstance().world, (LivingEntity)null);
                itemRender.renderItem(itm, ItemCameraTransforms.TransformType.GUI, false, matrixStack, buffer, 0, 0, ibakedmodel);
            }
            x += 30;
        }
        return x;
    }

    private int renderSlotOverlay(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontRenderer, int currenty, ItemStackScreenModule.ModuleDataStacks screenData, int slot, int index, int x) {
        if (slot != -1) {
            ItemStack itm = screenData.getStack(index);
            if (!itm.isEmpty()) {
//                itemRender.renderItemOverlayIntoGUI(fontRenderer, Minecraft.getInstance().getTextureManager(), itm, x, currenty);
                renderItemOverlayIntoGUI(matrixStack, buffer, fontRenderer, itm, x, currenty);
            }
            x += 30;
        }
        return x;
    }

    private static void renderItemOverlayIntoGUI(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontRenderer, ItemStack itemStack, int x, int y) {
        if (!itemStack.isEmpty()) {
            int size = itemStack.getCount();
            if (size > 1) {
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
//                GlStateManager.disableLighting();
//                GlStateManager.disableBlend();
                fontRenderer.renderString(s1, x + 19 - 2 - fontRenderer.getStringWidth(s1), y + 6 + 3, 16777215, false, matrixStack.getLast().getPositionMatrix(), buffer, false, 0, 140);
//                GlStateManager.enableLighting();
            }

            if (itemStack.getItem().showDurabilityBar(itemStack)) {
                double health = itemStack.getItem().getDurabilityForDisplay(itemStack);
                int j1 = (int) Math.round(13.0D - health * 13.0D);
                int k = (int) Math.round(255.0D - health * 255.0D);
                // @todo 1.15
//                GlStateManager.disableLighting();
//                GlStateManager.disableTexture();
//                GlStateManager.disableAlphaTest();
//                GlStateManager.disableBlend();
                IVertexBuilder builder = buffer.getBuffer(ScreenRenderType.QUADS_NOTEXTURE);    // @todo 1.15 check
                int l = 255 - k << 16 | k << 8;
                int i1 = (255 - k) / 4 << 16 | 16128;
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
        builder.pos(x, y, offset);
        builder.pos(x, (y + height), offset);
        builder.pos((x + width), (y + height), offset);
        builder.pos((x + width), y, offset);
    }


    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionType dim, BlockPos pos) {
        if (tagCompound != null) {
            if (tagCompound.contains("slot1")) {
                slot1 = tagCompound.getInt("slot1");
            }
            if (tagCompound.contains("slot2")) {
                slot2 = tagCompound.getInt("slot2");
            }
            if (tagCompound.contains("slot3")) {
                slot3 = tagCompound.getInt("slot3");
            }
            if (tagCompound.contains("slot4")) {
                slot4 = tagCompound.getInt("slot4");
            }
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
