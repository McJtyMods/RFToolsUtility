package mcjty.rftoolsutility.modules.environmental.recipes;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyringeRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SyringeBasedRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Nonnull
    @Override
    public SyringeBasedRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject root) {
        ShapedRecipe shapedRecipe = serializer.fromJson(recipeId, root);
        String mob = root.get("mob").getAsString();
        return new SyringeBasedRecipe(shapedRecipe, new ResourceLocation(mob));
    }

    @Nullable
    @Override
    public SyringeBasedRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        ShapedRecipe shapedRecipe = serializer.fromNetwork(recipeId, buffer);
        ResourceLocation mobId = buffer.readResourceLocation();
        return new SyringeBasedRecipe(shapedRecipe, mobId);
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull SyringeBasedRecipe recipe) {
        serializer.toNetwork(buffer, recipe);
        buffer.writeResourceLocation(recipe.getMobId());
    }
}
