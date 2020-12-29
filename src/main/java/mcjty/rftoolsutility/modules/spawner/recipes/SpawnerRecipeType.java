package mcjty.rftoolsutility.modules.spawner.recipes;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;

import static mcjty.rftoolsutility.modules.spawner.SpawnerModule.SPAWNER_RECIPE_TYPE_ID;

public class SpawnerRecipeType implements IRecipeType<SpawnerRecipe> {

    public void register() {
        Registry.register(Registry.RECIPE_TYPE, SPAWNER_RECIPE_TYPE_ID, this);
    }

}
