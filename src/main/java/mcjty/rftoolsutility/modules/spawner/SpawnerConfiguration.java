package mcjty.rftoolsutility.modules.spawner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.nio.file.Path;
import java.util.*;

public class SpawnerConfiguration {
    public static final String CATEGORY_SPAWNER = "spawner";
    public static final String CATEGORY_MOBDATA = "mobdata";
    public static final String CATEGORY_POWER = "power";
    public static final String CATEGORY_ITEMS = "items";

    public static final ResourceLocation LIVING = new ResourceLocation("rftoolsutility", "living/living");
    public static final ResourceLocation LOWYIELD = new ResourceLocation("rftoolsutility", "living/lowyield");
    public static final ResourceLocation HIGHYIELD = new ResourceLocation("rftoolsutility", "living/highyield");
    public static final ResourceLocation AVERAGEYIELD = new ResourceLocation("rftoolsutility", "living/averageyield");

    public static final ITag.INamedTag<Item> TAG_LIVING = tagItem(LIVING);
    public static final ITag.INamedTag<Item> TAG_LOWYIELD = tagItem(LOWYIELD);
    public static final ITag.INamedTag<Item> TAG_HIGHYIELD = tagItem(HIGHYIELD);
    public static final ITag.INamedTag<Item> TAG_AVERAGEYIELD = tagItem(AVERAGEYIELD);

    private static ITag.INamedTag<Item> tagItem(ResourceLocation id) {
        return ItemTags.makeWrapperTag(id.toString());
    }

    // Indexed by mob ID
    private static final Map<String, MobData> mobData = new HashMap<>();
    private static MobData unknownMobDefault;

    public static final int MATERIALTYPE_KEY = 0;
    public static final int MATERIALTYPE_BULK = 1;
    public static final int MATERIALTYPE_LIVING = 2;

    public static int SPAWNER_MAXENERGY = 200000;
    public static int SPAWNER_RECEIVEPERTICK = 2000;

    public static int BEAMER_MAXENERGY = 200000;
    public static int BEAMER_RECEIVEPERTICK = 1000;
    public static int beamRfPerObject = 2000;
    public static int beamBlocksPerSend = 1;
    public static int maxBeamDistance = 8;
    public static int maxMatterStorage = 64 * 100;

    public static ForgeConfigSpec.IntValue maxMobInjections;        // Maximum amount of injections we need to do a full mob extraction.

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> spawnRfConfig;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> spawnItems;

    public static void initMobConfigs(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for the spawner system").push(CATEGORY_MOBDATA);

        // Get defaultMobData
        Map<String, MobData> mobDataMap = getDefaultMobData();

        List<String> spawnRf = new ArrayList<>();
        List<String> amounts = new ArrayList<>();
        for (Map.Entry<String, MobData> entry : mobDataMap.entrySet()) {
            String key = entry.getKey();
            MobData data = entry.getValue();
            spawnRf.add(key + "=" + data.spawnRf);
            for (int i = 0 ; i < 3 ; i++) {
                MobSpawnAmount item = data.getItem(i);
                if (item != null && item.object != null) {
                    amounts.add(key + "=" + item.serialize());
                }
            }
        }

        SERVER_BUILDER.push(CATEGORY_POWER);
        spawnRfConfig = SERVER_BUILDER.comment("Spawn power amount for a given mob")
                .defineList("power", spawnRf, s -> s instanceof String);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push(CATEGORY_ITEMS);
        spawnItems = SERVER_BUILDER.comment("Spawn item amounts for a given mob")
                .defineList("item", amounts, s -> s instanceof String);
        SERVER_BUILDER.pop();


        SERVER_BUILDER.pop();
    }

