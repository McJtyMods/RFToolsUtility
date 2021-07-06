package mcjty.rftoolsutility.modules.environmental.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class SyringeBasedRecipe extends ShapedRecipe {

    public SyringeBasedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, group, width, height, ingredients, result);
    }

    public SyringeBasedRecipe(ShapedRecipe other) {
        super(other.getId(), other.getGroup(), other.getWidth(), other.getHeight(), other.getIngredients(), other.getResultItem());
    }

}
