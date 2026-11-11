package mcjty.rftoolsutility.modules.spawner.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SpawnerRecipeSerializer  {

    public static final MapCodec<SpawnerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(recipe -> recipe.getId()),
            SpawnerRecipes.MobSpawnAmount.CODEC.fieldOf("item1").forGetter(recipe -> recipe.getItem1()),
            SpawnerRecipes.MobSpawnAmount.CODEC.fieldOf("item2").forGetter(recipe -> recipe.getItem2()),
            SpawnerRecipes.MobSpawnAmount.CODEC.fieldOf("item3").forGetter(recipe -> recipe.getItem3()),
            Codec.INT.fieldOf("power").forGetter(recipe -> recipe.getSpawnRf()),
            Identifier.CODEC.fieldOf("entity").forGetter(SpawnerRecipe::getEntity)
    ).apply(instance, SpawnerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnerRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, d -> d.getId(),
            SpawnerRecipes.MobSpawnAmount.STREAM_CODEC, d -> d.getItem1(),
            SpawnerRecipes.MobSpawnAmount.STREAM_CODEC, d -> d.getItem2(),
            SpawnerRecipes.MobSpawnAmount.STREAM_CODEC, d -> d.getItem3(),
            ByteBufCodecs.INT, d -> d.getSpawnRf(),
            Identifier.STREAM_CODEC, d -> d.getEntity(),
            SpawnerRecipe::new);

    public static RecipeSerializer<SpawnerRecipe> create() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}