    private static Map<String, MobData> getDefaultMobData() {
        Map<String, MobData> defaultMobData = new HashMap<>();
        defaultMobData.put(EntityType.BAT.getRegistryName().toString(), MobData.create()
                .spawnRf(100)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.FEATHERS), .1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10f)));
        defaultMobData.put(EntityType.BLAZE.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.RODS_BLAZE), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.CAVE_SPIDER.getRegistryName().toString(), MobData.create()
                .spawnRf(500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.STRING), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.CHICKEN.getRegistryName().toString(), MobData.create()
                .spawnRf(500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.FEATHERS), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.PARROT.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.FEATHERS), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.COW.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.CREEPER.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GUNPOWDER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ENDER_DRAGON.getRegistryName().toString(), MobData.create()
                .spawnRf(100000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.EXPERIENCE_BOTTLE), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.END_STONE), 100))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 200)));
        defaultMobData.put(EntityType.ENDERMAN.getRegistryName().toString(), MobData.create()
                .spawnRf(2000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.END_STONE), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 40)));
        defaultMobData.put(EntityType.GHAST.getRegistryName().toString(), MobData.create()
                .spawnRf(2000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.GHAST_TEAR), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 50)));
        defaultMobData.put(EntityType.HORSE.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.SKELETON_HORSE.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.LLAMA.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.TRADER_LLAMA.getRegistryName().toString(), MobData.create()
                .spawnRf(1200)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.MULE.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.DONKEY.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.PANDA.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.BAMBOO), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.BEE.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.HONEY_BLOCK), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.ZOMBIE_HORSE.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.IRON_GOLEM.getRegistryName().toString(), MobData.create()
                .spawnRf(2000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.INGOTS_IRON), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 6.0f))
                .item3(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FLOWERS), 0.5f)));
        defaultMobData.put(EntityType.MAGMA_CUBE.getRegistryName().toString(), MobData.create()
                .spawnRf(600)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.MAGMA_CREAM), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.MOOSHROOM.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.OCELOT.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.CAT.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.FOX.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PIG.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ZOGLIN.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.NETHERRACK), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 70)));
        defaultMobData.put(EntityType.HOGLIN.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.NETHERRACK), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 70)));
        defaultMobData.put(EntityType.SHEEP.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.WOOL), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SKELETON.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SLIME.getRegistryName().toString(), MobData.create()
                .spawnRf(600)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.SLIMEBALLS), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.SNOW_GOLEM.getRegistryName().toString(), MobData.create()
                .spawnRf(600)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.SNOWBALL), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.SPIDER.getRegistryName().toString(), MobData.create()
                .spawnRf(500)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.STRING), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.SQUID.getRegistryName().toString(), MobData.create()
                .spawnRf(500)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.INK_SAC), 0.1f))
		        .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.VILLAGER.getRegistryName().toString(), MobData.create()
                .spawnRf(2000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.BOOK), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.ZOMBIE_VILLAGER.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.WANDERING_TRADER.getRegistryName().toString(), MobData.create()
                .spawnRf(20000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.BOOKSHELF), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 40)));
        defaultMobData.put(EntityType.WITCH.getRegistryName().toString(), MobData.create()
                .spawnRf(1200)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.GLASS_BOTTLE), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.WITHER.getRegistryName().toString(), MobData.create()
                .spawnRf(20000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.NETHER_STAR), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.SOUL_SAND), 0.5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 100)));
        defaultMobData.put(EntityType.WOLF.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ZOMBIFIED_PIGLIN.getRegistryName().toString(), MobData.create()
                .spawnRf(1200)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PIGLIN.getRegistryName().toString(), MobData.create()
                .spawnRf(1200)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.field_242287_aj.getRegistryName().toString(), MobData.create()
                .spawnRf(1400)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.STRIDER.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.NUGGETS_GOLD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PILLAGER.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.VINDICATOR.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.EVOKER.getRegistryName().toString(), MobData.create()
                .spawnRf(2000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ILLUSIONER.getRegistryName().toString(), MobData.create()
                .spawnRf(2000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.RAVAGER.getRegistryName().toString(), MobData.create()
                .spawnRf(4000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.SADDLE), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 60)));
        defaultMobData.put(EntityType.PHANTOM.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.PHANTOM_MEMBRANE), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ZOMBIE.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.DROWNED.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 90)));
        defaultMobData.put(EntityType.GIANT.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.HUSK.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.GUARDIAN.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.PRISMARINE_SHARD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.ELDER_GUARDIAN.getRegistryName().toString(), MobData.create()
                .spawnRf(5000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.PRISMARINE_SHARD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 60)));
        defaultMobData.put(EntityType.SHULKER.getRegistryName().toString(), MobData.create()
                .spawnRf(600)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.END_STONE), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ENDERMITE.getRegistryName().toString(), MobData.create()
                .spawnRf(400)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 0.05f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.END_STONE), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.SILVERFISH.getRegistryName().toString(), MobData.create()
                .spawnRf(400)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.INGOTS_IRON), 0.05f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.RABBIT.getRegistryName().toString(), MobData.create()
                .spawnRf(300)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.RABBIT_STEW), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.POLAR_BEAR.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.DOLPHIN.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SALMON.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.COD.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PUFFERFISH.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.TROPICAL_FISH.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.VEX.getRegistryName().toString(), MobData.create()
                .spawnRf(1000)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.IRON_SWORD), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.TURTLE.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.SEAGRASS), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.WITHER_SKELETON.getRegistryName().toString(), MobData.create()
                .spawnRf(1500)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.STRAY.getRegistryName().toString(), MobData.create()
                .spawnRf(800)
                .item1(MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        return defaultMobData;
    }

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the spawner system").push(CATEGORY_SPAWNER);
        CLIENT_BUILDER.comment("Settings for the spawner system").push(CATEGORY_SPAWNER);

        maxMobInjections = SERVER_BUILDER
                .comment("Maximum amount of injections we need to do a full mob extraction.")
                .defineInRange("maxMobInjections", 10, 1, Integer.MAX_VALUE);

        initMobConfigs(SERVER_BUILDER);

        CLIENT_BUILDER.pop();
        SERVER_BUILDER.pop();

        // @todo 1.15
