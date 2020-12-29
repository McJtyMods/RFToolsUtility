package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeBuilder;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousModule.MACHINE_FRAME.get());
        add('A', VariousModule.MACHINE_BASE.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
        add('Z', Tags.Items.DYES_BLACK);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER1.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                " T ", "CFC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER2.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterModule.CRAFTER1.get())
                .addCriterion("crafter1", hasItem(CrafterModule.CRAFTER1.get())),
                " T ", "CMC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER3.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterModule.CRAFTER2.get())
                .addCriterion("crafter2", hasItem(CrafterModule.CRAFTER2.get())),
                " T ", "CMC", " T ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.DIALING_DEVICE.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "rrr", "TFT", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.MATTER_RECEIVER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "iii", "rFr", "ooo");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.MATTER_TRANSMITTER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "ooo", "rFr", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.MATTER_BOOSTER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                " R ", "RFR", " R ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.SIMPLE_DIALER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rRr", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.CHARGED_PORTER.get())
                .addCriterion("pearl", hasItem(Items.ENDER_PEARL)),
                " o ", "oRo" , "ioi");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(TeleporterModule.ADVANCED_CHARGED_PORTER.get())
                .key('M', TeleporterModule.CHARGED_PORTER.get())
                .addCriterion("porter", hasItem(TeleporterModule.CHARGED_PORTER.get())),
                "RdR", "dMd", "RdR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TankModule.TANK.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "GGG", "bFb", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.SCREEN.get())
                        .addCriterion("base", hasItem(VariousModule.MACHINE_BASE.get())),
                "GGG", "GAG", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.SCREEN_CONTROLLER.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "ror", "GFG", "rGr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.TEXT_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " p ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.BUTTON_MODULE.get())
                        .key('X', Items.STONE_BUTTON)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.CLOCK_MODULE.get())
                        .key('X', Items.CLOCK)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COMPUTER_MODULE)
