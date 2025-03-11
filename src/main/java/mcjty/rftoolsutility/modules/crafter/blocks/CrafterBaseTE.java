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
import mcjty.lib.crafting.BaseRecipe;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Cached;
import mcjty.lib.varia.InventoryTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.compat.JEIRecipeAcceptor;
import mcjty.rftoolsbase.modules.filter.items.FilterModuleItem;
import mcjty.rftoolsutility.modules.crafter.CrafterConfiguration;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.crafter.data.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer.*;
import static mcjty.rftoolsutility.modules.crafter.data.CraftMode.EXTC;
import static mcjty.rftoolsutility.modules.crafter.data.CraftMode.INT;

public class CrafterBaseTE extends TickingTileEntity implements JEIRecipeAcceptor {

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid(this::isItemValidForSlot)
            .onUpdate((slot, stack) -> clearCacheOrUpdateRecipe(slot))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<CrafterBaseTE, GenericItemHandler> ITEM_CAP = be -> be.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, CrafterConfiguration.MAXENERGY.get(), CrafterConfiguration.RECEIVEPERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<CrafterBaseTE, GenericEnergyStorage> ENERGY_CAP = be -> be.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<CrafterBaseTE, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<CrafterContainer>("Crafter")
            .containerSupplier((windowId, player) -> new CrafterContainer(windowId, CrafterContainer.CONTAINER_FACTORY.get(), be.getBlockPos(), be, player))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .data(CrafterModule.CRAFTER_DATA, CrafterData.STREAM_CODEC, CrafterData.CODEC)
            .setupSync(be);

