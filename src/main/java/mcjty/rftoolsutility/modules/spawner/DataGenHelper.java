package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.HashMap;
import java.util.Map;

public class DataGenHelper {

    public static Map<String, SpawnerRecipes.MobData> getDefaultMobData() {
        Map<String, SpawnerRecipes.MobData> defaultMobData = new HashMap<>();
        defaultMobData.put(Tools.getId(EntityType.BAT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(100)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.FEATHERS), .1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10f)));
        defaultMobData.put(Tools.getId(EntityType.BLAZE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.RODS_BLAZE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.CAVE_SPIDER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.STRING), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(Tools.getId(EntityType.CHICKEN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.FEATHERS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(Tools.getId(EntityType.PARROT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.FEATHERS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(Tools.getId(EntityType.COW).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.CREEPER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.GUNPOWDER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.ENDER_DRAGON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(100000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.EXPERIENCE_BOTTLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.END_STONE), 100))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 200)));
        defaultMobData.put(Tools.getId(EntityType.ENDERMAN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.ENDER_PEARLS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.END_STONE), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 40)));
        defaultMobData.put(Tools.getId(EntityType.GHAST).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.GHAST_TEAR), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 50)));
        defaultMobData.put(Tools.getId(EntityType.HORSE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.SKELETON_HORSE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.LLAMA).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.TRADER_LLAMA).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.MULE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.DONKEY).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.PANDA).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.BAMBOO), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.BEE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.HONEY_BLOCK), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.ZOMBIE_HORSE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.IRON_GOLEM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.INGOTS_IRON), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 6.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FLOWERS), 0.5f)));
        defaultMobData.put(Tools.getId(EntityType.MAGMA_CUBE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.MAGMA_CREAM), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(Tools.getId(EntityType.MOOSHROOM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.OCELOT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.CAT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.FOX).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.PIG).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.ZOGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 70)));
        defaultMobData.put(Tools.getId(EntityType.HOGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 70)));
        defaultMobData.put(Tools.getId(EntityType.SHEEP).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.WOOL), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.SKELETON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.SLIME).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.SLIMEBALLS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(Tools.getId(EntityType.SNOW_GOLEM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.SNOWBALL), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(Tools.getId(EntityType.SPIDER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.STRING), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(Tools.getId(EntityType.SQUID).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.INK_SAC), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(Tools.getId(EntityType.VILLAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.BOOK), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.ZOMBIE_VILLAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.WANDERING_TRADER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(20000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.BOOKSHELF), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 40)));
        defaultMobData.put(Tools.getId(EntityType.WITCH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.GLASS_BOTTLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.WITHER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(20000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.NETHER_STAR), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.SOUL_SAND), 0.5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 100)));
        defaultMobData.put(Tools.getId(EntityType.WOLF).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.ZOMBIFIED_PIGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.PIGLIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.PIGLIN_BRUTE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.STRIDER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.PILLAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.VINDICATOR).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.EVOKER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.ILLUSIONER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.RAVAGER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(4000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.SADDLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 60)));
        defaultMobData.put(Tools.getId(EntityType.PHANTOM).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.PHANTOM_MEMBRANE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.ZOMBIE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.DROWNED).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 90)));
        defaultMobData.put(Tools.getId(EntityType.GIANT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.HUSK).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.GUARDIAN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.PRISMARINE_SHARD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.ELDER_GUARDIAN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(5000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.PRISMARINE_SHARD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 60)));
        defaultMobData.put(Tools.getId(EntityType.SHULKER).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.ENDER_PEARLS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.END_STONE), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.ENDERMITE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.ENDER_PEARLS), 0.05f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.END_STONE), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(Tools.getId(EntityType.SILVERFISH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.INGOTS_IRON), 0.05f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(Tools.getId(EntityType.RABBIT).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(300)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.RABBIT_STEW), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(Tools.getId(EntityType.POLAR_BEAR).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.DOLPHIN).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.SALMON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.COD).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.PUFFERFISH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.TROPICAL_FISH).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.VEX).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.IRON_SWORD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.TURTLE).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.SEAGRASS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(Tools.getId(EntityType.WITHER_SKELETON).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(Tools.getId(EntityType.STRAY).toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.of(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        return defaultMobData;
    }


}
