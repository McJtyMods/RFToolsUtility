package mcjty.rftoolsutility.modules.crafter.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.ItemStackList;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterBaseTE;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer;
import mcjty.rftoolsutility.modules.crafter.data.CraftingRecipe;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.horizontal;
import static mcjty.lib.gui.widgets.Widgets.label;

public class GuiCrafter extends GenericGuiContainer<CrafterBaseTE, CrafterContainer> {
    private EnergyBar energyBar;
    private WidgetList recipeList;
    private Button applyButton;

    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    public GuiCrafter(CrafterBaseTE te, CrafterContainer container, Inventory inventory) {
        super(te, container, inventory, CrafterModule.CRAFTER1.get().getManualEntry());
    }

    public static void register() {
        register(CrafterModule.CONTAINER_CRAFTER.get(), GuiCrafter::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsUtilityMessages.INSTANCE, new ResourceLocation(RFToolsUtility.MODID, "gui/crafter.gui"));
        super.init();

        initializeFields();

        window.event("select", (source, params) -> selectRecipe());
    }

    private void initializeFields() {
        recipeList = window.findChild("recipes");
        energyBar = window.findChild("energybar");
        applyButton = window.findChild("apply");
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        recipeList.selected(tileEntity.getSelected());
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
        populateList();
        updateEnergyBar(energyBar);
    }


    private void populateList() {
        recipeList.removeChildren();
        for (int i = 0; i < tileEntity.getSupportedRecipes(); i++) {
            CraftingRecipe recipe = tileEntity.getRecipe(i);
            addRecipeLine(recipe.getResult());
        }
    }

    private void addRecipeLine(ItemStack craftingResult) {
        String readableName = Tools.getReadableName(craftingResult);
        int color = StyleConfig.colorTextInListNormal;
        if (craftingResult.isEmpty()) {
            readableName = "<no recipe>";
            color = 0xFF505050;
        }
        Panel panel = horizontal().children(
                new BlockRender()
                        .renderItem(craftingResult)
                        .tooltips("Double click to edit this recipe"),
                label(readableName)
                        .color(color)
                        .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                        .dynamic(true)
                        .tooltips("Double click to edit this recipe"));
        recipeList.children(panel);
    }

    private void selectRecipe() {
        int selected = recipeList.getSelected();
        setValue(RFToolsUtilityMessages.INSTANCE, CrafterBaseTE.SELECTED, selected);
    }

    private void updateButtons() {
        if (recipeList != null) {
            boolean selected = recipeList.getSelected() != -1;
            applyButton.enabled(selected);
        } else {
            applyButton.enabled(false);
        }
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float v, int x, int y) {
        if (window == null) {
            return;
        }
        updateFields();
        updateButtons();

        drawWindow(matrixStack);

        // Draw the ghost slots here
        drawGhostSlots(matrixStack);
    }

    private void drawGhostSlots(PoseStack matrixStack) {
        com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
        matrixStack.pushPose();
//        itemRenderer.blitOffset = 100.0F;
        matrixStack.translate(leftPos, topPos, 100.0F);
        RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1.0F);
//        RenderSystem.enableRescaleNormal();// @todo 1.18
        // @todo 1.15
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240 / 1.0F, 240 / 1.0F);

        ItemStackList ghostSlots = tileEntity.getGhostSlots();
        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();
//        RenderSystem.enableLighting();// @todo 1.18

        for (int i = 0; i < ghostSlots.size(); i++) {
            ItemStack stack = ghostSlots.get(i);
            if (!stack.isEmpty()) {
                int slotIdx;
                if (i < CrafterContainer.BUFFER_SIZE) {
                    slotIdx = i + CrafterContainer.SLOT_BUFFER;
                } else {
                    slotIdx = i + CrafterContainer.SLOT_BUFFEROUT - CrafterContainer.BUFFER_SIZE;
                }
                Slot slot = menu.getSlot(slotIdx);
                if (!slot.hasItem()) {
                    itemRenderer.renderAndDecorateItem(matrixStack, stack, leftPos + slot.x, topPos + slot.y);

//                    RenderSystem.disableLighting();// @todo 1.18
                    RenderSystem.enableBlend();
                    RenderSystem.disableDepthTest();
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, iconGuiElements);
                    RenderHelper.drawTexturedModalRect(matrixStack, slot.x, slot.y, 14 * 16, 3 * 16, 16, 16);
                    RenderSystem.enableDepthTest();
                    RenderSystem.disableBlend();
//                    RenderSystem.enableLighting();// @todo 1.18
                }
            }

        }
//        itemRenderer.blitOffset = 0.0F;

        matrixStack.popPose();
//        Lighting.turnOff();   // @todo 1.18
    }
}
