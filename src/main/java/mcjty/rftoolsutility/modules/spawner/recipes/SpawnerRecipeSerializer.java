package mcjty.rftoolsutility.modules.spawner.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnerRecipeSerializer implements RecipeSerializer<SpawnerRecipe> {

    @Nonnull
    @Override
    public SpawnerRecipe fromJson(@Nonnull ResourceLocation recipeId, JsonObject root) {
        ResourceLocation id = new ResourceLocation(root.getAsJsonPrimitive("id").getAsString());
        int power = root.getAsJsonPrimitive("power").getAsInt();

        SpawnerRecipes.MobSpawnAmount item1 = readSpawnAmount(root, "item1");
        SpawnerRecipes.MobSpawnAmount item2 = readSpawnAmount(root, "item2");
        SpawnerRecipes.MobSpawnAmount item3 = readSpawnAmount(root, "item3");

        ResourceLocation entity = new ResourceLocation(root.getAsJsonPrimitive("entity").getAsString());

        return new SpawnerRecipe(id, item1, item2, item3, power, entity);
    }

    private SpawnerRecipes.MobSpawnAmount readSpawnAmount(JsonObject root, String tag) {
        SpawnerRecipes.MobSpawnAmount item1;
        JsonObject item1Object = root.getAsJsonObject(tag);
        float amount = item1Object.get("amount").getAsFloat();
        if (item1Object.has("living")) {
            item1 = new SpawnerRecipes.MobSpawnAmount(Ingredient.EMPTY, amount);
        } else {
            Ingredient ingredient = Ingredient.fromJson(item1Object.get("ingredient"));
            item1 = new SpawnerRecipes.MobSpawnAmount(ingredient, amount);
        }
        return item1;
    }

    @Nullable
    @Override
    public SpawnerRecipe fromNetwork(@Nonnull ResourceLocation recipeId, FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        int power = buffer.readInt();

        SpawnerRecipes.MobSpawnAmount item1 = readSpawnAmount(buffer);
        SpawnerRecipes.MobSpawnAmount item2 = readSpawnAmount(buffer);
        SpawnerRecipes.MobSpawnAmount item3 = readSpawnAmount(buffer);

        ResourceLocation entity = buffer.readResourceLocation();

        return new SpawnerRecipe(id, item1, item2, item3, power, entity);
    }

    private SpawnerRecipes.MobSpawnAmount readSpawnAmount(FriendlyByteBuf buffer) {
        SpawnerRecipes.MobSpawnAmount item1;
        float amount = buffer.readFloat();
        if (buffer.readBoolean()) {
            // Has ingredient
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            item1 = new SpawnerRecipes.MobSpawnAmount(ingredient, amount);
        } else {
            item1 = new SpawnerRecipes.MobSpawnAmount(Ingredient.EMPTY, amount);
        }
        return item1;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, SpawnerRecipe recipe) {
        buffer.writeResourceLocation(recipe.getId());
        buffer.writeInt(recipe.getSpawnRf());

        writeMobAmount(buffer, recipe.getItem1());
        writeMobAmount(buffer, recipe.getItem2());
        writeMobAmount(buffer, recipe.getItem3());

        buffer.writeResourceLocation(recipe.getEntity());
    }

    private void writeMobAmount(FriendlyByteBuf buffer, SpawnerRecipes.MobSpawnAmount item) {
        boolean hasIngredient = item.getObject() != null;
        buffer.writeFloat(item.getAmount());
        buffer.writeBoolean(hasIngredient);
        if (hasIngredient) {
            item.getObject().toNetwork(buffer);
        }
    }

}
