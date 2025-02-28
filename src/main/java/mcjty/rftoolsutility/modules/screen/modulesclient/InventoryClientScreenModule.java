package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.modules.InventoryScreenModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InventoryClientScreenModule implements IClientScreenModule<InventoryScreenModule.ModuleDataStacks> {
    private int slot1 = -1;
    private int slot2 = -1;
    private int slot3 = -1;
    private int slot4 = -1;

    public static final Codec<InventoryClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("slot1").forGetter(module -> module.slot1),
            Codec.INT.fieldOf("slot2").forGetter(module -> module.slot2),
            Codec.INT.fieldOf("slot3").forGetter(module -> module.slot3),
            Codec.INT.fieldOf("slot4").forGetter(module -> module.slot4)
    ).apply(instance, InventoryClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryClientScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.slot1,
            ByteBufCodecs.INT, module -> module.slot2,
            ByteBufCodecs.INT, module -> module.slot3,
            ByteBufCodecs.INT, module -> module.slot4,
            InventoryClientScreenModule::new);

    public InventoryClientScreenModule(int slot1, int slot2, int slot3, int slot4) {
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
        this.slot4 = slot4;
    }

    public int getSlot1() {
        return slot1;
    }

    public void setSlot1(int slot1) {
        this.slot1 = slot1;
    }

    public int getSlot2() {
        return slot2;
    }

    public void setSlot2(int slot2) {
        this.slot2 = slot2;
    }

    public int getSlot3() {
        return slot3;
    }

    public void setSlot3(int slot3) {
        this.slot3 = slot3;
    }

    public int getSlot4() {
        return slot4;
    }

    public void setSlot4(int slot4) {
        this.slot4 = slot4;
    }

    public InventoryClientScreenModule() {
    }

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return TransformMode.ITEM;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, InventoryScreenModule.ModuleDataStacks screenData, ModuleRenderInfo renderInfo) {
        if (screenData == null) {
            return;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        float f3 = 0.0075f;
        float factor = renderInfo.factor;
        poseStack.translate(-0.5, 0.5, 0.06F);
        poseStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        int x = 10;
        x = renderSlot(poseStack, buffer, currenty, screenData, slot1, 0, x, renderInfo.getLightmapValue());
        x = renderSlot(poseStack, buffer, currenty, screenData, slot2, 1, x, renderInfo.getLightmapValue());
        x = renderSlot(poseStack, buffer, currenty, screenData, slot3, 2, x, renderInfo.getLightmapValue());
        renderSlot(poseStack, buffer, currenty, screenData, slot4, 3, x, renderInfo.getLightmapValue());

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-0.5F, 0.5F, 0.08F);
        poseStack.scale(f3 * factor, -f3 * factor, 0.0001f);

        x = 10;
        x = renderSlotOverlay(poseStack, buffer, fontRenderer, currenty, screenData, slot1, 0, x, renderInfo.getLightmapValue());
        x = renderSlotOverlay(poseStack, buffer, fontRenderer, currenty, screenData, slot2, 1, x, renderInfo.getLightmapValue());
        x = renderSlotOverlay(poseStack, buffer, fontRenderer, currenty, screenData, slot3, 2, x, renderInfo.getLightmapValue());
        renderSlotOverlay(poseStack, buffer, fontRenderer, currenty, screenData, slot4, 3, x, renderInfo.getLightmapValue());
        poseStack.popPose();
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {

    }

    private int renderSlot(PoseStack matrixStack, MultiBufferSource buffer, int currenty, InventoryScreenModule.ModuleDataStacks screenData, int slot, int index, int x, int lightmapValue) {
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

                RenderHelper.renderItemGui(matrixStack, buffer, itm, lightmapValue, OverlayTexture.NO_OVERLAY);

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

    private int renderSlotOverlay(PoseStack matrixStack, MultiBufferSource buffer, Font fontRenderer, int currenty, InventoryScreenModule.ModuleDataStacks screenData, int slot, int index, int x, int lightmapValue) {
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
                    renderQuad(builder, x + 2, currenty + 13, 13, 2, 0, 0, 0, 0.0f, 140);
                    renderQuad(builder, x + 2, currenty + 13, 12, 1, r2, g2, b2, 0.02f, 140);
                    renderQuad(builder, x + 2, currenty + 13, j1, 1, r1, g1, b1, 0.04f, 140);
                }
            }
            x += 30;
        }
        return x;
    }

    private static void renderQuad(VertexConsumer builder, int x, int y, int width, int height, int r, int g, int b, float offset, int lightmapValue) {
        // @todo 1.21 is setLight correct? Used to be setUv2(lightmap)
        builder.addVertex(x, y, offset).setColor(r, g, b, 255).setLight(lightmapValue);
        builder.addVertex(x, (y + height), offset).setColor(r, g, b, 255).setLight(lightmapValue);
        builder.addVertex((x + width), (y + height), offset).setColor(r, g, b, 255).setLight(lightmapValue);
        builder.addVertex((x + width), y, offset).setColor(r, g, b, 255).setLight(lightmapValue);
    }


    @Override
    public boolean needsServerData() {
        return true;
    }
}
