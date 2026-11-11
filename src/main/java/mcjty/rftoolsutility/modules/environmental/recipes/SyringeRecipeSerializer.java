package mcjty.rftoolsutility.modules.environmental.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class SyringeRecipeSerializer {

    private static final MapCodec<SyringeBasedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShapedRecipe.MAP_CODEC.codec().fieldOf("recipe").forGetter(SyringeBasedRecipe::getRecipe),
            Identifier.CODEC.fieldOf("mob").forGetter(SyringeBasedRecipe::getMobId),
            Codec.INT.fieldOf("syringe").forGetter(SyringeBasedRecipe::getSyringeIndex)
    ).apply(instance, SyringeBasedRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, SyringeBasedRecipe> STREAM_CODEC = StreamCodec.composite(
            ShapedRecipe.STREAM_CODEC, SyringeBasedRecipe::getRecipe,
            Identifier.STREAM_CODEC, SyringeBasedRecipe::getMobId,
            ByteBufCodecs.INT, SyringeBasedRecipe::getSyringeIndex,
            SyringeBasedRecipe::new);

    private SyringeRecipeSerializer() {
    }

    public static RecipeSerializer<SyringeBasedRecipe> create() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