//        cfg.addCustomCategoryComment(SpawnerConfiguration.CATEGORY_SPAWNER, "Settings for the spawner system");
//        cfg.addCustomCategoryComment(SpawnerConfiguration.CATEGORY_MOBSPAWNAMOUNTS, "Amount of materials needed to spawn mobs");
//        cfg.addCustomCategoryComment(SpawnerConfiguration.CATEGORY_MOBSPAWNRF, "Amount of RF needed to spawn mobs");
//        cfg.addCustomCategoryComment(SpawnerConfiguration.CATEGORY_LIVINGMATTER, "Blocks and items that are seen as living for the spawner");
//        SPAWNER_MAXENERGY = cfg.get(CATEGORY_SPAWNER, "spawnerMaxRF", SPAWNER_MAXENERGY,
//                "Maximum RF storage that the spawner can hold").getInt();
//        SPAWNER_RECEIVEPERTICK = cfg.get(CATEGORY_SPAWNER, "spawnerRFPerTick", SPAWNER_RECEIVEPERTICK,
//                "RF per tick that the spawner can receive").getInt();
//
//        BEAMER_MAXENERGY = cfg.get(CATEGORY_SPAWNER, "beamerMaxRF", BEAMER_MAXENERGY,
//                "Maximum RF storage that the matter beamer can hold").getInt();
//        BEAMER_RECEIVEPERTICK = cfg.get(CATEGORY_SPAWNER, "beamerRFPerTick", BEAMER_RECEIVEPERTICK,
//                "RF per tick that the matter beamer can receive").getInt();
//        beamRfPerObject = cfg.get(CATEGORY_SPAWNER, "beamerRfPerSend", beamRfPerObject,
//                "RF per tick that the matter beamer will use for sending over a single object").getInt();
//        beamBlocksPerSend = cfg.get(CATEGORY_SPAWNER, "beamerBlocksPerSend", beamBlocksPerSend,
//                "The amount of blocks that the matter beamer will use send in one operation (every 20 ticks)").getInt();
//
//        maxMatterStorage = cfg.get(CATEGORY_SPAWNER, "spawnerMaxMatterStorage", maxMatterStorage,
//                "The maximum amount of energized matter that this spawner can store (per type)").getInt();
//        maxBeamDistance = cfg.get(CATEGORY_SPAWNER, "maxBeamDistance", maxBeamDistance,
//                "The maximum distance that a laser can travel between the beamer and the spawner").getInt();

