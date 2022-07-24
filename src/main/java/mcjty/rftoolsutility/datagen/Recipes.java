package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.recipes.SyringeRecipeBuilder;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeBuilder;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
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
        add('S', SpawnerModule.SYRINGE.get());
        add('Z', Tags.Items.DYES_BLACK);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(CrafterModule.CRAFTER1.get())
                        .define('C', Blocks.CRAFTING_TABLE)
                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                " T ", "CFC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER2.get())
                        .define('C', Blocks.CRAFTING_TABLE)
                        .define('M', CrafterModule.CRAFTER1.get())
                        .unlockedBy("crafter1", has(CrafterModule.CRAFTER1.get())),
                " T ", "CMC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER3.get())
                        .define('C', Blocks.CRAFTING_TABLE)
                        .define('M', CrafterModule.CRAFTER2.get())
                        .unlockedBy("crafter2", has(CrafterModule.CRAFTER2.get())),
                " T ", "CMC", " T ");

        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.DESTINATION_ANALYZER.get())
                        .define('q', Items.QUARTZ)
                        .define('C', Items.COMPARATOR)
                        .define('f', Items.REPEATER)
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "ror", "CFf", "qrq");
        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.DIALING_DEVICE.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "rrr", "TFT", "rrr");
        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.MATTER_RECEIVER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "iii", "rFr", "ooo");
        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.MATTER_TRANSMITTER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "ooo", "rFr", "iii");
        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.MATTER_BOOSTER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                " R ", "RFR", " R ");
        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.SIMPLE_DIALER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "rRr", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shaped(TeleporterModule.CHARGED_PORTER.get())
                        .unlockedBy("pearl", has(Items.ENDER_PEARL)),
                " o ", "oRo", "ioi");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(TeleporterModule.ADVANCED_CHARGED_PORTER.get())
                        .define('M', TeleporterModule.CHARGED_PORTER.get())
                        .unlockedBy("porter", has(TeleporterModule.CHARGED_PORTER.get())),
                "RdR", "dMd", "RdR");
        build(consumer, ShapedRecipeBuilder.shaped(TankModule.TANK.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "GGG", "bFb", "iii");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.SCREEN.get())
                        .unlockedBy("base", has(VariousModule.MACHINE_BASE.get())),
                "GGG", "GAG", "iii");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.SCREEN_CONTROLLER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "ror", "GFG", "rGr");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.TEXT_MODULE.get())
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " p ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.BUTTON_MODULE.get())
                        .define('X', Items.STONE_BUTTON)
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.CLOCK_MODULE.get())
                        .define('X', Items.CLOCK)
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
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
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.ENERGY_MODULE.get())
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " r ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.ENERGYPLUS_MODULE.get())
                        .define('z', Tags.Items.INGOTS_GOLD)
                        .define('M', ScreenModule.ENERGY_MODULE.get())
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.FLUID_MODULE.get())
                        .define('X', Items.BUCKET)
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.FLUIDPLUS_MODULE.get())
                        .define('z', Tags.Items.INGOTS_GOLD)
                        .define('M', ScreenModule.FLUID_MODULE.get())
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.INVENTORY_MODULE.get())
                        .define('X', Tags.Items.CHESTS)
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.INVENTORYPLUS_MODULE.get())
                        .define('z', Tags.Items.INGOTS_GOLD)
                        .define('M', ScreenModule.INVENTORY_MODULE.get())
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.MACHINEINFORMATION_MODULE.get())
                        .define('X', Items.FURNACE)
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.REDSTONE_MODULE.get())
                        .define('X', Items.REPEATER)
                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                " X ", "rir", " Z ");

        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.ANALOG.get())
                        .define('C', Items.COMPARATOR)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "rAC");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.COUNTER.get())
                        .define('C', Items.CLOCK)
                        .define('g', Items.GOLD_NUGGET)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "gCg", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.DIGIT.get())
                        .define('P', Tags.Items.GLASS_PANES)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "PPP", "rAr", "PPP");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.INVCHECKER.get())
                        .define('P', Items.COMPARATOR)
                        .define('C', Tags.Items.CHESTS)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " C ");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.SENSOR.get())
                        .define('C', Items.COMPARATOR)
                        .define('x', Tags.Items.GEMS_QUARTZ)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "xCx", "rAr", "xCx");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.SEQUENCER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "rTr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.LOGIC.get())
                        .define('C', Items.COMPARATOR)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "rCr", "CAC", "rCr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.TIMER.get())
                        .define('C', Items.CLOCK)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "rCr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.WIRE.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "rAr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.REDSTONE_RECEIVER.get())
                        .define('C', Items.COMPARATOR)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "ror", "CAC", "rRr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.REDSTONE_TRANSMITTER.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                "ror", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shaped(LogicBlockModule.REDSTONE_INFORMATION.get())
                        .unlockedBy("redstone", has(Items.REDSTONE)),
                "ror", "rRr", "rrr");
        build(consumer, ShapedRecipeBuilder.shaped(ScreenModule.SCREEN_LINK.get())
                        .define('P', Tags.Items.GLASS_PANES)
                        .unlockedBy("redstone", has(Items.REDSTONE)),
                "ror", "PPP", "rrr");
        build(consumer, ShapedRecipeBuilder.shaped(SpawnerModule.SPAWNER.get())
                        .define('z', Items.ROTTEN_FLESH)
                        .define('P', Tags.Items.BONES)
                        .define('X', Tags.Items.RODS_BLAZE)
                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                "rzr", "oFX", "rPr");
        build(consumer, ShapedRecipeBuilder.shaped(SpawnerModule.MATTER_BEAMER.get())
                        .define('z', Blocks.GLOWSTONE)
                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                "RzR", "zFz", "RzR");
        build(consumer, ShapedRecipeBuilder.shaped(SpawnerModule.SYRINGE.get())
                        .define('z', Items.GLASS_BOTTLE)
                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                "i  ", " i ", "  z");

        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get())
                        .define('X', Blocks.DIAMOND_BLOCK)
                        .define('E', Blocks.EMERALD_BLOCK)
                        .define('I', Blocks.IRON_BLOCK)
                        .define('z', Blocks.GOLD_BLOCK)
                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                "oXo", "zFI", "oEo");

        buildEnvironmentalModules(consumer);
        buildSpawnerRecipes(consumer);
    }

    private void buildEnvironmentalModules(Consumer<FinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('X', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("dimshards", has(VariousModule.DIMENSIONALSHARD.get())),
                "sis", "iXi", "sis");
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('X', VariousModule.INFUSED_DIAMOND.get())
                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                        .unlockedBy("dimshards", has(VariousModule.DIMENSIONALSHARD.get())),
                "EXE", "XPX", "EXE");

        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.BLINDNESS_MODULE.get(), new ResourceLocation("minecraft:squid"), 1)
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "ZSZ", "ZPZ", "ZZZ");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.FEATHERFALLING_MODULE.get(), new ResourceLocation("minecraft:chicken"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.FEATHER)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.FEATHERFALLINGPLUS_MODULE.get())
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('X', EnvironmentalModule.FEATHERFALLING_MODULE.get())
                        .define('f', Items.FEATHER)
                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fXf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.HASTE_MODULE.get(), new ResourceLocation("minecraft:pillager"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.REDSTONE)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.HASTEPLUS_MODULE.get())
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('X', EnvironmentalModule.HASTE_MODULE.get())
                        .define('f', Items.REDSTONE)
                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fXf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.FLIGHT_MODULE.get(), new ResourceLocation("minecraft:ghast"), 1)
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('f', Items.GHAST_TEAR)
                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fSf", "fPf", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.GLOWING_MODULE.get(), new ResourceLocation("minecraft:creeper"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.GLOWSTONE)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.LUCK_MODULE.get(), new ResourceLocation("minecraft:cat"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.QUARTZ)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.NIGHTVISION_MODULE.get(), new ResourceLocation("minecraft:drowned"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.GLOWSTONE)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.NOTELEPORT_MODULE.get(), new ResourceLocation("minecraft:enderman"), 1)
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('f', Items.ENDER_PEARL)
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.PEACEFUL_MODULE.get(), new ResourceLocation("minecraft:iron_golem"), 1)
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('f', Blocks.IRON_BLOCK)
                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fSf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.POISON_MODULE.get(), new ResourceLocation("minecraft:cave_spider"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.POISONOUS_POTATO)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.REGENERATION_MODULE.get(), new ResourceLocation("minecraft:witch"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.GOLDEN_APPLE)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.REGENERATIONPLUS_MODULE.get())
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('X', EnvironmentalModule.REGENERATION_MODULE.get())
                        .define('f', Items.GOLDEN_APPLE)
                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fXf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.SATURATION_MODULE.get(), new ResourceLocation("minecraft:zombie"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.ROTTEN_FLESH)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.SATURATIONPLUS_MODULE.get())
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('X', EnvironmentalModule.SATURATION_MODULE.get())
                        .define('f', Items.ROTTEN_FLESH)
                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fXf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.SLOWNESS_MODULE.get(), new ResourceLocation("minecraft:turtle"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Items.STRING)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.SPEED_MODULE.get(), new ResourceLocation("minecraft:wolf"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Blocks.POWERED_RAIL)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
        build(consumer, ShapedRecipeBuilder.shaped(EnvironmentalModule.SPEEDPLUS_MODULE.get())
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('X', EnvironmentalModule.SPEED_MODULE.get())
                        .define('f', Blocks.POWERED_RAIL)
                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fXf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.WATERBREATHING_MODULE.get(), new ResourceLocation("minecraft:guardian"), 1)
                        .define('P', EnvironmentalModule.MODULEPLUS_TEMPLATE.get())
                        .define('f', Items.PRISMARINE_SHARD)
                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                        .unlockedBy("template", has(EnvironmentalModule.MODULEPLUS_TEMPLATE.get())),
                "fSf", "EPE", "fEf");
        build(consumer, SyringeRecipeBuilder.shaped(EnvironmentalModule.WEAKNESS_MODULE.get(), new ResourceLocation("minecraft:piglin"), 1)
                        .define('P', EnvironmentalModule.MODULE_TEMPLATE.get())
                        .define('f', Blocks.CACTUS)
                        .unlockedBy("template", has(EnvironmentalModule.MODULE_TEMPLATE.get())),
                "fSf", "fPf", "fff");
    }

    private void buildSpawnerRecipes(Consumer<FinishedRecipe> consumer) {
        Map<String, SpawnerRecipes.MobData> data = getDefaultMobData();
        for (Map.Entry<String, SpawnerRecipes.MobData> entry : data.entrySet()) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entry.getKey()));
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
