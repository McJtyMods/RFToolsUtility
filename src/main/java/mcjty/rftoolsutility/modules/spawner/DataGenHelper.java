package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class DataGenHelper {

    public static Map<String, SpawnerRecipes.MobData> getDefaultMobData() {
        Map<String, SpawnerRecipes.MobData> defaultMobData = new HashMap<>();
        defaultMobData.put(Tools.getId(EntityTypes.BAT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(100)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.FEATHERS)), .1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10f)));
        defaultMobData.put(Tools.getId(EntityTypes.BLAZE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BLAZE_RODS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.CAVE_SPIDER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.STRINGS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10)));
        defaultMobData.put(Tools.getId(EntityTypes.CHICKEN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.FEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 15)));
        defaultMobData.put(Tools.getId(EntityTypes.PARROT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.FEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 15)));
        defaultMobData.put(Tools.getId(EntityTypes.COW).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.CREEPER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.GUNPOWDERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.ENDER_DRAGON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(100000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.EXPERIENCE_BOTTLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.END_STONE), 100))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 200)));
        defaultMobData.put(Tools.getId(EntityTypes.ENDERMAN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.ENDER_PEARLS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.END_STONE), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 40)));
        defaultMobData.put(Tools.getId(EntityTypes.GHAST).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.GHAST_TEAR), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 50)));
        defaultMobData.put(Tools.getId(EntityTypes.HORSE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.SKELETON_HORSE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BONES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.LLAMA).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.TRADER_LLAMA).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.MULE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.DONKEY).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.PANDA).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.BAMBOO), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.BEE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.HONEY_BLOCK), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.ZOMBIE_HORSE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.IRON_GOLEM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.IRON_INGOTS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 6.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(BlockItemTags.FLOWERS.item())), 0.5f)));
        defaultMobData.put(Tools.getId(EntityTypes.MAGMA_CUBE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.MAGMA_CREAM), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10)));
        defaultMobData.put(Tools.getId(EntityTypes.MOOSHROOM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.OCELOT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.CAT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.FOX).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BONES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.PIG).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.ZOGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 70)));
        defaultMobData.put(Tools.getId(EntityTypes.HOGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.LEATHERS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 70)));
        defaultMobData.put(Tools.getId(EntityTypes.SHEEP).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.WOOL)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.SKELETON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BONES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.SLIME).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.SLIME_BALLS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 15)));
        defaultMobData.put(Tools.getId(EntityTypes.SNOW_GOLEM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.SNOWBALL), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 15)));
        defaultMobData.put(Tools.getId(EntityTypes.SPIDER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.STRING), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 15)));
        defaultMobData.put(Tools.getId(EntityTypes.SQUID).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.INK_SAC), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10)));
        defaultMobData.put(Tools.getId(EntityTypes.VILLAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.BOOK), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.ZOMBIE_VILLAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.WANDERING_TRADER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(20000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.BOOKSHELF), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 40)));
        defaultMobData.put(Tools.getId(EntityTypes.WITCH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.GLASS_BOTTLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.WITHER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(20000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHER_STAR), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.SOUL_SAND), 0.5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 100)));
        defaultMobData.put(Tools.getId(EntityTypes.WOLF).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BONES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.ZOMBIFIED_PIGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.GOLD_NUGGETS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.PIGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.GOLD_NUGGETS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.PIGLIN_BRUTE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.GOLD_NUGGETS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.STRIDER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.GOLD_NUGGETS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.PILLAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.EMERALD_GEMS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.VINDICATOR).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.EMERALD_GEMS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.EVOKER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.EMERALD_GEMS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.ILLUSIONER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.EMERALD_GEMS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.RAVAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(4000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.SADDLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 60)));
        defaultMobData.put(Tools.getId(EntityTypes.PHANTOM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.PHANTOM_MEMBRANE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.ZOMBIE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.DROWNED).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 90)));
        defaultMobData.put(Tools.getId(EntityTypes.GIANT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.HUSK).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.GUARDIAN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.PRISMARINE_SHARD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.ELDER_GUARDIAN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(5000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.PRISMARINE_SHARD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 60)));
        defaultMobData.put(Tools.getId(EntityTypes.SHULKER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.ENDER_PEARLS)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.END_STONE), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.ENDERMITE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.ENDER_PEARLS)), 0.05f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.END_STONE), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10)));
        defaultMobData.put(Tools.getId(EntityTypes.SILVERFISH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.IRON_INGOTS)), 0.05f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10)));
        defaultMobData.put(Tools.getId(EntityTypes.RABBIT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(300)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.RABBIT_STEW), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 10)));
        defaultMobData.put(Tools.getId(EntityTypes.POLAR_BEAR).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.DOLPHIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.SALMON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.COD).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.PUFFERFISH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.TROPICAL_FISH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ItemTags.FISHES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.VEX).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.IRON_SWORD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.TURTLE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.SEAGRASS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        defaultMobData.put(Tools.getId(EntityTypes.WITHER_SKELETON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BONES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 30)));
        defaultMobData.put(Tools.getId(EntityTypes.STRAY).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(DataGen.recipeItems().getOrThrow(ConventionalItemTags.BONES)), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(null, 20)));
        return defaultMobData;
    }


}
