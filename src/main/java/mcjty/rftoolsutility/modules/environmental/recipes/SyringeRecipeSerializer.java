package mcjty.rftoolsutility.modules.environmental.recipes;

import com.google.gson.JsonObject;
import mcjty.lib.crafting.BaseRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyringeRecipeSerializer implements RecipeSerializer<SyringeBasedRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Nonnull
    @Override
    public SyringeBasedRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject root) {
        ShapedRecipe shapedRecipe = serializer.fromJson(recipeId, root);
        String mob = root.get("mob").getAsString();
        int syringe = root.get("syringe").getAsInt();
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
        return new SyringeBasedRecipe(shapedRecipe, new ResourceLocation(mob), syringe, result);
    }

    @Nullable
    @Override
    public SyringeBasedRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        ShapedRecipe shapedRecipe = serializer.fromNetwork(recipeId, buffer);
        ResourceLocation mobId = buffer.readResourceLocation();
        int syringeIndex = buffer.readInt();
        return new SyringeBasedRecipe(shapedRecipe, mobId, syringeIndex, BaseRecipe.getResultItem(shapedRecipe, null));
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull SyringeBasedRecipe recipe) {
        serializer.toNetwork(buffer, recipe);
        buffer.writeResourceLocation(recipe.getMobId());
        buffer.writeInt(recipe.getSyringeIndex());
    }
}
