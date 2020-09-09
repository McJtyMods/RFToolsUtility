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
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.ItemStackList;
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

        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
        ((ImageChoiceLabel) window.findChild("speed")).setCurrentChoice(tileEntity.getSpeedMode());

        populateList();
    }


    private void populateList() {
        recipeList.removeChildren();
        for (int i = 0; i < tileEntity.getSupportedRecipes(); i++) {
            CraftingRecipe recipe = tileEntity.getRecipe(i);
            addRecipeLine(recipe.getResult());
        }
    }

    private void addRecipeLine(ItemStack craftingResult) {
        String readableName = BlockTools.getReadableName(craftingResult);
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
                container.getSlot(i).putStack(ItemStack.EMPTY);
            }
            keepItem.choice("All");
            internalRecipe.choice("Ext");
            return;
        }
        CraftingRecipe craftingRecipe = tileEntity.getRecipe(selected);
        CraftingInventory inv = craftingRecipe.getInventory();
        for (int i = 0; i < 9; i++) {
            container.getSlot(i).putStack(inv.getStackInSlot(i));
        }
        container.getSlot(9).putStack(craftingRecipe.getResult());
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
            public boolean canInteractWith(PlayerEntity var1) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; i++) {
            inv.setInventorySlotContents(i, container.getSlot(i).getStack());
        }

        // Compare current contents to avoid unneeded slot update.
        IRecipe recipe = CraftingRecipe.findRecipe(minecraft.world, inv);
        ItemStack newResult;
        if (recipe == null) {
            newResult = ItemStack.EMPTY;
        } else {
            newResult = recipe.getCraftingResult(inv);
        }
        container.getSlot(9).putStack(newResult);
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
            ItemStack oldStack = inv.getStackInSlot(i);
            ItemStack newStack = container.getSlot(i).getStack();
            if (!itemStacksEqual(oldStack, newStack)) {
                inv.setInventorySlotContents(i, newStack);
            }
        }

        // Compare current contents to avoid unneeded slot update.
        IRecipe recipe = CraftingRecipe.findRecipe(minecraft.world, inv);
        ItemStack newResult;
        if (recipe == null) {
            newResult = ItemStack.EMPTY;
        } else {
            newResult = recipe.getCraftingResult(inv);
        }
        ItemStack oldResult = container.getSlot(9).getStack();
        if (!itemStacksEqual(oldResult, newResult)) {
            container.getSlot(9).putStack(newResult);
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
            return !oldStack.isEmpty() && matches.isItemEqual(oldStack);
        }
    }

    private void sendChangeToServer(int index, CraftingInventory inv, ItemStack result, boolean keepOne,
                                    CraftingRecipe.CraftMode mode) {

        RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketCrafter(tileEntity.getPos(), index, inv,
                result, keepOne, mode));
    }

    private void updateButtons() {
        if (recipeList != null) {
            boolean selected = recipeList.getSelected() != -1;
            keepItem.enabled(selected);
            internalRecipe.enabled(selected);
            applyButton.enabled(selected);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float v, int x, int y) {
        if (window == null) {
            return;
        }
        updateButtons();

        drawWindow(matrixStack);
        updateEnergyBar(energyBar);

        // Draw the ghost slots here
        drawGhostSlots(matrixStack);
        testRecipe();
    }

    private void drawGhostSlots(MatrixStack matrixStack) {
        net.minecraft.client.renderer.RenderHelper.setupGui3DDiffuseLighting();
        matrixStack.push();
        matrixStack.translate(guiLeft, guiTop, 0.0F);
        RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        // @todo 1.15
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240 / 1.0F, 240 / 1.0F);

        ItemStackList ghostSlots = tileEntity.getGhostSlots();
        itemRenderer.zLevel = 100.0F;
        GlStateManager.enableDepthTest();
        GlStateManager.disableBlend();
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
                Slot slot = container.getSlot(slotIdx);
                if (!slot.getHasStack()) {
                    itemRenderer.renderItemAndEffectIntoGUI(stack, slot.xPos, slot.yPos);

                    RenderSystem.disableLighting();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepthTest();
                    this.minecraft.getTextureManager().bindTexture(iconGuiElements);
                    RenderHelper.drawTexturedModalRect(slot.xPos, slot.yPos, 14 * 16, 3 * 16, 16, 16);
                    GlStateManager.enableDepthTest();
                    GlStateManager.disableBlend();
                    RenderSystem.enableLighting();
                }
            }

        }
        itemRenderer.zLevel = 0.0F;

        matrixStack.pop();
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }
}
