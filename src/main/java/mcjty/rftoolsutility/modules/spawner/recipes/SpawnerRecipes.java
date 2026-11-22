package mcjty.rftoolsutility.modules.spawner.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.TagTools;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public class SpawnerRecipes {

    private static final Map<RecipeManager, Map<Identifier, SpawnerRecipe>> RECIPES = new WeakHashMap<>();

    @Nullable
    public static synchronized SpawnerRecipe getRecipe(Level level, Identifier id) {
        if (!(level instanceof ServerLevel serverLevel) || id == null) {
            return null;
        }
        // Minecraft replaces its recipe manager when it reloads datapacks.
        return RECIPES.computeIfAbsent(serverLevel.getServer().getRecipeManager(), SpawnerRecipes::loadRecipes).get(id);
    }

    private static Map<Identifier, SpawnerRecipe> loadRecipes(RecipeManager manager) {
        Map<Identifier, SpawnerRecipe> recipes = new HashMap<>();
        for (RecipeHolder<SpawnerRecipe> holder : manager.recipeMap().byType(SpawnerModule.SPAWNER_RECIPE_TYPE.get())) {
            SpawnerRecipe recipe = holder.value();
            recipes.put(recipe.getEntity(), recipe);
        }
        return recipes;
    }

    public static class MobSpawnAmount {
        public static final Codec<MobSpawnAmount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.optionalFieldOf("object").forGetter(value -> Optional.ofNullable(value.object)),
                Codec.FLOAT.fieldOf("amount").forGetter(MobSpawnAmount::getAmount)
        ).apply(instance, (object, amount) -> MobSpawnAmount.create(object.orElse(null), amount)));
        public static final StreamCodec<RegistryFriendlyByteBuf, MobSpawnAmount> STREAM_CODEC = StreamCodec.composite(
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, d -> Optional.ofNullable(d.object),
                ByteBufCodecs.FLOAT, d -> d.amount,
                (object, amount) -> MobSpawnAmount.create(object.orElse(null), amount));

        private final Ingredient object;
        private final float amount;

        public MobSpawnAmount(Ingredient object, float amount) {
            this.object = object;
            this.amount = amount;
        }

        public static MobSpawnAmount create(Ingredient object, float amount) {
            return new MobSpawnAmount(object, amount);
        }

        public Ingredient getObject() {
            return object;
        }

        public float getAmount() {
            return amount;
        }

        public boolean isLivingMatter() {
            return object == null;
        }

        public Float match(ItemStack stack) {
            if (isLivingMatter()) {
                Item item = stack.getItem();
                Collection<TagKey<Item>> tags = TagTools.getTags(item);
                if (tags.contains(SpawnerConfiguration.TAG_HIGHYIELD)) {
                    return 1.5f;
                } else if (tags.contains(SpawnerConfiguration.TAG_AVERAGEYIELD)) {
                    return 1.0f;
                } else if (tags.contains(SpawnerConfiguration.TAG_LOWYIELD)) {
                    return 0.5f;
                }
                return null;
            }
            if (object.test(stack)) {
                return 1.0f;
            }
            return null;
        }
    }

    public static class MobData {
        private MobSpawnAmount item1;
        private MobSpawnAmount item2;
        private MobSpawnAmount item3;
        private int spawnRf;

        public static MobData create() {
            return new MobData();
        }

        public MobData item1(MobSpawnAmount item1) {
            this.item1 = item1;
            return this;
        }

        public MobData item2(MobSpawnAmount item2) {
            this.item2 = item2;
            return this;
        }

        public MobData item3(MobSpawnAmount item3) {
            this.item3 = item3;
            return this;
        }

        public MobData spawnRf(int spawnRf) {
            this.spawnRf = spawnRf;
            return this;
        }

        public MobSpawnAmount getItem1() {
            return item1;
        }

        public MobSpawnAmount getItem2() {
            return item2;
        }

        public MobSpawnAmount getItem3() {
            return item3;
        }

        public MobSpawnAmount getItem(int index) {
            switch (index) {
                case 0:
                    return item1;
                case 1:
                    return item2;
                case 2:
                    return item3;
                default:
                    throw new IllegalStateException("Bad index for MobData.getItem()!");
            }
        }

        public int getSpawnRf() {
            return spawnRf;
        }
    }
}
