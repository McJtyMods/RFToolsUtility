package mcjty.rftoolsutility.craftinggrid;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipe {
    private CraftingInventory inv = new CraftingInventory(new Container(null, -1) {
        @Override
        public boolean canInteractWith(PlayerEntity var1) {
            return false;
        }
    }, 3, 3);
    private ItemStack result = ItemStack.EMPTY;

    private boolean recipePresent = false;
    private IRecipe recipe = null;

    // Compressed information about the recipe
    public static class CompressedIngredient {
        private final ItemStack stack;
        // How matchers of this stack should be distributed over the crafting grid (each integer is an amount in the correspoding slot)
        private int[] gridDistribution = new int[9];

        public CompressedIngredient(ItemStack stack) {
            this.stack = stack;
            for (int i = 0 ; i < gridDistribution.length ; i++) {
                gridDistribution[i] = 0;
            }
        }

        public ItemStack getStack() {
            return stack;
        }

        public int[] getGridDistribution() {
            return gridDistribution;
        }
    }
    private List<CompressedIngredient> compressedIngredients = null;

    private boolean keepOne = false;

    public enum CraftMode {
        EXT("Ext"),
        INT("Int"),
        EXTC("ExtC");

        private final String description;

        CraftMode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private CraftMode craftMode = CraftMode.EXT;

    /**
     * Cached function that returns the ingredients of the recipe in a compact
     * form (i.e. duplicates put together)
     */
    public List<CompressedIngredient> getCompressedIngredients() {
        if (compressedIngredients == null) {
            compressedIngredients = new ArrayList<>();
            for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    boolean found  = false;
                    for (CompressedIngredient ingredient : compressedIngredients) {
                        if (InventoryHelper.isItemStackConsideredEqual(stack, ingredient.getStack())) {
                            ingredient.getStack().grow(stack.getCount());
                            ingredient.getGridDistribution()[i] += stack.getCount();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        CompressedIngredient ingredient = new CompressedIngredient(stack);
                        ingredient.getGridDistribution()[i] += stack.getCount();
                        compressedIngredients.add(ingredient);
                    }
                }
            }
        }
        return compressedIngredients;
    }

    public static IRecipe findRecipe(World world, CraftingInventory inv) {
        for (IRecipe r : McJtyLib.proxy.getRecipeManager(world).getRecipes()) {
            if (r != null && IRecipeType.CRAFTING.equals(r.getType()) && r.matches(inv, world)) {
                return r;
            }
        }
        return null;
    }

    public void readFromNBT(CompoundNBT tagCompound) {
        ListNBT nbtTagList = tagCompound.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtTagList.size(); i++) {
            CompoundNBT CompoundNBT = nbtTagList.getCompound(i);
            inv.setInventorySlotContents(i, ItemStack.read(CompoundNBT));
        }
        CompoundNBT resultCompound = tagCompound.getCompound("Result");
        if (resultCompound != null) {
            result = ItemStack.read(resultCompound);
        } else {
            result = ItemStack.EMPTY;
        }
        keepOne = tagCompound.getBoolean("Keep");
        craftMode = CraftMode.values()[tagCompound.getByte("Int")];
        recipePresent = false;
    }

    public void writeToNBT(CompoundNBT tagCompound) {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            CompoundNBT CompoundNBT = new CompoundNBT();
            if (!stack.isEmpty()) {
                stack.write(CompoundNBT);
            }
            nbtTagList.add(CompoundNBT);
        }
        CompoundNBT resultCompound = new CompoundNBT();
        if (!result.isEmpty()) {
            result.write(resultCompound);
        }
        tagCompound.put("Result", resultCompound);
        tagCompound.put("Items", nbtTagList);
        tagCompound.putBoolean("Keep", keepOne);
        tagCompound.putByte("Int", (byte) craftMode.ordinal());
    }

    public void setRecipe(ItemStack[] items, ItemStack result) {
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            inv.setInventorySlotContents(i, items[i]);
        }
        this.result = result;
        recipePresent = false;
    }

    public CraftingInventory getInventory() {
        return inv;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public ItemStack getResult() {
        return result;
    }

    public IRecipe getCachedRecipe(World world) {
        if (!recipePresent) {
            recipePresent = true;
            recipe = findRecipe(world, inv);
            compressedIngredients = null;
        }
        return recipe;
    }

    public boolean isKeepOne() {
        return keepOne;
    }

    public void setKeepOne(boolean keepOne) {
        this.keepOne = keepOne;
    }

    public CraftMode getCraftMode() {
        return craftMode;
    }

    public void setCraftMode(CraftMode craftMode) {
        this.craftMode = craftMode;
    }
}
