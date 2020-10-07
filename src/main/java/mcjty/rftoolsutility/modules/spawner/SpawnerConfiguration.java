package mcjty.rftoolsutility.modules.spawner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.*;

public class SpawnerConfiguration {
    public static final String CATEGORY_SPAWNER = "spawner";
    public static final String CATEGORY_MOBDATA = "mobdata";

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
    private static final Map<String, MobData> defaultMobData = new HashMap<>();
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

    public static void initMobConfigs(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for the spawner system").push(CATEGORY_MOBDATA);

        // Fill in defaultMobData
        setupDefaultMobData();

        Map<String, List<ResourceLocation>> byMod = new HashMap<>();
        for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> entry : ForgeRegistries.ENTITIES.getEntries()) {
            EntityType<?> type = entry.getValue();
            if (type.getClassification() != EntityClassification.MISC) {
                RegistryKey<EntityType<?>> key = entry.getKey();
                ResourceLocation id = key.getRegistryName();
                byMod.computeIfAbsent(id.getNamespace(), s -> new ArrayList<>());
                byMod.get(id.getNamespace()).add(id);
            }
        }

        for (Map.Entry<String, List<ResourceLocation>> entry : byMod.entrySet()) {
            SERVER_BUILDER.push(entry.getKey());
            for (ResourceLocation id : entry.getValue()) {
                SERVER_BUILDER.push(id.getPath());

                MobData data = defaultMobData.getOrDefault(id.toString(), unknownMobDefault);

                ForgeConfigSpec.ConfigValue<String> item1 = SERVER_BUILDER.define("item1", data.getItem1().serialize());
                ForgeConfigSpec.ConfigValue<String> item2 = SERVER_BUILDER.define("item2", data.getItem2().serialize());
                ForgeConfigSpec.ConfigValue<String> item3 = SERVER_BUILDER.define("item3", data.getItem3().serialize());
                ForgeConfigSpec.IntValue spawnRf = SERVER_BUILDER.defineInRange("spawnRf", data.getSpawnRf(), 0, Integer.MAX_VALUE);

                data = new UnresolvedMobData(item1, item2, item3, spawnRf);
                mobData.put(id.toString(), data);

                SERVER_BUILDER.pop();
            }
            SERVER_BUILDER.pop();
        }


        SERVER_BUILDER.pop();
    }

    private static void setupDefaultMobData() {
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
    }

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the spawner system").push(CATEGORY_SPAWNER);
        CLIENT_BUILDER.comment("Settings for the spawner system").push(CATEGORY_SPAWNER);

        maxMobInjections = SERVER_BUILDER
                .comment("Maximum amount of injections we need to do a full mob extraction.")
                .defineInRange("maxMobInjections", 10, 1, Integer.MAX_VALUE);

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

        // @todo 1.15
//        if (cfg.getCategory(CATEGORY_MOBSPAWNAMOUNTS).isEmpty()) {
//            setupInitialMobSpawnConfig(cfg);
//        }
    }

//    private static void readLivingConfig(Configuration cfg) {
//        ConfigCategory category = cfg.getCategory(CATEGORY_LIVINGMATTER);
//        if (category.isEmpty()) {
//            setupInitialLivingConfig(cfg);
//        }
//        for (Map.Entry<String, Property> entry : category.entrySet()) {
//            String[] value = entry.getValue().getStringList();
//            try {
//                // value[0] is type and is no longer used
//                String name = value[1];
//                Float factor = Float.parseFloat(value[2]);
//                livingMatter.put(new ResourceLocation(name), factor);
//            } catch (Exception e) {
//                Logging.logError("Badly formatted 'livingmatter' configuration option!");
//                return;
//            }
//        }
//    }

