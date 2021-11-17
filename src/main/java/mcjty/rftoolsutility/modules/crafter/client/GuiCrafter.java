package mcjty.rftoolsutility.modules.crafter.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import mcjty.rftoolsutility.modules.crafter.CraftingRecipe;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterBaseTE;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer;
import mcjty.rftoolsutility.modules.crafter.network.PacketCrafter;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.horizontal;
import static mcjty.lib.gui.widgets.Widgets.label;

public class GuiCrafter extends GenericGuiContainer<CrafterBaseTE, CrafterContainer> {
    private EnergyBar energyBar;
    private WidgetList recipeList;
    private ChoiceLabel keepItem;
    private ChoiceLabel internalRecipe;
    private Button applyButton;

    private static final ResourceLocation iconGuiElements = new ResourceLocation(RFToolsBase.MODID, "textures/gui/guielements.png");

    private static int lastSelected = -1;

    public GuiCrafter(CrafterBaseTE te, CrafterContainer container, PlayerInventory inventory) {
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

        Integer sizeInventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(IItemHandler::getSlots).orElse(0);
        if (lastSelected != -1 && lastSelected < sizeInventory) {
            recipeList.selected(lastSelected);
        }
//        sendChangeToServer(-1, null, null, false, CraftingRecipe.CraftMode.EXT);

        window.event("apply", (source, params) -> applyRecipe());
        window.event("select", (source, params) -> selectRecipe());
    }

    private void initializeFields() {
        recipeList = window.findChild("recipes");
        energyBar = window.findChild("energybar");
        applyButton = window.findChild("apply");
        keepItem = window.findChild("keep");
        internalRecipe = window.findChild("internal");
    }

    private void updateFields() {
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
        ((ImageChoiceLabel) window.findChild("speed")).setCurrentChoice(tileEntity.getSpeedMode());
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
        lastSelected = selected;
        if (selected == -1) {
            for (int i = 0; i < 10; i++) {
                menu.getSlot(i).set(ItemStack.EMPTY);
            }
            keepItem.choice("All");
            internalRecipe.choice("Ext");
            return;
        }
        CraftingRecipe craftingRecipe = tileEntity.getRecipe(selected);
        CraftingInventory inv = craftingRecipe.getInventory();
        for (int i = 0; i < 9; i++) {
            menu.getSlot(i).set(inv.getItem(i));
        }
        menu.getSlot(9).set(craftingRecipe.getResult());
        keepItem.choice(craftingRecipe.isKeepOne() ? "Keep" : "All");
        internalRecipe.choice(craftingRecipe.getCraftMode().getDescription());
    }

    private void testRecipe() {
        int selected = recipeList.getSelected();
        if (selected == -1) {
            return;
        }

        CraftingInventory inv = new CraftingInventory(new Container(null, -1) {
            @Override
            public boolean stillValid(@Nonnull PlayerEntity var1) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, menu.getSlot(i).getItem());
        }

        // Compare current contents to avoid unneeded slot update.
        IRecipe recipe = CraftingRecipe.findRecipe(minecraft.level, inv);
        ItemStack newResult;
        if (recipe == null) {
            newResult = ItemStack.EMPTY;
        } else {
            newResult = recipe.assemble(inv);
        }
        menu.getSlot(9).set(newResult);
    }

    private void applyRecipe() {
        int selected = recipeList.getSelected();
        if (selected == -1) {
            return;
        }

        if (selected >= tileEntity.getSupportedRecipes()) {
            recipeList.selected(-1);
            return;
        }

        CraftingRecipe craftingRecipe = tileEntity.getRecipe(selected);
        CraftingInventory inv = craftingRecipe.getInventory();

        for (int i = 0; i < 9; i++) {
            ItemStack oldStack = inv.getItem(i);
            ItemStack newStack = menu.getSlot(i).getItem();
            if (!itemStacksEqual(oldStack, newStack)) {
                inv.setItem(i, newStack);
            }
        }

        // Compare current contents to avoid unneeded slot update.
        IRecipe recipe = CraftingRecipe.findRecipe(minecraft.level, inv);
        ItemStack newResult;
        if (recipe == null) {
            newResult = ItemStack.EMPTY;
        } else {
            newResult = recipe.assemble(inv);
        }
        ItemStack oldResult = menu.getSlot(9).getItem();
        if (!itemStacksEqual(oldResult, newResult)) {
            menu.getSlot(9).set(newResult);
        }

        craftingRecipe.setResult(newResult);
        updateRecipe();
        populateList();
    }

