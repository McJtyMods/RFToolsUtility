package mcjty.rftoolsutility.modules.environmental.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.crafting.BaseShapedRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class SyringeRecipeSerializer implements RecipeSerializer<SyringeBasedRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    public static final MapCodec<SyringeBasedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipe.CODEC.fieldOf("recipe").forGetter(recipe -> recipe),
            ResourceLocation.CODEC.fieldOf("mob").forGetter(SyringeBasedRecipe::getMobId),
            Codec.INT.fieldOf("syringe").forGetter(SyringeBasedRecipe::getSyringeIndex)
    ).apply(instance, (recipe, mob, index) -> new SyringeBasedRecipe((BaseShapedRecipe) recipe, mob, index)));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyringeBasedRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                ShapedRecipe.STREAM_CODEC.encode(buf, recipe);
                buf.writeResourceLocation(recipe.getMobId());
                buf.writeInt(recipe.getSyringeIndex());
            },
            buf -> {
                Recipe<?> recipe = ShapedRecipe.STREAM_CODEC.decode(buf);
                ShapedRecipe sr = (ShapedRecipe) recipe;
                ResourceLocation mobId = buf.readResourceLocation();
                int syringeIndex = buf.readInt();
                return new SyringeBasedRecipe((BaseShapedRecipe) sr, mobId, syringeIndex);
            }
    );

    @Override
    public MapCodec<SyringeBasedRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SyringeBasedRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