    private final DefaultInfusable infusable = new DefaultInfusable(CrafterBaseTE.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<CrafterBaseTE, IInfusable> INFUSABLE_CAP = be -> be.infusable;

    private final Cached<Predicate<ItemStack>> filterCache = Cached.of(this::createFilterCache);

    @GuiValue
    public static final Value<CrafterBaseTE, String> SPEED_MODE = Value.createEnum("speedMode", SpeedMode.values(), CrafterBaseTE::getSpeedMode, CrafterBaseTE::setSpeedMode);

    // The selected recipe
    private int selected = -1;
    @GuiValue
    public static final Value<CrafterBaseTE, Integer> SELECTED = Value.create("selected", Type.INTEGER, CrafterBaseTE::getSelected, CrafterBaseTE::setSelected);

    // Values for the current selected recipe
    @GuiValue
    public static final Value<CrafterBaseTE, String> CRAFT_MODE = Value.createEnum("craftMode", CraftMode.values(), CrafterBaseTE::getCraftMode, CrafterBaseTE::setCraftMode);
    @GuiValue
    public static final Value<CrafterBaseTE, String> KEEP_ONE = Value.createEnum("keepOne", KeepMode.values(), CrafterBaseTE::getKeepOne, CrafterBaseTE::setKeepOne);

    public static CrafterBaseTE createTier1(BlockPos pos, BlockState state) {
        return new CrafterBaseTE(CrafterModule.CRAFTER1.be().get(), pos, state, 2);
    }

    public static CrafterBaseTE createTier2(BlockPos pos, BlockState state) {
        return new CrafterBaseTE(CrafterModule.CRAFTER2.be().get(), pos, state, 4);
    }

    public static CrafterBaseTE createTier3(BlockPos pos, BlockState state) {
        return new CrafterBaseTE(CrafterModule.CRAFTER3.be().get(), pos, state, 8);
    }

    // If the crafter tries to craft something, but there's nothing it can make,
    // this gets set to true, preventing further ticking. It gets cleared whenever
    // any of its inventories or recipes change.
    public boolean noRecipesWork = false;

    private static CraftingInput workInventory = CraftingInput.of(3, 3, createList());
    private static List<ItemStack> createList() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0 ; i < 9 ; i++) {
            list.add(ItemStack.EMPTY);
        }
        return list;
    }

    private void clearCacheOrUpdateRecipe(Integer slot) {
        noRecipesWork = false;
        if (slot == SLOT_FILTER_MODULE) {
            filterCache.clear();
        } else if (slot >= SLOT_CRAFTINPUT && slot < SLOT_CRAFTOUTPUT) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                items.add(this.items.getStackInSlot(i + SLOT_CRAFTINPUT));
            }
            CraftingInput input = CraftingInput.of(3, 3, items);
            Recipe recipe = CraftingRecipe.findRecipe(level, input);
            if (recipe != null) {
                ItemStack result = BaseRecipe.assemble(recipe, input, level);
                items.add(result);
            } else {
                items.add(ItemStack.EMPTY);
            }
        }
    }

    public CrafterBaseTE(BlockEntityType type, BlockPos pos, BlockState state, int supportedRecipes) {
        super(type, pos, state);
        List<CraftingRecipe> recipes = new ArrayList<>(supportedRecipes);
        for (int i = 0 ; i < supportedRecipes ; ++i) {
            recipes.add(new CraftingRecipe());
        }
        setData(CrafterModule.CRAFTER_DATA, CrafterData.createDefault().withRecipes(recipes));
    }

    public int getSelected() {
        return selected;
    }

    private void setSelected(int sel) {
        if (sel == selected) {
            return;
        }
        List<CraftingRecipe> recipes = getData(CrafterModule.CRAFTER_DATA).recipes();
        if (sel < 0 || sel >= recipes.size()) {
            selected = -1;
        } else {
            selected = sel;
        }
        if (selected < 0) {
            for (int i = 0; i < 10; ++i) {
                items.setStackInSlot(CrafterContainer.SLOT_CRAFTINPUT + i, ItemStack.EMPTY);
            }
        } else {
            CraftingRecipe recipe = recipes.get(selected);
            items.setStackInSlot(CrafterContainer.SLOT_CRAFTOUTPUT, recipe.getResult());
            CraftingInput inv = recipe.getInventory();
            int size = inv.size();
            for (int i = 0; i < size; ++i) {
                items.setStackInSlot(CrafterContainer.SLOT_CRAFTINPUT + i, inv.getItem(i));
            }
        }
        setChanged();
    }

    private void applyRecipe() {
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        List<CraftingRecipe> recipes = data.recipes();
        if (selected < 0 || selected >= recipes.size()) {
            return;
        }
        CraftingRecipe recipe = recipes.get(selected);
        ItemStack[] recipeItems = new ItemStack[9];
        for (int i = 0 ; i < 9 ; i++) {
            recipeItems[i] = items.getStackInSlot(i + SLOT_CRAFTINPUT).copy();
        }
        recipe.setRecipe(recipeItems, items.getStackInSlot(SLOT_CRAFTOUTPUT).copy());
        setData(CrafterModule.CRAFTER_DATA, data.withRecipes(recipes));
        markDirtyClient();
    }

    private CraftMode getCraftMode() {
        List<CraftingRecipe> recipes = getData(CrafterModule.CRAFTER_DATA).recipes();
        if (selected < 0 || selected >= recipes.size()) {
            return CraftMode.EXT;
        } else {
            return recipes.get(selected).getCraftMode();
        }
    }

    private void setCraftMode(CraftMode mode) {
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        List<CraftingRecipe> recipes = data.recipes();
        if (selected >= 0 && selected < recipes.size()) {
            if (recipes.get(selected).getCraftMode() != mode) {
                recipes.get(selected).setCraftMode(mode);
                setData(CrafterModule.CRAFTER_DATA, data.withRecipes(recipes));
            }
        }
    }

    private KeepMode getKeepOne() {
        List<CraftingRecipe> recipes = getData(CrafterModule.CRAFTER_DATA).recipes();
        if (selected < 0 || selected >= recipes.size()) {
            return KeepMode.ALL;
        } else {
            return recipes.get(selected).getKeepOne();
        }
    }

    private void setKeepOne(KeepMode keepOne) {
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        List<CraftingRecipe> recipes = data.recipes();
        if (selected >= 0 && selected < recipes.size()) {
            if (recipes.get(selected).getKeepOne() != keepOne) {
                recipes.get(selected).setKeepOne(keepOne);
                setData(CrafterModule.CRAFTER_DATA, data.withRecipes(recipes));
            }
        }
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }


    public List<ItemStack> getGhostSlots() {
        return getData(CrafterModule.CRAFTER_DATA).ghostSlots();
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
        List<CraftingRecipe> recipes = getData(CrafterModule.CRAFTER_DATA).recipes();
        return recipes.size();
    }

    public SpeedMode getSpeedMode() {
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        return data.speedMode();
    }

    public void setSpeedMode(SpeedMode speedMode) {
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        data = data.withSpeedMode(speedMode);
        setData(CrafterModule.CRAFTER_DATA, data);
        markDirtyClient();
    }

    public CraftingRecipe getRecipe(int index) {
        List<CraftingRecipe> recipes = getData(CrafterModule.CRAFTER_DATA).recipes();
        return recipes.get(index);
    }

    public Predicate<ItemStack> createFilterCache() {
        return FilterModuleItem.getCache(items.getStackInSlot(CrafterContainer.SLOT_FILTER_MODULE));
    }

    @Override
    protected void tickServer() {
        if (!isMachineEnabled() || noRecipesWork) {
            return;
        }

        // 0%: rf -> rf
        // 100%: rf -> rf / 2
        int defaultCost = CrafterConfiguration.rfPerOperation.get();
        int rf = (int) (defaultCost * (2.0f - infusable.getInfusedFactor()) / 2.0f);

        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        int steps = data.speedMode() == SpeedMode.FAST ? CrafterConfiguration.speedOperations.get() : 1;
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

        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        for (CraftingRecipe craftingRecipe : data.recipes()) {
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
            result = BaseRecipe.assemble(recipe, workInventory, level);
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

        Recipe recipe = craftingRecipe.getCachedRecipe(level);
        int w = 3;
        int h = 3;
        if (recipe instanceof ShapedRecipe) {
            w = ((ShapedRecipe) recipe).getWidth();
            h = ((ShapedRecipe) recipe).getHeight();
        }

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        List<ItemStack> list = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            list.add(ItemStack.EMPTY);
        }

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
                                    list.set(y * 3 + x, copy);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        workInventory = CraftingInput.of(3, 3, list);
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
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        List<ItemStack> ghostSlots = data.ghostSlots();
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
        setData(CrafterModule.CRAFTER_DATA, data.withGhostSlots(ghostSlots));
        noRecipesWork = false;
        markDirtyClient();
    }

    private void forgetItems() {
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        List<ItemStack> ghostSlots = data.ghostSlots();
        for (int i = 0; i < ghostSlots.size(); i++) {
            ghostSlots.set(i, ItemStack.EMPTY);
        }
        setData(CrafterModule.CRAFTER_DATA, data.withGhostSlots(ghostSlots));
        noRecipesWork = false;
        markDirtyClient();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energyStorage.load(tag, "energy", provider);
        items.load(tag, "items", provider);
        infusable.load(tag, "infusable");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        energyStorage.save(tag, "energy", provider);
        items.save(tag, "items", provider);
        infusable.save(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(CrafterModule.ITEM_CRAFTER_DATA);
        if (data != null) {
            setData(CrafterModule.CRAFTER_DATA, data);
        }
        energyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
        infusable.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(CrafterModule.ITEM_CRAFTER_DATA, getData(CrafterModule.CRAFTER_DATA));
        energyStorage.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
        infusable.collectImplicitComponents(builder);
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
        CrafterData data = getData(CrafterModule.CRAFTER_DATA);
        List<ItemStack> ghostSlots = data.ghostSlots();
        if (slot >= CrafterContainer.SLOT_BUFFER && slot < CrafterContainer.SLOT_BUFFEROUT) {
            ItemStack ghostSlot = ghostSlots.get(slot - CrafterContainer.SLOT_BUFFER);
            if (!ghostSlot.isEmpty()) {
                if (!ItemStack.isSameItem(ghostSlot, stack)) {
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
                if (!ItemStack.isSameItem(ghostSlot, stack)) {
                    return false;
                }
            }
        } else if (slot == CrafterContainer.SLOT_FILTER_MODULE) {
            return stack.getItem() instanceof FilterModuleItem;
        }
        return true;
    }

}
