package mcjty.rftoolsutility.modules.spawner.recipes;

import mcjty.lib.crafting.BaseRecipe;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class SpawnerRecipe implements BaseRecipe<CraftingInput> {

    private final Identifier id;
    private final SpawnerRecipes.MobSpawnAmount item1;
    private final SpawnerRecipes.MobSpawnAmount item2;
    private final SpawnerRecipes.MobSpawnAmount item3;
    private final int spawnRf;
    private final Identifier entity;

    public SpawnerRecipe(Identifier id, SpawnerRecipes.MobSpawnAmount item1, SpawnerRecipes.MobSpawnAmount item2, SpawnerRecipes.MobSpawnAmount item3, int spawnRf,
                         Identifier entity) {
        this.id = id;
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.spawnRf = spawnRf;
        this.entity = entity;
    }

    public Identifier getId() {
        return id;
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

    public Identifier getEntity() {
        return entity;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public RecipeSerializer<SpawnerRecipe> getSerializer() {
        return SpawnerModule.SPAWNER_SERIALIZER.get();
    }

    @Nonnull
    @Override
    public RecipeType<SpawnerRecipe> getType() {
        return SpawnerModule.SPAWNER_RECIPE_TYPE.get();
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput) {
        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return false;
    }
}
