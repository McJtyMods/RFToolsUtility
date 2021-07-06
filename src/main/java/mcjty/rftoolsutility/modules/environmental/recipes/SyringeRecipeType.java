package mcjty.rftoolsutility.modules.environmental.recipes;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;

import static mcjty.rftoolsutility.modules.environmental.EnvironmentalModule.SYRINGE_RECIPE_TYPE_ID;

public class SyringeRecipeType implements IRecipeType<SyringeBasedRecipe> {

    public void register() {
        Registry.register(Registry.RECIPE_TYPE, SYRINGE_RECIPE_TYPE_ID, this);
    }

}
