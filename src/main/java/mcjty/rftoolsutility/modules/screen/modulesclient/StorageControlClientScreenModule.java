package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsutility.modules.screen.modules.StorageControlScreenModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
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
            matrixStack.push();
            matrixStack.translate(-0.5F, 0.5F, 0.07F);
            float f3 = 0.0105F;
            matrixStack.scale(f3 * renderInfo.factor, -f3 * renderInfo.factor, f3);

            int y = currenty;
            int i = 0;

            for (int yy = 0 ; yy < 3 ; yy++) {
                for (int xx = 0 ; xx < 3 ; xx++) {
                    if (!stacks.get(i).isEmpty()) {
                        int x = xx * 40;
                        boolean hilighted = renderInfo.hitx >= x+8 && renderInfo.hitx <= x + 38 && renderInfo.hity >= y-7 && renderInfo.hity <= y + 22;
                        if (hilighted) {
                            RenderHelper.drawFlatButtonBox(matrixStack, buffer, (int) (5 + xx * 30.5f), 10 + yy * 24 - 4, (int) (29 + xx * 30.5f), 10 + yy * 24 + 20, 0xffffffff, 0xff333333, 0xffffffff,
                                    renderInfo.getLightmapValue());
                        }
                    }
                    i++;
                }
                y += 35;
            }
            matrixStack.pop();
        }

        matrixStack.push();
        float f3 = 0.0105F;
        matrixStack.translate(-0.5F, 0.5F, 0.06F);
        float factor = renderInfo.factor;
        matrixStack.scale(f3 * factor, f3 * factor, 0.0001f);

        int y = currenty;
        int i = 0;

        for (int yy = 0 ; yy < 3 ; yy++) {
            for (int xx = 0 ; xx < 3 ; xx++) {
                if (!stacks.get(i).isEmpty()) {
                    int x = 7 + xx * 30;
                    renderSlot(matrixStack, buffer, -16-y, stacks.get(i), x, renderInfo.getLightmapValue());
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
                    renderSlotOverlay(matrixStack, buffer, fontRenderer, y, stacks.get(i), screenData.getAmount(i), 32 + xx * 64,
                            renderInfo.getLightmapValue());
                }
                i++;
            }
            y += 52;
        }

        boolean insertStackActive = renderInfo.hitx >= 0 && renderInfo.hitx < 60 && renderInfo.hity > 98 && renderInfo.hity <= 120;
        fontRenderer.renderString("Insert Stack", 20, y - 20, insertStackActive ? 0xffffff : 0x666666, false, matrixStack.getLast().getMatrix(), buffer, false, 0, renderInfo.getLightmapValue());
        boolean insertAllActive = renderInfo.hitx >= 60 && renderInfo.hitx <= 120 && renderInfo.hity > 98 && renderInfo.hity <= 120;
        fontRenderer.renderString("Insert All", 120, y - 20, insertAllActive ? 0xffffff : 0x666666, false, matrixStack.getLast().getMatrix(), buffer, false, 0, renderInfo.getLightmapValue());

        matrixStack.pop();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    private void renderSlot(MatrixStack matrixStack, IRenderTypeBuffer buffer, int currenty, ItemStack stack, int x, int lightmapValue) {
        matrixStack.push();
        matrixStack.translate((float)x+8f, (float)currenty+8f, 5);
        matrixStack.scale(16, 16, 16);

        ItemRenderer itemRender = Minecraft.getInstance().getItemRenderer();
        IBakedModel ibakedmodel = itemRender.getItemModelWithOverrides(stack, Minecraft.getInstance().world, (LivingEntity)null);
        itemRender.renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixStack, buffer, lightmapValue, OverlayTexture.NO_OVERLAY, ibakedmodel);
        matrixStack.pop();
    }

    private void renderSlotOverlay(MatrixStack matrixStack, IRenderTypeBuffer buffer, FontRenderer fontRenderer, int currenty, ItemStack stack, int amount, int x, int lightmapValue) {
        if (!stack.isEmpty()) {
            String s1;
            if (amount < 10000) {
                s1 = String.valueOf(amount);
            } else if (amount < 1000000) {
                s1 = String.valueOf(amount / 1000) + "k";
            } else if (amount < 1000000000) {
                s1 = String.valueOf(amount / 1000000) + "m";
            } else {
                s1 = String.valueOf(amount / 1000000000) + "g";
            }
            fontRenderer.renderString(s1, x + 19 - 2 - fontRenderer.getStringWidth(s1), currenty + 6 + 3, 16777215, false, matrixStack.getLast().getMatrix(), buffer, false, 0, lightmapValue);

            if (stack.getItem().showDurabilityBar(stack)) {
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int j1 = (int) Math.round(13.0D - health * 13.0D);
                int k = (int) Math.round(255.0D - health * 255.0D);
                int l = 255 - k << 16 | k << 8;
                int i1 = (255 - k) / 4 << 16 | 16128;
                IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.QUADS_NOTEXTURE);
                renderQuad(builder, x + 2, currenty + 13, 13, 2, 0, 0.0D, lightmapValue);
                renderQuad(builder, x + 2, currenty + 13, 12, 1, i1, 0.02D, lightmapValue);
                renderQuad(builder, x + 2, currenty + 13, j1, 1, l, 0.04D, lightmapValue);
            }
        }
    }

    private static void renderQuad(IVertexBuilder builder, int x, int y, int width, int height, int color, double offset, int lightmapValue) {
        builder.pos(x, y, offset).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(lightmapValue).endVertex();
        builder.pos(x, (y + height), offset).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(lightmapValue).endVertex();
        builder.pos((x + width), (y + height), offset).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(lightmapValue).endVertex();
        builder.pos((x + width), y, offset).color(1.0f, 1.0f, 1.0f, 1.0f).lightmap(lightmapValue).endVertex();
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
