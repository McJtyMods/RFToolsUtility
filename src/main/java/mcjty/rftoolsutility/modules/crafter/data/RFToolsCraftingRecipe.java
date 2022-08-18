package mcjty.rftoolsutility.modules.crafter.data;

import mcjty.lib.varia.InventoryTools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RFToolsCraftingRecipe {
    private final CraftingContainer inv = new CraftingContainer(new AbstractContainerMenu(null, -1) {
        @Override
        public boolean stillValid(@Nonnull Player var1) {
            return false;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slot) {
            return ItemStack.EMPTY;
        }
    }, 3, 3);
    private ItemStack result = ItemStack.EMPTY;

    private boolean recipePresent = false;
    private Recipe recipe = null;

    private KeepMode keepOne = KeepMode.ALL;
    private CraftMode craftMode = CraftMode.EXT;

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
            for (int i = 0 ; i < inv.getContainerSize() ; i++) {
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

    public static Recipe findRecipe(Level world, CraftingContainer inv) {
        RecipeManager recipeManager = world.getRecipeManager();
        for (Recipe r : recipeManager.getRecipes()) {
            if (r != null && RecipeType.CRAFTING.equals(r.getType()) && r.matches(inv, world)) {
                return r;
            }
        }
        return null;
    }

    public void readFromNBT(CompoundTag tagCompound) {
        ListTag nbtTagList = tagCompound.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < nbtTagList.size(); i++) {
            inv.setItem(i, ItemStack.of(nbtTagList.getCompound(i)));
        }
        CompoundTag resultCompound = tagCompound.getCompound("Result");
        result = ItemStack.of(resultCompound);
        keepOne = tagCompound.getBoolean("Keep") ? KeepMode.KEEP : KeepMode.ALL;
        craftMode = CraftMode.values()[tagCompound.getByte("Int")];
        recipePresent = false;
    }

    public void writeToNBT(CompoundTag tagCompound) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0 ; i < inv.getContainerSize() ; i++) {
            ItemStack stack = inv.getItem(i);
            CompoundTag tag = new CompoundTag();
            if (!stack.isEmpty()) {
                stack.save(tag);
            }
            nbtTagList.add(tag);
        }
        CompoundTag resultCompound = new CompoundTag();
        if (!result.isEmpty()) {
            result.save(resultCompound);
        }
        tagCompound.put("Result", resultCompound);
        tagCompound.put("Items", nbtTagList);
        tagCompound.putBoolean("Keep", keepOne == KeepMode.KEEP);
        tagCompound.putByte("Int", (byte) craftMode.ordinal());
    }

    public void setRecipe(ItemStack[] items, ItemStack result) {
        for (int i = 0 ; i < inv.getContainerSize() ; i++) {
            inv.setItem(i, items[i]);
        }
        this.result = result;
        recipePresent = false;
    }

    public CraftingContainer getInventory() {
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
