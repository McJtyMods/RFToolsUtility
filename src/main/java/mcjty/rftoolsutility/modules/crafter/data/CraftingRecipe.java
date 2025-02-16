package mcjty.rftoolsutility.modules.crafter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.InventoryTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CraftingRecipe {
    private CraftingInput inv = CraftingInput.of(3, 3, createList());
    private static List<ItemStack> createList() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0 ; i < 9 ; i++) {
            list.add(ItemStack.EMPTY);
        }
        return list;
    }
//    private final CraftingContainer inv = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
//        @Override
//        public boolean stillValid(@Nonnull Player var1) {
//            return false;
//        }
//
//        @Override
//        public ItemStack quickMoveStack(Player player, int slot) {
//            return ItemStack.EMPTY;
//        }
//    }, 3, 3);
    private ItemStack result = ItemStack.EMPTY;

    private boolean recipePresent = false;
    private Recipe recipe = null;

    private KeepMode keepOne = KeepMode.ALL;
    private CraftMode craftMode = CraftMode.EXT;

    public static final Codec<CraftingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("inv").forGetter(o -> o.inv.items()),
            ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(o -> o.result),
            KeepMode.CODEC.fieldOf("keepOne").forGetter(CraftingRecipe::getKeepOne),
            CraftMode.CODEC.fieldOf("craftMode").forGetter(CraftingRecipe::getCraftMode)
    ).apply(instance, (itemStacks, itemStack, keepMode, craftMode) -> {
        CraftingRecipe recipe = new CraftingRecipe();
        recipe.inv = CraftingInput.of(3, 3, itemStacks);
        recipe.result = itemStack;
        recipe.keepOne = keepMode;
        recipe.craftMode = craftMode;
        recipe.recipePresent = false;
        return recipe;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.inv.items(),
            ItemStack.OPTIONAL_STREAM_CODEC, o -> o.result,
            KeepMode.STREAM_CODEC, CraftingRecipe::getKeepOne,
            CraftMode.STREAM_CODEC, CraftingRecipe::getCraftMode,
            (itemStacks, itemStack, keepMode, craftMode) -> {
                CraftingRecipe recipe = new CraftingRecipe();
                recipe.inv = CraftingInput.of(3, 3, itemStacks);
                recipe.result = itemStack;
                recipe.keepOne = keepMode;
                recipe.craftMode = craftMode;
                recipe.recipePresent = false;
                return recipe;
            }
    );

    // Compressed information about the recipe
    public static class CompressedIngredient {
        private final ItemStack stack;
        // How matchers of this stack should be distributed over the crafting grid (each integer is an amount in the correspoding slot)
        private final int[] gridDistribution = new int[9];

        public CompressedIngredient(ItemStack stack) {
            this.stack = stack;
            Arrays.fill(gridDistribution, 0);
        }

        public ItemStack getStack() {
            return stack;
        }

        public int[] getGridDistribution() {
            return gridDistribution;
        }
    }
    private List<CompressedIngredient> compressedIngredients = null;

    /**
     * Cached function that returns the ingredients of the recipe in a compact
     * form (i.e. duplicates put together)
     */
    public List<CompressedIngredient> getCompressedIngredients() {
        if (compressedIngredients == null) {
            compressedIngredients = new ArrayList<>();
            for (int i = 0 ; i < inv.size() ; i++) {
                ItemStack stack = inv.getItem(i);
                if (!stack.isEmpty()) {
                    boolean found  = false;
                    for (CompressedIngredient ingredient : compressedIngredients) {
                        if (InventoryTools.isItemStackConsideredEqual(stack, ingredient.getStack())) {
                            ingredient.getStack().grow(stack.getCount());
                            ingredient.getGridDistribution()[i] += stack.getCount();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        CompressedIngredient ingredient = new CompressedIngredient(stack.copy());
                        ingredient.getGridDistribution()[i] += stack.getCount();
                        compressedIngredients.add(ingredient);
                    }
                }
            }
        }
        return compressedIngredients;
    }

    public static Recipe findRecipe(Level world, CraftingInput inv) {
        RecipeManager recipeManager = world.getRecipeManager();
        Optional<RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>> recipeFor = recipeManager.getRecipeFor(RecipeType.CRAFTING, inv, world);
        if (recipeFor.isPresent()) {
            return recipeFor.get().value();
        }
        return null;
    }

    public void setRecipe(ItemStack[] items, ItemStack result) {
        inv = CraftingInput.of(3, 3, Arrays.asList(items));
        this.result = result;
        recipePresent = false;
    }

    public CraftingInput getInventory() {
        return inv;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public ItemStack getResult() {
        return result;
    }

    public Recipe getCachedRecipe(Level world) {
        if (!recipePresent) {
            recipePresent = true;
            recipe = findRecipe(world, inv);
            compressedIngredients = null;
        }
        return recipe;
    }

    public KeepMode getKeepOne() {
        return keepOne;
    }

    public void setKeepOne(KeepMode keepOne) {
        this.keepOne = keepOne;
    }

    public CraftMode getCraftMode() {
        return craftMode;
    }

    public void setCraftMode(CraftMode craftMode) {
        this.craftMode = craftMode;
    }
}
