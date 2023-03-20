package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.UndoableItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Cached;
import mcjty.lib.varia.InventoryTools;
import mcjty.lib.varia.ItemStackList;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.compat.JEIRecipeAcceptor;
import mcjty.rftoolsbase.modules.filter.items.FilterModuleItem;
import mcjty.rftoolsutility.modules.crafter.CrafterConfiguration;
import mcjty.rftoolsutility.modules.crafter.data.CraftMode;
import mcjty.rftoolsutility.modules.crafter.data.CraftingRecipe;
import mcjty.rftoolsutility.modules.crafter.data.KeepMode;
import mcjty.rftoolsutility.modules.crafter.data.SpeedMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

import static mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer.*;
import static mcjty.rftoolsutility.modules.crafter.data.CraftMode.EXTC;
import static mcjty.rftoolsutility.modules.crafter.data.CraftMode.INT;

public class CrafterBaseTE extends TickingTileEntity implements JEIRecipeAcceptor {

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid(this::isItemValidForSlot)
            .onUpdate((slot, stack) -> clearCacheOrUpdateRecipe(slot))
            .build();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, CrafterConfiguration.MAXENERGY.get(), CrafterConfiguration.RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<CrafterContainer>("Crafter")
            .containerSupplier((windowId, player) -> new CrafterContainer(windowId, CrafterContainer.CONTAINER_FACTORY.get(), getBlockPos(), CrafterBaseTE.this, player))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(CrafterBaseTE.this));

    private final ItemStackList ghostSlots = ItemStackList.create(CrafterContainer.BUFFER_SIZE + CrafterContainer.BUFFEROUT_SIZE);

    private final CraftingRecipe[] recipes;

    private final Cached<Predicate<ItemStack>> filterCache = Cached.of(this::createFilterCache);

    @GuiValue
    private SpeedMode speedMode = SpeedMode.SLOW;

    // The selected recipe
    private int selected = -1;
    @GuiValue
    public static final Value<CrafterBaseTE, Integer> SELECTED = Value.create("selected", Type.INTEGER, CrafterBaseTE::getSelected, CrafterBaseTE::setSelected);

    // Values for the current selected recipe
    @GuiValue
    public static final Value<CrafterBaseTE, String> CRAFT_MODE = Value.createEnum("craftMode", CraftMode.values(), CrafterBaseTE::getCraftMode, CrafterBaseTE::setCraftMode);
    @GuiValue
    public static final Value<CrafterBaseTE, String> KEEP_ONE = Value.createEnum("keepOne", KeepMode.values(), CrafterBaseTE::getKeepOne, CrafterBaseTE::setKeepOne);

    // If the crafter tries to craft something, but there's nothing it can make,
    // this gets set to true, preventing further ticking. It gets cleared whenever
    // any of its inventories or recipes change.
    public boolean noRecipesWork = false;

    private final CraftingContainer workInventory = new CraftingContainer(new AbstractContainerMenu(null, -1) {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean stillValid(Player var1) {
            return false;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slot) {
            return ItemStack.EMPTY;
        }
    }, 3, 3);

    private void clearCacheOrUpdateRecipe(Integer slot) {
        noRecipesWork = false;
        if (slot == SLOT_FILTER_MODULE) {
            filterCache.clear();
        } else if (slot >= SLOT_CRAFTINPUT && slot < SLOT_CRAFTOUTPUT) {
            for (int i = 0; i < 9; i++) {
                workInventory.setItem(i, items.getStackInSlot(i + SLOT_CRAFTINPUT).copy());
            }
            Recipe recipe = CraftingRecipe.findRecipe(level, workInventory);
            if (recipe != null) {
                ItemStack result = recipe.assemble(workInventory, level.registryAccess());
                items.setStackInSlot(SLOT_CRAFTOUTPUT, result);
            } else {
                items.setStackInSlot(SLOT_CRAFTOUTPUT, ItemStack.EMPTY);
            }
        }
    }

    public CrafterBaseTE(BlockEntityType type, BlockPos pos, BlockState state, int supportedRecipes) {
        super(type, pos, state);
        recipes = new CraftingRecipe[supportedRecipes];
        for (int i = 0; i < recipes.length; ++i) {
            recipes[i] = new CraftingRecipe();
        }
    }

    public int getSelected() {
        return selected;
    }

    private void setSelected(int sel) {
        if (sel == selected) {
            return;
        }
        if (sel < 0 || sel >= recipes.length) {
            selected = -1;
        } else {
            selected = sel;
        }
        if (selected < 0) {
            for (int i = 0; i < 10; ++i) {
                items.setStackInSlot(CrafterContainer.SLOT_CRAFTINPUT + i, ItemStack.EMPTY);
            }
        } else {
            CraftingRecipe recipe = recipes[selected];
            items.setStackInSlot(CrafterContainer.SLOT_CRAFTOUTPUT, recipe.getResult());
            CraftingContainer inv = recipe.getInventory();
            int size = inv.getContainerSize();
            for (int i = 0; i < size; ++i) {
                items.setStackInSlot(CrafterContainer.SLOT_CRAFTINPUT + i, inv.getItem(i));
            }
        }
        setChanged();
    }

    private void applyRecipe() {
        if (selected < 0 || selected >= recipes.length) {
            return;
        }
        CraftingRecipe recipe = recipes[selected];
        ItemStack[] recipeItems = new ItemStack[9];
        for (int i = 0 ; i < 9 ; i++) {
            recipeItems[i] = items.getStackInSlot(i + SLOT_CRAFTINPUT).copy();
        }
        recipe.setRecipe(recipeItems, items.getStackInSlot(SLOT_CRAFTOUTPUT).copy());
        markDirtyClient();
    }

    private CraftMode getCraftMode() {
        if (selected < 0 || selected >= recipes.length) {
            return CraftMode.EXT;
        } else {
            return recipes[selected].getCraftMode();
        }
    }

    private void setCraftMode(CraftMode mode) {
        if (selected >= 0 && selected < recipes.length) {
            if (recipes[selected].getCraftMode() != mode) {
                recipes[selected].setCraftMode(mode);
                setChanged();
            }
        }
    }

    private KeepMode getKeepOne() {
        if (selected < 0 || selected >= recipes.length) {
            return KeepMode.ALL;
        } else {
            return recipes[selected].getKeepOne();
        }
    }

    private void setKeepOne(KeepMode keepOne) {
        if (selected >= 0 && selected < recipes.length) {
            if (recipes[selected].getKeepOne() != keepOne) {
                recipes[selected].setKeepOne(keepOne);
            }
            setChanged();
        }
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }


    public ItemStackList getGhostSlots() {
        return ghostSlots;
    }

    @Override
    public void setGridContents(List<ItemStack> stacks) {
        items.setStackInSlot(CrafterContainer.SLOT_CRAFTOUTPUT, stacks.get(0));
        for (int i = 1; i < stacks.size(); i++) {
            items.setStackInSlot(CrafterContainer.SLOT_CRAFTINPUT + i - 1, stacks.get(i));
        }
        setChanged();
    }

    public int getSupportedRecipes() {
        return recipes.length;
    }

    public SpeedMode getSpeedMode() {
        return speedMode;
    }

    public CraftingRecipe getRecipe(int index) {
        return recipes[index];
    }

    public Predicate<ItemStack> createFilterCache() {
        return FilterModuleItem.getCache(items.getStackInSlot(CrafterContainer.SLOT_FILTER_MODULE));
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        CompoundTag info = getOrCreateInfo(tagCompound);
        writeGhostBufferToNBT(info);
        writeRecipesToNBT(info);
//        saveRSMode(info);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        CompoundTag info = tagCompound.getCompound("Info");
        readGhostBufferFromNBT(info);
        readRecipesFromNBT(info);
//        loadRSMode(info);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        readGhostBufferFromNBT(info);
        readRecipesFromNBT(info);
        speedMode = SpeedMode.values()[info.getByte("speedMode")];
    }

    private void readGhostBufferFromNBT(CompoundTag tagCompound) {
        ListTag bufferTagList = tagCompound.getList("GItems", Tag.TAG_COMPOUND);
        for (int i = 0; i < bufferTagList.size(); i++) {
            ghostSlots.set(i, ItemStack.of(bufferTagList.getCompound(i)));
        }
    }

    private void readRecipesFromNBT(CompoundTag tagCompound) {
        ListTag recipeTagList = tagCompound.getList("Recipes", Tag.TAG_COMPOUND);
        for (int i = 0; i < recipeTagList.size(); i++) {
            recipes[i].readFromNBT(recipeTagList.getCompound(i));
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        writeGhostBufferToNBT(info);
        writeRecipesToNBT(info);
        info.putByte("speedMode", (byte) speedMode.ordinal());
    }

    private void writeGhostBufferToNBT(CompoundTag tagCompound) {
        ListTag bufferTagList = new ListTag();
        for (ItemStack stack : ghostSlots) {
            CompoundTag CompoundNBT = new CompoundTag();
            if (!stack.isEmpty()) {
                stack.save(CompoundNBT);
            }
            bufferTagList.add(CompoundNBT);
        }
        tagCompound.put("GItems", bufferTagList);
    }

    private void writeRecipesToNBT(CompoundTag tagCompound) {
        ListTag recipeTagList = new ListTag();
        for (CraftingRecipe recipe : recipes) {
            CompoundTag CompoundNBT = new CompoundTag();
            recipe.writeToNBT(CompoundNBT);
            recipeTagList.add(CompoundNBT);
        }
        tagCompound.put("Recipes", recipeTagList);
    }

    @Override
    protected void tickServer() {
        if (!isMachineEnabled() || noRecipesWork) {
            return;
        }

        // 0%: rf -> rf
        // 100%: rf -> rf / 2
        int defaultCost = CrafterConfiguration.rfPerOperation.get();
        int rf = infusableHandler.map(inf -> (int) (defaultCost * (2.0f - inf.getInfusedFactor()) / 2.0f)).orElse(defaultCost);

        int steps = speedMode == SpeedMode.FAST ? CrafterConfiguration.speedOperations.get() : 1;
        if (rf > 0) {
            steps = (int) Math.min(steps, energyStorage.getEnergy() / rf);
        }

        int i;
        for (i = 0; i < steps; ++i) {
            if (!craftOneCycle()) {
                noRecipesWork = true;
                break;
            }
        }
        rf *= i;
        if (rf > 0) {
            energyStorage.consumeEnergy(rf);
        }
    }

    private boolean craftOneCycle() {
        boolean craftedAtLeastOneThing = false;

        for (CraftingRecipe craftingRecipe : recipes) {
            if (craftOneItem(craftingRecipe)) {
                craftedAtLeastOneThing = true;
            }
        }

        return craftedAtLeastOneThing;
    }

    private boolean craftOneItem(CraftingRecipe craftingRecipe) {
        Recipe recipe = craftingRecipe.getCachedRecipe(level);
        if (recipe == null) {
            return false;
        }

        UndoableItemHandler undoHandler = new UndoableItemHandler(items);

        // 'testAndConsume' will setup the workInventory and return true if it matches
        if (!testAndConsume(craftingRecipe, undoHandler)) {
            undoHandler.restore();
            return false;
        }

        ItemStack result = ItemStack.EMPTY;
        try {
            result = recipe.assemble(workInventory, level.registryAccess());
        } catch (RuntimeException e) {
            // Ignore this error for now to make sure we don't crash on bad recipes.
            Logging.logError("Problem with recipe!", e);
        }

        // Try to merge the output. If there is something that doesn't fit we undo everything.
        CraftMode mode = craftingRecipe.getCraftMode();
        if (!result.isEmpty() && placeResult(mode, undoHandler, result)) {
            List<ItemStack> remaining = recipe.getRemainingItems(workInventory);
            CraftMode remainingMode = mode == EXTC ? INT : mode;
            for (ItemStack s : remaining) {
                if (!s.isEmpty()) {
                    if (!placeResult(remainingMode, undoHandler, s)) {
                        // Not enough room.
                        undoHandler.restore();
                        return false;
                    }
                }
            }
            return true;
        } else {
            // We don't have place. Undo the operation.
            undoHandler.restore();
            return false;
        }
    }

    private boolean testAndConsume(CraftingRecipe craftingRecipe, UndoableItemHandler undoHandler) {
        int keep = craftingRecipe.getKeepOne() == KeepMode.KEEP ? 1 : 0;
        for (int i = 0; i < workInventory.getContainerSize(); i++) {
            workInventory.setItem(i, ItemStack.EMPTY);
        }

        Recipe recipe = craftingRecipe.getCachedRecipe(level);
        int w = 3;
        int h = 3;
        if (recipe instanceof ShapedRecipe) {
            w = ((ShapedRecipe) recipe).getRecipeWidth();
            h = ((ShapedRecipe) recipe).getRecipeHeight();
        }

        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int index = y * w + x;
                if (index < ingredients.size()) {
                    Ingredient ingredient = ingredients.get(index);
                    if (ingredient != Ingredient.EMPTY) {
                        for (int j = 0; j < CrafterContainer.BUFFER_SIZE; j++) {
                            int slotIdx = CrafterContainer.SLOT_BUFFER + j;
                            ItemStack input = undoHandler.getStackInSlot(slotIdx);
                            if (!input.isEmpty() && input.getCount() > keep) {
                                if (ingredient.test(input)) {
                                    undoHandler.remember(slotIdx);
                                    ItemStack copy = input.split(1);
                                    workInventory.setItem(y * 3 + x, copy);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return recipe.matches(workInventory, level);
    }

    private boolean placeResult(CraftMode mode, IItemHandlerModifiable undoHandler, ItemStack result) {
        int start;
        int stop;
        if (mode == INT) {
            start = CrafterContainer.SLOT_BUFFER;
            stop = CrafterContainer.SLOT_BUFFER + CrafterContainer.BUFFER_SIZE;
        } else {
            // EXT and EXTC are handled the same here
            start = CrafterContainer.SLOT_BUFFEROUT;
            stop = CrafterContainer.SLOT_BUFFEROUT + CrafterContainer.BUFFEROUT_SIZE;
        }
        ItemStack remaining = InventoryTools.insertItemRanged(undoHandler, result, start, stop, true);
        if (remaining.isEmpty()) {
            InventoryTools.insertItemRanged(undoHandler, result, start, stop, false);
            return true;
        }
        return false;
    }

    private void rememberItems() {
        for (int i = 0; i < ghostSlots.size(); i++) {
            int slotIdx;
            if (i < CrafterContainer.BUFFER_SIZE) {
                slotIdx = i + CrafterContainer.SLOT_BUFFER;
            } else {
                slotIdx = i + CrafterContainer.SLOT_BUFFEROUT - CrafterContainer.BUFFER_SIZE;
            }
            if (!items.getStackInSlot(slotIdx).isEmpty()) {
                ItemStack stack = items.getStackInSlot(slotIdx).copy();
                stack.setCount(1);
                ghostSlots.set(i, stack);
            }
        }
        noRecipesWork = false;
        markDirtyClient();
    }

    private void forgetItems() {
        for (int i = 0; i < ghostSlots.size(); i++) {
            ghostSlots.set(i, ItemStack.EMPTY);
        }
        noRecipesWork = false;
        markDirtyClient();
    }

    @ServerCommand
    public static final Command<?> CMD_REMEMBER = Command.<CrafterBaseTE>create("crafter.remember",
            (te, player, params) -> te.rememberItems());

    @ServerCommand
    public static final Command<?> CMD_FORGET = Command.<CrafterBaseTE>create("crafter.forget",
            (te, player, params) -> te.forgetItems());

    @ServerCommand
    public static final Command<?> CMD_APPLY = Command.<CrafterBaseTE>create("crafter.apply",
            (te, player, params) -> te.applyRecipe());

    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot >= CrafterContainer.SLOT_CRAFTINPUT && slot <= CrafterContainer.SLOT_CRAFTOUTPUT) {
            return false;
        }
        if (slot >= CrafterContainer.SLOT_BUFFER && slot < CrafterContainer.SLOT_BUFFEROUT) {
            ItemStack ghostSlot = ghostSlots.get(slot - CrafterContainer.SLOT_BUFFER);
            if (!ghostSlot.isEmpty()) {
                if (!ghostSlot.sameItem(stack)) {
                    return false;
                }
            }
            ItemStack filterModule = items.getStackInSlot(CrafterContainer.SLOT_FILTER_MODULE);
            if (!filterModule.isEmpty()) {
                if (filterCache.get() != null) {
                    return filterCache.get().test(stack);
                }
            }
        } else if (slot >= CrafterContainer.SLOT_BUFFEROUT && slot < CrafterContainer.SLOT_FILTER_MODULE) {
            ItemStack ghostSlot = ghostSlots.get(slot - CrafterContainer.SLOT_BUFFEROUT + CrafterContainer.BUFFER_SIZE);
            if (!ghostSlot.isEmpty()) {
                if (!ghostSlot.sameItem(stack)) {
                    return false;
                }
            }
        } else if (slot == CrafterContainer.SLOT_FILTER_MODULE) {
            return stack.getItem() instanceof FilterModuleItem;
        }
        return true;
    }

}
