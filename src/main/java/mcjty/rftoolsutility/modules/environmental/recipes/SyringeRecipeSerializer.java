package mcjty.rftoolsutility.modules.environmental.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class SyringeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SyringeBasedRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    public SyringeBasedRecipe fromJson(ResourceLocation recipeId, JsonObject root) {
        ShapedRecipe shapedRecipe = serializer.fromJson(recipeId, root);
        return new SyringeBasedRecipe(shapedRecipe);
    }

    @Nullable
    @Override
    public SyringeBasedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe shapedRecipe = serializer.fromNetwork(recipeId, buffer);
        return new SyringeBasedRecipe(shapedRecipe);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, SyringeBasedRecipe recipe) {
        serializer.toNetwork(buffer, recipe);
    }
}
