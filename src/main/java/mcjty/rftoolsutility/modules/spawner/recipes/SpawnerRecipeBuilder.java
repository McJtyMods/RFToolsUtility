package mcjty.rftoolsutility.modules.spawner.recipes;

import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class SpawnerRecipeBuilder {

    private final ResourceLocation id;
    private final ResourceLocation entity;
    private int power = 0;
    private SpawnerRecipes.MobSpawnAmount item1;
    private SpawnerRecipes.MobSpawnAmount item2;
    private SpawnerRecipes.MobSpawnAmount item3;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private ShapedRecipeBuilder builder;

    private SpawnerRecipeBuilder(EntityType entity) {
        this.id = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, Tools.getId(entity).getNamespace() + "_" + Tools.getId(entity).getPath());
        this.entity = Tools.getId(entity);
        builder = new ShapedRecipeBuilder(RecipeCategory.MISC, Items.AIR, 0);
    }

    public static SpawnerRecipeBuilder create(EntityType entity) {
        return new SpawnerRecipeBuilder(entity);
    }

    public SpawnerRecipeBuilder power(int power) {
        this.power = power;
        return this;
    }

    public SpawnerRecipeBuilder item1(Ingredient ingredient, float amount) {
        item1 = new SpawnerRecipes.MobSpawnAmount(ingredient, amount);
        return this;
    }

    public SpawnerRecipeBuilder item2(Ingredient ingredient, float amount) {
        item2 = new SpawnerRecipes.MobSpawnAmount(ingredient, amount);
        return this;
    }

    public SpawnerRecipeBuilder item3(Ingredient ingredient, float amount) {
        item3 = new SpawnerRecipes.MobSpawnAmount(ingredient, amount);
        return this;
    }

    public void build(RecipeOutput consumerIn) {
        consumerIn.accept(id, new SpawnerRecipe(id, item1, item2, item3, power, entity), null);
    }
}