    private void updateRecipe() {
        int selected = recipeList.getSelected();
        if (selected == -1) {
            return;
        }
        CraftingRecipe craftingRecipe = tileEntity.getRecipe(selected);
        boolean keepOne = "Keep".equals(keepItem.getCurrentChoice());
        CraftingRecipe.CraftMode mode;
        if ("Int".equals(internalRecipe.getCurrentChoice())) {
            mode = CraftingRecipe.CraftMode.INT;
        } else if ("Ext".equals(internalRecipe.getCurrentChoice())) {
            mode = CraftingRecipe.CraftMode.EXT;
        } else {
            mode = CraftingRecipe.CraftMode.EXTC;
        }
        craftingRecipe.setKeepOne(keepOne);
        craftingRecipe.setCraftMode(mode);
        sendChangeToServer(selected, craftingRecipe.getInventory(), craftingRecipe.getResult(), keepOne, mode);
    }

    private boolean itemStacksEqual(ItemStack matches, ItemStack oldStack) {
        if (matches.isEmpty()) {
            return oldStack.isEmpty();
        } else {
            return !oldStack.isEmpty() && matches.sameItem(oldStack);
        }
    }

    private void sendChangeToServer(int index, CraftingInventory inv, ItemStack result, boolean keepOne,
                                    CraftingRecipe.CraftMode mode) {

        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketCrafter(tileEntity.getBlockPos(), index, inv,
                result, keepOne, mode));
    }

    private void updateButtons() {
        if (recipeList != null) {
            boolean selected = recipeList.getSelected() != -1;
            keepItem.enabled(selected);
            internalRecipe.enabled(selected);
            applyButton.enabled(selected);
        }
        if (keepItem.getCurrentChoice() == null || keepItem.getCurrentChoice().trim().isEmpty()) {
            keepItem.choice("All");
        }
        if (internalRecipe.getCurrentChoice() == null || internalRecipe.getCurrentChoice().trim().isEmpty()) {
            internalRecipe.choice("Ext");
        }
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float v, int x, int y) {
        if (window == null) {
            return;
        }
        updateFields();
        updateButtons();

        drawWindow(matrixStack);

        // Draw the ghost slots here
        drawGhostSlots(matrixStack);
        testRecipe();
    }

    private void drawGhostSlots(MatrixStack matrixStack) {
        net.minecraft.client.renderer.RenderHelper.setupFor3DItems();
        matrixStack.pushPose();
        matrixStack.translate(leftPos, topPos, 0.0F);
        RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        // @todo 1.15
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240 / 1.0F, 240 / 1.0F);

        ItemStackList ghostSlots = tileEntity.getGhostSlots();
        itemRenderer.blitOffset = 100.0F;
        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();
        RenderSystem.enableLighting();

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
                    itemRenderer.renderAndDecorateItem(stack, leftPos + slot.x, topPos + slot.y);

                    RenderSystem.disableLighting();
                    GlStateManager._enableBlend();
                    GlStateManager._disableDepthTest();
                    this.minecraft.getTextureManager().bind(iconGuiElements);
                    RenderHelper.drawTexturedModalRect(matrixStack.last().pose(), slot.x, slot.y, 14 * 16, 3 * 16, 16, 16);
                    GlStateManager._enableDepthTest();
                    GlStateManager._disableBlend();
                    RenderSystem.enableLighting();
                }
            }

        }
        itemRenderer.blitOffset = 0.0F;

        matrixStack.popPose();
        net.minecraft.client.renderer.RenderHelper.turnOff();
    }
}