//                        .key('X', Items.QUARTZ)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COUNTERPLUS_MODULE)
//                        .key('z', Tags.Items.INGOTS_GOLD)
//                        .key('M', ScreenSetup.COUNTER_MODULE)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                " o ", "zMz", " o ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COUNTER_MODULE)
//                        .key('X', Items.COMPARATOR)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ELEVATOR_MODULE)
//                        .key('X', Items.STONE_BUTTON)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                "XXX", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.ENERGY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " r ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.ENERGYPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenModule.ENERGY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.FLUID_MODULE.get())
                        .key('X', Items.BUCKET)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.FLUIDPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenModule.FLUID_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.INVENTORY_MODULE.get())
                        .key('X', Tags.Items.CHESTS)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.INVENTORYPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenModule.INVENTORY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.MACHINEINFORMATION_MODULE.get())
                        .key('X', Items.FURNACE)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.REDSTONE_MODULE.get())
                        .key('X', Items.REPEATER)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.ANALOG.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rAC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.COUNTER.get())
                        .key('C', Items.CLOCK)
                        .key('g', Items.GOLD_NUGGET)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "gCg", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.DIGIT.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "PPP", "rAr", "PPP");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.INVCHECKER.get())
                        .key('P', Items.COMPARATOR)
                        .key('C', Tags.Items.CHESTS)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " C ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.SENSOR.get())
                        .key('C', Items.COMPARATOR)
                        .key('x', Tags.Items.GEMS_QUARTZ)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "xCx", "rAr", "xCx");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.SEQUENCER.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rTr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.LOGIC.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rCr", "CAC", "rCr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.TIMER.get())
                        .key('C', Items.CLOCK)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rCr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.WIRE.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rAr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.REDSTONE_RECEIVER.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "ror", "CAC", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.REDSTONE_TRANSMITTER.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "ror", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.REDSTONE_INFORMATION.get())
                        .addCriterion("redstone", hasItem(Items.REDSTONE)),
                "ror", "rRr", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.SCREEN_LINK.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("redstone", hasItem(Items.REDSTONE)),
                "ror", "PPP", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(SpawnerModule.SPAWNER.get())
                        .key('z', Items.ROTTEN_FLESH)
                        .key('P', Tags.Items.BONES)
                        .key('X', Tags.Items.RODS_BLAZE)
                        .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "rzr", "oFX", "rPr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(SpawnerModule.MATTER_BEAMER.get())
                        .key('z', Blocks.GLOWSTONE)
                        .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "RzR", "zFz", "RzR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(SpawnerModule.SYRINGE.get())
                        .key('z', Items.GLASS_BOTTLE)
                        .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "i  ", " i ", "  z");

        buildSpawnerRecipes(consumer);
    }

    private void buildSpawnerRecipes(Consumer<IFinishedRecipe> consumer) {
        Map<String, SpawnerRecipes.MobData> data = getDefaultMobData();
        for (Map.Entry<String, SpawnerRecipes.MobData> entry : data.entrySet()) {
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entry.getKey()));
            SpawnerRecipes.MobData value = entry.getValue();
            SpawnerRecipeBuilder builder = SpawnerRecipeBuilder.create(type);
            builder.power(value.getSpawnRf());
            builder.item1(value.getItem1().getObject(), value.getItem1().getAmount());
            builder.item2(value.getItem2().getObject(), value.getItem2().getAmount());
            builder.item3(value.getItem3().getObject(), value.getItem3().getAmount());
            builder.build(consumer);
        }
    }

    private static Map<String, SpawnerRecipes.MobData> getDefaultMobData() {
        Map<String, SpawnerRecipes.MobData> defaultMobData = new HashMap<>();
        defaultMobData.put(EntityType.BAT.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(100)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.FEATHERS), .1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10f)));
        defaultMobData.put(EntityType.BLAZE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.RODS_BLAZE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.CAVE_SPIDER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.STRING), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.CHICKEN.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.FEATHERS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.PARROT.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.FEATHERS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.COW.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.CREEPER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GUNPOWDER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ENDER_DRAGON.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(100000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.EXPERIENCE_BOTTLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.END_STONE), 100))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 200)));
        defaultMobData.put(EntityType.ENDERMAN.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.END_STONE), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 40)));
        defaultMobData.put(EntityType.GHAST.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.GHAST_TEAR), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 50)));
        defaultMobData.put(EntityType.HORSE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.SKELETON_HORSE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.LLAMA.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.TRADER_LLAMA.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.MULE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.DONKEY.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.PANDA.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.BAMBOO), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.BEE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.HONEY_BLOCK), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.ZOMBIE_HORSE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.IRON_GOLEM.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.INGOTS_IRON), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 6.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FLOWERS), 0.5f)));
        defaultMobData.put(EntityType.MAGMA_CUBE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.MAGMA_CREAM), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.MOOSHROOM.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.OCELOT.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.CAT.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.FOX.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PIG.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.LEATHER), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SHEEP.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.WOOL), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SKELETON.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SLIME.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.SLIMEBALLS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.SNOW_GOLEM.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.SNOWBALL), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.SPIDER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.STRING), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 15)));
        defaultMobData.put(EntityType.SQUID.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.INK_SAC), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.VILLAGER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.BOOK), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.ZOMBIE_VILLAGER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.WANDERING_TRADER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(20000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.BOOKSHELF), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 5.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 40)));
        defaultMobData.put(EntityType.WITCH.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1200)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.GLASS_BOTTLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), 1.0f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.WITHER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(20000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.NETHER_STAR), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.SOUL_SAND), 0.5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 100)));
        defaultMobData.put(EntityType.WOLF.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PILLAGER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.VINDICATOR.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.EVOKER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ILLUSIONER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(2000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.RAVAGER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(4000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.SADDLE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 60)));
        defaultMobData.put(EntityType.PHANTOM.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.PHANTOM_MEMBRANE), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ZOMBIE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.DROWNED.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 90)));
        defaultMobData.put(EntityType.GIANT.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.HUSK.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.ROTTEN_FLESH), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.GUARDIAN.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.PRISMARINE_SHARD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.ELDER_GUARDIAN.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(5000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.PRISMARINE_SHARD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 60)));
        defaultMobData.put(EntityType.SHULKER.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(600)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.END_STONE), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.ENDERMITE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), 0.05f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.END_STONE), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.SILVERFISH.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(400)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.INGOTS_IRON), 0.05f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.RABBIT.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(300)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.RABBIT_STEW), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 10)));
        defaultMobData.put(EntityType.POLAR_BEAR.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.DOLPHIN.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.SALMON.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.COD.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.PUFFERFISH.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.TROPICAL_FISH.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(ItemTags.FISHES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.VEX.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1000)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.IRON_SWORD), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.TURTLE.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.SEAGRASS), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Items.DIRT, Items.GRAVEL, Items.SAND), .2f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        defaultMobData.put(EntityType.WITHER_SKELETON.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(1500)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 30)));
        defaultMobData.put(EntityType.STRAY.getRegistryName().toString(), SpawnerRecipes.MobData.create()
                .spawnRf(800)
                .item1(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromTag(Tags.Items.BONES), 0.1f))
                .item2(SpawnerRecipes.MobSpawnAmount.create(Ingredient.fromItems(Blocks.NETHERRACK), .5f))
                .item3(SpawnerRecipes.MobSpawnAmount.create(Ingredient.EMPTY, 20)));
        return defaultMobData;
    }

}
