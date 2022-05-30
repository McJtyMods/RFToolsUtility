package mcjty.rftoolsutility.modules.environmental.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyringeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SyringeBasedRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Nonnull
    @Override
    public SyringeBasedRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject root) {
        ShapedRecipe shapedRecipe = serializer.fromJson(recipeId, root);
        String mob = root.get("mob").getAsString();
        int syringe = root.get("syringe").getAsInt();
        return new SyringeBasedRecipe(shapedRecipe, new ResourceLocation(mob), syringe);
    }

    @Nullable
    @Override
    public SyringeBasedRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        ShapedRecipe shapedRecipe = serializer.fromNetwork(recipeId, buffer);
        ResourceLocation mobId = buffer.readResourceLocation();
        int syringeIndex = buffer.readInt();
        return new SyringeBasedRecipe(shapedRecipe, mobId, syringeIndex);
    }

    @Override
    public void toNetwork(@Nonnull PacketBuffer buffer, @Nonnull SyringeBasedRecipe recipe) {
        serializer.toNetwork(buffer, recipe);
        buffer.writeResourceLocation(recipe.getMobId());
        buffer.writeInt(recipe.getSyringeIndex());
    }
}
