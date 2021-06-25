package mcjty.rftoolsutility.modules.spawner.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SpawnerRecipeBuilder {

    private final ResourceLocation id;
    private final ResourceLocation entity;
    private int power = 0;
    private SpawnerRecipes.MobSpawnAmount item1;
    private SpawnerRecipes.MobSpawnAmount item2;
    private SpawnerRecipes.MobSpawnAmount item3;

    private SpawnerRecipeBuilder(EntityType entity) {
        this.id = new ResourceLocation(RFToolsUtility.MODID, entity.getRegistryName().getNamespace() + "_" + entity.getRegistryName().getPath());
        this.entity = entity.getRegistryName();
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

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        consumerIn.accept(new Result(new SpawnerRecipe(id, item1, item2, item3, power, entity)));
    }


    public class Result implements IFinishedRecipe {

        private final SpawnerRecipe recipe;

        public Result(SpawnerRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("id", new JsonPrimitive(recipe.getId().toString()));
            json.add("power", new JsonPrimitive(recipe.getSpawnRf()));
            json.add("entity", new JsonPrimitive(recipe.getEntity().toString()));
            serializeItem(json, recipe.getItem1(), "item1");
            serializeItem(json, recipe.getItem2(), "item2");
            serializeItem(json, recipe.getItem3(), "item3");
        }

        private void serializeItem(JsonObject json, SpawnerRecipes.MobSpawnAmount item1, String tag) {
            JsonObject itemObject = new JsonObject();
            itemObject.add("amount", new JsonPrimitive(item1.getAmount()));
            if (item1.getObject() != null && item1.getObject() != Ingredient.EMPTY) {
                itemObject.add("ingredient", item1.getObject().toJson());
            } else {
                itemObject.add("living", new JsonPrimitive(true));
            }
            json.add(tag, itemObject);
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public IRecipeSerializer<?> getType() {
            return SpawnerModule.SPAWNER_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }

}