//    private static int addLiving(Configuration cfg, Item item, int counter, float factor) {
//        cfg.get(CATEGORY_LIVINGMATTER, "living." + counter, new String[] { "I", item.getRegistryName().toString(), Float.toString(factor) });
//        return counter+1;
//    }
//
//    public static void readMobSpawnAmountConfig(Configuration cfg) {
//        ConfigCategory category = cfg.getCategory(CATEGORY_MOBSPAWNAMOUNTS);
//        for (Map.Entry<String, Property> entry : category.entrySet()) {
//            String key = entry.getKey();
//
//            String[] splitted = entry.getValue().getStringList();
//
//            int materialType;
//            if (key.endsWith(".spawnamount.0")) {
//                materialType = MATERIALTYPE_KEY;
//            } else if (key.endsWith(".spawnamount.1")) {
//                materialType = MATERIALTYPE_BULK;
//            } else {
//                materialType = MATERIALTYPE_LIVING;
//            }
//            String id = key.substring(0, key.indexOf(".spawnamount"));
//            setSpawnAmounts(id, materialType, splitted);
//        }
//
//        category = cfg.getCategory(CATEGORY_MOBSPAWNRF);
//        for (Map.Entry<String, Property> entry : category.entrySet()) {
//            String key = entry.getKey();
//            int rf = entry.getValue().getInt();
//            mobSpawnRf.put(key, rf);
//        }
//    }
//
//    private static void addMobSpawnAmount(Configuration cfg, String id, int materialType, Object object, int meta, float amount) {
//        String type;
//        ResourceLocation itemname;
//        if (object instanceof Item) {
//            type = "I";
//            itemname = Item.REGISTRY.getNameForObject((Item) object);
//        } else if (object instanceof Block) {
//            type = "B";
//            itemname = Block.REGISTRY.getNameForObject((Block) object);
//        } else {
//            type = "L";
//            itemname = null;
//        }
//        cfg.get(CATEGORY_MOBSPAWNAMOUNTS, id + ".spawnamount." + materialType,
//                new String[] { type, itemname == null ? "" : itemname.toString(), Integer.toString(meta), Float.toString(amount) });
//    }
//
//    private static void setSpawnAmounts(String id, int materialType, String[] splitted) {
//        String type;
//        ResourceLocation itemname;
//        int meta;
//        float amount;
//        try {
//            type = splitted[0];
//            String n = splitted[1];
//            if ("".equals(n)) {
//                itemname = null;
//            } else {
//                itemname = new ResourceLocation(n);
//            }
//            meta = Integer.parseInt(splitted[2]);
//            amount = Float.parseFloat(splitted[3]);
//        } catch (NumberFormatException e) {
//            Logging.logError("Something went wrong parsing the spawnamount setting for '" + id + "'!");
//            return;
//        }
//
//        ItemStack stack = ItemStack.EMPTY;
//        if ("I".equals(type)) {
//            Item item = Item.REGISTRY.getObject(itemname);
//            stack = new ItemStack(item, 1, meta);
//        } else if ("B".equals(type)) {
//            Block block = Block.REGISTRY.getObject(itemname);
//            stack = new ItemStack(block, 1, meta);
//        } else if ("S".equals(type)) {
//        }
//        List<MobSpawnAmount> list = mobSpawnAmounts.get(id);
//        if (list == null) {
//            list = new ArrayList<>(3);
//            list.add(null);
//            list.add(null);
//            list.add(null);
//            mobSpawnAmounts.put(id, list);
//        }
//
//        list.set(materialType, new MobSpawnAmount(stack, amount));
//    }
//


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

        public boolean isResolved() {
            return true;
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

    public static class UnresolvedMobData extends MobData {

        private final ForgeConfigSpec.ConfigValue<String> item1;
        private final ForgeConfigSpec.ConfigValue<String> item2;
        private final ForgeConfigSpec.ConfigValue<String> item3;
        private final ForgeConfigSpec.IntValue spawnRf;

        public UnresolvedMobData(ForgeConfigSpec.ConfigValue<String> item1, ForgeConfigSpec.ConfigValue<String> item2, ForgeConfigSpec.ConfigValue<String> item3, ForgeConfigSpec.IntValue spawnRf) {
            this.item1 = item1;
            this.item2 = item2;
            this.item3 = item3;
            this.spawnRf = spawnRf;
        }

        @Override
        public boolean isResolved() {
            return false;
        }

        @Override
        public MobData resolve() {
            return MobData.create()
                    .item1(MobSpawnAmount.create(item1.get()))
                    .item2(MobSpawnAmount.create(item2.get()))
                    .item3(MobSpawnAmount.create(item3.get()))
                    .spawnRf(spawnRf.get());
        }
    }

    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            initMobDataConfig();
        }
    }

    private static final FolderName SERVERCONFIG = new FolderName("serverconfig");

    public static void initMobDataConfig() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        initMobConfigs(SERVER_BUILDER);

        ForgeConfigSpec configSpec = SERVER_BUILDER.build();
        final Path serverConfig = ServerLifecycleHooks.getCurrentServer().func_240776_a_(SERVERCONFIG);
        FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
        Config.loadConfig(configSpec, serverConfig.resolve("rftoolsutility-mobdata.toml"));
    }

    public static MobData getMobData(String id) {
        MobData mobData = SpawnerConfiguration.mobData.get(id);
        if (mobData == null) {
            return null;
        }
        if (!mobData.isResolved()) {
            mobData = mobData.resolve();
            SpawnerConfiguration.mobData.put(id, mobData);
        }
        return mobData;
    }
}
