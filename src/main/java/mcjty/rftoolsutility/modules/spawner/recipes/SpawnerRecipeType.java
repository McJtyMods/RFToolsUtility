package mcjty.rftoolsutility.modules.spawner.recipes;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.Registry;

import static mcjty.rftoolsutility.modules.spawner.SpawnerModule.SPAWNER_RECIPE_TYPE_ID;

public class SpawnerRecipeType implements RecipeType<SpawnerRecipe> {

    public void register() {
        Registry.register(Registry.RECIPE_TYPE, SPAWNER_RECIPE_TYPE_ID, this);
    }

}