//        readLivingConfig(cfg);

        unknownMobDefault = MobData.create()
                .item1(MobSpawnAmount.create(Ingredient.fromItems(Items.BEDROCK), 1.0f))
                .item2(MobSpawnAmount.create(Ingredient.fromItems(Items.BEDROCK), 1.0f))
                .item3(MobSpawnAmount.create(Ingredient.fromItems(Items.BEDROCK), 1.0f))
                .spawnRf(50000);
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

        public static MobSpawnAmount create(String object) {
            Ingredient ingredient;
            int index = object.indexOf(':');
            if (index == -1) {
                throw new IllegalStateException("Bad formatted config for mob data!");
            }
            String amountString = object.substring(0, index);
            Float amount = Float.parseFloat(amountString);

            object = object.substring(index + 1);

            if ("<living>".equals(object)) {
                ingredient = Ingredient.EMPTY;
            } else {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(object.replaceAll("'", "\""));
                ingredient = Ingredient.deserialize(element);
            }
            return new MobSpawnAmount(ingredient, amount);
        }

        public Ingredient getObject() {
            return object;
        }

        public String serialize() {
            if (object.hasNoMatchingItems()) {
                return Float.toString(amount) + ":<living>";
            }

            return Float.toString(amount) + ":" + object.serialize().toString().replaceAll("\"", "'");
        }

        public float getAmount() {
            return amount;
        }

        public Float match(ItemStack stack) {
            if (object.hasNoMatchingItems()) {
                // Living?
                Item item = stack.getItem();
                Set<ResourceLocation> tags = item.getTags();
                if (tags.contains(HIGHYIELD)) {
                    return 1.5f;
                } else if (tags.contains(AVERAGEYIELD)) {
                    return 1.0f;
                } else if (tags.contains(LOWYIELD)) {
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

        public MobData resolve() {
            return this;
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

    private static final FolderName SERVERCONFIG = new FolderName("serverconfig");

    public static MobData getMobData(String id) {
        if (mobData.isEmpty()) {
            reloadConfig();
        }
        MobData data = mobData.get(id);
        if (data == null) {
            return null;
        }
        return data;
    }

    private static void reloadConfig() {
        mobData.clear();
        for (String s : spawnItems.get()) {
            String[] split = s.split("=");
            String key = split[0];
            MobSpawnAmount amount = MobSpawnAmount.create(split[1]);
            MobData data = mobData.computeIfAbsent(key, k -> new MobData());
            if (data.item1 == null) {
                data.item1 = amount;
            } else if (data.item2 == null) {
                data.item2 = amount;
            } else if (data.item3 == null) {
                data.item3 = amount;
            } else {
                throw new RuntimeException("Too many items for the mob '" + key + "'!");
            }
        }

        for (String s : spawnRfConfig.get()) {
            String[] split = s.split("=");
            String key = split[0];
            int power = Integer.parseInt(split[1]);
            MobData data = mobData.computeIfAbsent(key, k -> new MobData());
            data.spawnRf = power;
        }
    }

    public static void onLoad(final ModConfig.Loading configEvent) {
        mobData.clear();
    }

    public static void onReload(final ModConfig.Reloading configEvent) {
        mobData.clear();
    }
}
