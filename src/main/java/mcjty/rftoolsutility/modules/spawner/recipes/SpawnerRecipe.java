package mcjty.rftoolsutility.modules.spawner.recipes;

import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SpawnerRecipe implements IRecipe<IInventory> {

    private final ResourceLocation id;
    private final SpawnerRecipes.MobSpawnAmount item1;
    private final SpawnerRecipes.MobSpawnAmount item2;
    private final SpawnerRecipes.MobSpawnAmount item3;
    private final int spawnRf;
    private final ResourceLocation entity;

    public SpawnerRecipe(ResourceLocation id, SpawnerRecipes.MobSpawnAmount item1, SpawnerRecipes.MobSpawnAmount item2, SpawnerRecipes.MobSpawnAmount item3, int spawnRf,
                         ResourceLocation entity) {
        this.id = id;
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.spawnRf = spawnRf;
        this.entity = entity;
    }

    public SpawnerRecipes.MobSpawnAmount getItem1() {
        return item1;
    }

    public SpawnerRecipes.MobSpawnAmount getItem2() {
        return item2;
    }

    public SpawnerRecipes.MobSpawnAmount getItem3() {
        return item3;
    }

    public int getSpawnRf() {
        return spawnRf;
    }

    public ResourceLocation getEntity() {
        return entity;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SpawnerModule.SPAWNER_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return SpawnerModule.SPAWNER_RECIPE_TYPE;
    }
}
