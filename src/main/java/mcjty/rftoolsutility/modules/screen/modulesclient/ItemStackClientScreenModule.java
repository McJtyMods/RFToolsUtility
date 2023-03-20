package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.modules.ItemStackScreenModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
    public void render(PoseStack matrixStack, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, ItemStackScreenModule.ModuleDataStacks screenData, ModuleRenderInfo renderInfo) {
        if (screenData == null) {
            return;
        }

        matrixStack.pushPose();
        float f3 = 0.0075f;
        float factor = renderInfo.factor;
        matrixStack.translate(-0.5, 0.5, 0.06F);
        matrixStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        int x = 10;
        x = renderSlot(matrixStack, buffer, currenty, screenData, slot1, 0, x, renderInfo.getLightmapValue());
        x = renderSlot(matrixStack, buffer, currenty, screenData, slot2, 1, x, renderInfo.getLightmapValue());
        x = renderSlot(matrixStack, buffer, currenty, screenData, slot3, 2, x, renderInfo.getLightmapValue());
        renderSlot(matrixStack, buffer, currenty, screenData, slot4, 3, x, renderInfo.getLightmapValue());

        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(-0.5F, 0.5F, 0.08F);
        matrixStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        x = 10;
        x = renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot1, 0, x, renderInfo.getLightmapValue());
        x = renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot2, 1, x, renderInfo.getLightmapValue());
        x = renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot3, 2, x, renderInfo.getLightmapValue());
        renderSlotOverlay(matrixStack, buffer, fontRenderer, currenty, screenData, slot4, 3, x, renderInfo.getLightmapValue());
        matrixStack.popPose();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked) {

    }

    private int renderSlot(PoseStack matrixStack, MultiBufferSource buffer, int currenty, ItemStackScreenModule.ModuleDataStacks screenData, int slot, int index, int x, int lightmapValue) {
        if (slot != -1) {
            ItemStack itm = ItemStack.EMPTY;
            try {
                itm = screenData.getStack(index);
            } catch (Exception e) {
                // Ignore this.
            }
            if (!itm.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.translate(x +8f, currenty +8f, 0);
                matrixStack.scale(16, -16, 16);

                ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
                BakedModel ibakedmodel = itemRender.getModel(itm, Minecraft.getInstance().level, null, 0);  // @todo 1.18 Last parameter?
                itemRender.render(itm, ItemDisplayContext.GUI, false, matrixStack, buffer, lightmapValue, OverlayTexture.NO_OVERLAY, ibakedmodel);

                // @todo 1.15 UGLY HACK to forge consistent lighting in gui and in tablet
//                RenderSystem.enableRescaleNormal(); // @todo 1.18
//                RenderSystem.enableAlphaTest();
//                RenderSystem.defaultAlphaFunc();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F); // @todo 1.18
//                RenderHelper.setupGuiFlatDiffuseLighting();
                ((MultiBufferSource.BufferSource)buffer).endBatch();
                // END OF UGLY HACK

                matrixStack.popPose();
            }
            x += 30;
        }
        return x;
    }

    private int renderSlotOverlay(PoseStack matrixStack, MultiBufferSource buffer, Font fontRenderer, int currenty, ItemStackScreenModule.ModuleDataStacks screenData, int slot, int index, int x, int lightmapValue) {
        if (slot != -1) {
            ItemStack itm = screenData.getStack(index);
            if (!itm.isEmpty()) {
                int size = itm.getCount();
                if (size > 1) {
                    String s1;
                    if (size < 10000) {
                        s1 = String.valueOf(size);
                    } else if (size < 1000000) {
                        s1 = size / 1000 + "k";
                    } else if (size < 1000000000) {
                        s1 = size / 1000000 + "m";
                    } else {
                        s1 = size / 1000000000 + "g";
                    }
                    RenderHelper.renderText(fontRenderer, s1, x + 19 - 2 - fontRenderer.width(s1), currenty + 6 + 3, 16777215, matrixStack, buffer, lightmapValue);
                }

                if (itm.getItem().isBarVisible(itm)) {
                    double health = itm.getItem().getBarWidth(itm);
                    int j1 = (int) Math.round(13.0D - health * 13.0D);
                    int k = (int) Math.round(255.0D - health * 255.0D);
                    VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);

                    int r1 = 255 - k;
                    int g1 = k;
                    int b1 = 0;
                    int r2 = (255-k)/4;
                    int g2 = 0x3f;
                    int b2 = 0;
                    renderQuad(builder, x + 2, currenty + 13, 13, 2, 0, 0, 0, 0.0D, 140);
                    renderQuad(builder, x + 2, currenty + 13, 12, 1, r2, g2, b2, 0.02D, 140);
                    renderQuad(builder, x + 2, currenty + 13, j1, 1, r1, g1, b1, 0.04D, 140);
                }
            }
            x += 30;
        }
        return x;
    }

    private static void renderQuad(VertexConsumer builder, int x, int y, int width, int height, int r, int g, int b, double offset, int lightmapValue) {
        builder.vertex(x, y, offset).color(r, g, b, 255).uv2(lightmapValue).endVertex();
        builder.vertex(x, (y + height), offset).color(r, g, b, 255).uv2(lightmapValue).endVertex();
        builder.vertex((x + width), (y + height), offset).color(r, g, b, 255).uv2(lightmapValue).endVertex();
        builder.vertex((x + width), y, offset).color(r, g, b, 255).uv2(lightmapValue).endVertex();
    }


    @Override
    public void setupFromNBT(CompoundTag tagCompound, ResourceKey<Level> dim, BlockPos pos) {
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
