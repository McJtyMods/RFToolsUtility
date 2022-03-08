package mcjty.rftoolsutility.modules.spawner.recipes;

import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnerRecipes {

    // Indexed by mob ID
    private static final Map<String, MobData> mobData = new HashMap<>();

    public static MobData getMobData(Level world, String id) {
        if (mobData.isEmpty()) {
            loadRecipes(world);
        }
        MobData data = mobData.get(id);
        if (data == null) {
            return null;
        }
        return data;
    }

    private static void loadRecipes(Level world) {
        mobData.clear();
        List<SpawnerRecipe> recipes = world.getRecipeManager().getAllRecipesFor(SpawnerModule.SPAWNER_RECIPE_TYPE);
        for (SpawnerRecipe recipe : recipes) {
            mobData.put(recipe.getEntity().toString(), MobData.create()
                    .item1(recipe.getItem1())
                    .item2(recipe.getItem2())
                    .item3(recipe.getItem3())
                    .spawnRf(recipe.getSpawnRf())
                );
        }
    }

    public static class MobSpawnAmount {
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

        public Float match(ItemStack stack) {
            if (object.isEmpty()) {
                // Living?
                Item item = stack.getItem();
                Set<TagKey<Item>> tags = item.builtInRegistryHolder().tags().collect(Collectors.toSet());
                if (tags.contains(SpawnerConfiguration.TAG_HIGHYIELD)) {
                    return 1.5f;
                } else if (tags.contains(SpawnerConfiguration.TAG_AVERAGEYIELD)) {
                    return 1.0f;
                } else if (tags.contains(SpawnerConfiguration.TAG_LOWYIELD)) {
                    return 0.5f;
                } else {
                    return 0.0f;
                }
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
