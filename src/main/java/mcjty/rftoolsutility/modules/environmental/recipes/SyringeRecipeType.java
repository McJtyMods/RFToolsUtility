package mcjty.rftoolsutility.modules.environmental.recipes;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.Registry;

import static mcjty.rftoolsutility.modules.environmental.EnvironmentalModule.SYRINGE_RECIPE_TYPE_ID;

public class SyringeRecipeType implements RecipeType<SyringeBasedRecipe> {

    public void register() {
        Registry.register(Registry.RECIPE_TYPE, SYRINGE_RECIPE_TYPE_ID, this);
    }

}
