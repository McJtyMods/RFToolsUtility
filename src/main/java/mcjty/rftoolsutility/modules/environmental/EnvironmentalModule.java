package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.varia.ClientTools;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.client.ClientSetup;
import mcjty.rftoolsutility.modules.environmental.client.EnvironmentalRenderer;
import mcjty.rftoolsutility.modules.environmental.client.GuiEnvironmentalController;
import mcjty.rftoolsutility.modules.environmental.items.EnvironmentalControllerItem;
import mcjty.rftoolsutility.modules.environmental.recipes.SyringeRecipeBuilder;
import mcjty.rftoolsutility.modules.environmental.recipes.SyringeRecipeSerializer;
import mcjty.rftoolsutility.modules.environmental.recipes.SyringeRecipeType;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class EnvironmentalModule implements IModule {

    public static final DeferredBlock<BaseBlock> ENVIRONENTAL_CONTROLLER = BLOCKS.register("environmental_controller", EnvironmentalControllerTileEntity::createBlock);
    public static final DeferredItem<Item> ENVIRONENTAL_CONTROLLER_ITEM = ITEMS.register("environmental_controller", tab(() -> new BlockItem(ENVIRONENTAL_CONTROLLER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<EnvironmentalControllerTileEntity>> TYPE_ENVIRONENTAL_CONTROLLER = TILES.register("environmental_controller", () -> BlockEntityType.Builder.of(EnvironmentalControllerTileEntity::new, ENVIRONENTAL_CONTROLLER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENVIRONENTAL_CONTROLLER = CONTAINERS.register("environmental_controller", GenericContainer::createContainerType);

    public static final DeferredItem<Item> MODULE_TEMPLATE = ITEMS.register("module_template", tab(() -> new Item(createStandardProperties())));
    public static final DeferredItem<Item> MODULEPLUS_TEMPLATE = ITEMS.register("moduleplus_template", tab(() -> new Item(createStandardProperties())));

    public static final DeferredItem<EnvironmentalControllerItem> BLINDNESS_MODULE = ITEMS.register("blindness_module", tab(EnvironmentalControllerItem::createBlindnessModule));
    public static final DeferredItem<EnvironmentalControllerItem> FEATHERFALLING_MODULE = ITEMS.register("featherfalling_module", tab(EnvironmentalControllerItem::createFeatherfallingModule));
    public static final DeferredItem<EnvironmentalControllerItem> FEATHERFALLINGPLUS_MODULE = ITEMS.register("featherfallingplus_module", tab(EnvironmentalControllerItem::createFeatherfallingPlusModule));
    public static final DeferredItem<EnvironmentalControllerItem> HASTE_MODULE = ITEMS.register("haste_module", tab(EnvironmentalControllerItem::createHasteModule));
    public static final DeferredItem<EnvironmentalControllerItem> HASTEPLUS_MODULE = ITEMS.register("hasteplus_module", tab(EnvironmentalControllerItem::createHastePlusModule));
    public static final DeferredItem<EnvironmentalControllerItem> FLIGHT_MODULE = ITEMS.register("flight_module", tab(EnvironmentalControllerItem::createFlightModule));
    public static final DeferredItem<EnvironmentalControllerItem> GLOWING_MODULE = ITEMS.register("glowing_module", tab(EnvironmentalControllerItem::createGlowingModule));
    public static final DeferredItem<EnvironmentalControllerItem> LUCK_MODULE = ITEMS.register("luck_module", tab(EnvironmentalControllerItem::createLuckModule));
    public static final DeferredItem<EnvironmentalControllerItem> NIGHTVISION_MODULE = ITEMS.register("nightvision_module", tab(EnvironmentalControllerItem::createNightvisionModule));
    public static final DeferredItem<EnvironmentalControllerItem> NOTELEPORT_MODULE = ITEMS.register("noteleport_module", tab(EnvironmentalControllerItem::createNoteleportModule));
    public static final DeferredItem<EnvironmentalControllerItem> PEACEFUL_MODULE = ITEMS.register("peaceful_module", tab(EnvironmentalControllerItem::createPeacefulModule));
    public static final DeferredItem<EnvironmentalControllerItem> POISON_MODULE = ITEMS.register("poison_module", tab(EnvironmentalControllerItem::createPoisonModule));
    public static final DeferredItem<EnvironmentalControllerItem> REGENERATION_MODULE = ITEMS.register("regeneration_module", tab(EnvironmentalControllerItem::createRegenerationModule));
    public static final DeferredItem<EnvironmentalControllerItem> REGENERATIONPLUS_MODULE = ITEMS.register("regenerationplus_module", tab(EnvironmentalControllerItem::createRegenerationPlusModule));
    public static final DeferredItem<EnvironmentalControllerItem> SATURATION_MODULE = ITEMS.register("saturation_module", tab(EnvironmentalControllerItem::createSaturationModule));
    public static final DeferredItem<EnvironmentalControllerItem> SATURATIONPLUS_MODULE = ITEMS.register("saturationplus_module", tab(EnvironmentalControllerItem::createSaturationPlusModule));
    public static final DeferredItem<EnvironmentalControllerItem> SLOWNESS_MODULE = ITEMS.register("slowness_module", tab(EnvironmentalControllerItem::createSlownessModule));
    public static final DeferredItem<EnvironmentalControllerItem> SPEED_MODULE = ITEMS.register("speed_module", tab(EnvironmentalControllerItem::createSpeedModule));
    public static final DeferredItem<EnvironmentalControllerItem> SPEEDPLUS_MODULE = ITEMS.register("speedplus_module", tab(EnvironmentalControllerItem::createSpeedPlusModule));
    public static final DeferredItem<EnvironmentalControllerItem> WATERBREATHING_MODULE = ITEMS.register("waterbreathing_module", tab(EnvironmentalControllerItem::createWaterbreathingModule));
    public static final DeferredItem<EnvironmentalControllerItem> WEAKNESS_MODULE = ITEMS.register("weakness_module", tab(EnvironmentalControllerItem::createWeaknessModule));

    public static final Supplier<SyringeRecipeSerializer> SYRINGE_SERIALIZER = RECIPE_SERIALIZERS.register("syringe", SyringeRecipeSerializer::new);

    public static final ResourceLocation SYRINGE_RECIPE_TYPE_ID = new ResourceLocation(RFToolsUtility.MODID, "syringe");
    public static final Supplier<SyringeRecipeType> SYRINGE_RECIPE_TYPE = RECIPE_TYPES.register("syringe", SyringeRecipeType::new);

//    public static void initCrafting() {
//
//        Object inkSac = Item.REGISTRY.getObjectById(351);
//
//        String[] syringeMatcher = new String[]{"level", "mobId"};
//        String[] pickMatcher = new String[]{"ench"};
//
//        ItemStack ironGolemSyringe = SyringeItem.createMobSyringe(EntityIronGolem.class);
//        ItemStack endermanSyringe = SyringeItem.createMobSyringe(EntityEnderman.class);
//        ItemStack ghastSyringe = SyringeItem.createMobSyringe(EntityGhast.class);
//        ItemStack chickenSyringe = SyringeItem.createMobSyringe(EntityChicken.class);
//        ItemStack batSyringe = SyringeItem.createMobSyringe(EntityBat.class);
//        ItemStack horseSyringe = SyringeItem.createMobSyringe(EntityHorse.class);
//        ItemStack zombieSyringe = SyringeItem.createMobSyringe(EntityZombie.class);
//        ItemStack squidSyringe = SyringeItem.createMobSyringe(EntitySquid.class);
//        ItemStack guardianSyringe = SyringeItem.createMobSyringe(EntityGuardian.class);
//        ItemStack caveSpiderSyringe = SyringeItem.createMobSyringe(EntityCaveSpider.class);
//        ItemStack blazeSyringe = SyringeItem.createMobSyringe(EntityBlaze.class);
//        ItemStack shulkerEntity = SyringeItem.createMobSyringe(EntityShulker.class);
//        ItemStack diamondPick = createEnchantedItem(Items.DIAMOND_PICKAXE, Enchantment.REGISTRY.getObject(new ResourceLocation("efficiency")), 3);
//        ItemStack reds = new ItemStack(Items.REDSTONE);
//        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
//        ItemStack ink = new ItemStack((Item) inkSac);
//        ItemStack obsidian = new ItemStack(Blocks.OBSIDIAN);
//        ItemStack lapis = new ItemStack(Items.DYE, 1, 4);
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, chickenSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(featherFallingEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "featherfalling_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, ironGolemSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(regenerationEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "regeneration_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, horseSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(speedEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "speed_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3, new ItemStack[]{ItemStack.EMPTY, diamondPick, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, pickMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(hasteEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "haste_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, zombieSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(saturationEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "saturation_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, ghastSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(flightEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "flight_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, guardianSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(waterBreathingEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "waterbreathing_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, caveSpiderSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(nightVisionEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "nightvision_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(2, 2,
//                        new ItemStack[]{new ItemStack(regenerationEModuleItem), ironGolemSyringe, ironGolemSyringe, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, syringeMatcher, null},
//                        new ItemStack(regenerationPlusEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "regenerationplus_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(2, 2,
//                        new ItemStack[]{new ItemStack(speedEModuleItem), horseSyringe, horseSyringe, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, syringeMatcher, null},
//                        new ItemStack(speedPlusEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "speedplus_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(2, 2,
//                        new ItemStack[]{new ItemStack(hasteEModuleItem), diamondPick, ItemStack.EMPTY, ItemStack.EMPTY},
//                        new String[][]{null, pickMatcher, null, null},
//                        new ItemStack(hastePlusEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "hasteplus_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(2, 2,
//                        new ItemStack[]{new ItemStack(saturationEModuleItem), zombieSyringe, zombieSyringe, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, syringeMatcher, null},
//                        new ItemStack(saturationPlusEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "saturationplus_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(2, 2,
//                        new ItemStack[]{new ItemStack(featherFallingEModuleItem), chickenSyringe, batSyringe, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, syringeMatcher, null},
//                        new ItemStack(featherFallingPlusEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "featherfallingplus_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, blazeSyringe, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(glowingEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "glowing_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, shulkerEntity, ItemStack.EMPTY, reds, gold, reds, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(luckEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "luck_module")));
//
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, squidSyringe, ItemStack.EMPTY, lapis, obsidian, lapis, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(blindnessEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "blindness_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, batSyringe, ItemStack.EMPTY, lapis, obsidian, lapis, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(weaknessEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "weakness_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, caveSpiderSyringe, ItemStack.EMPTY, lapis, obsidian, lapis, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(poisonEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "poison_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, new ItemStack(Items.CLOCK), ItemStack.EMPTY, lapis, obsidian, lapis, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, null, null, null, null, null, null, null, null},
//                        new ItemStack(slownessEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "slowness_module")));
//
//        ForgeRegistries.RECIPES.register(
//                new NBTMatchingRecipe(3, 3,
//                        new ItemStack[]{ItemStack.EMPTY, endermanSyringe, ItemStack.EMPTY, lapis, obsidian, lapis, ItemStack.EMPTY, ink, ItemStack.EMPTY},
//                        new String[][]{null, syringeMatcher, null, null, null, null, null, null, null},
//                        new ItemStack(noTeleportEModuleItem))
//                        .setRegistryName(new ResourceLocation(RFTools.MODID, "noteleport_module")));
//    }

//    public static ItemStack createEnchantedItem(Item item, Enchantment effectId, int amount) {
//        ItemStack stack = new ItemStack(item);
//        Map<Enchantment, Integer> enchant = new HashMap<>();
//        enchant.put(effectId, amount);
//        EnchantmentHelper.setEnchantments(enchant, stack);
//        return stack;
//    }


    public EnvironmentalModule(IEventBus bus, Dist dist) {
        if (dist.isClient()) {
            ClientTools.onTextureStitch(bus, ClientSetup::onTextureStitch);
        }
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiEnvironmentalController.register();
        });
        ClientSetup.initClient();
        EnvironmentalRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
        EnvironmentalConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(ENVIRONENTAL_CONTROLLER)
                        .ironPickaxeTags()
                        .parentedItem("block/environmental_controller")
                        .standardLoot(TYPE_ENVIRONENTAL_CONTROLLER)
                        .blockState(DataGenHelper::createEnvController)
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('X', Blocks.DIAMOND_BLOCK)
                                        .define('E', Blocks.EMERALD_BLOCK)
                                        .define('I', Blocks.IRON_BLOCK)
                                        .define('z', Blocks.GOLD_BLOCK)
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                "oXo", "zFI", "oEo"),
                Dob.itemBuilder(MODULE_TEMPLATE)
                        .generatedItem("item/envmodules/moduletemplate")
                        .shaped(builder -> builder
                                        .define('X', VariousModule.INFUSED_DIAMOND.get())
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("dimshards", has(VariousModule.DIMENSIONALSHARD.get())),
                                "sis", "iXi", "sis"),
                Dob.itemBuilder(MODULEPLUS_TEMPLATE)
                        .generatedItem("item/envmodules/moduletemplateplus")
                        .shaped(builder -> builder
                                        .define('P', MODULE_TEMPLATE.get())
                                        .define('X', VariousModule.INFUSED_DIAMOND.get())
                                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("dimshards", has(VariousModule.DIMENSIONALSHARD.get())),
                                "EXE", "XPX", "EXE"),
                Dob.itemBuilder(BLINDNESS_MODULE)
                        .generatedItem("item/envmodules/blindnessmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(BLINDNESS_MODULE.get(), new ResourceLocation("minecraft:squid"), 1)
                                .define('Z', Tags.Items.DYES_BLACK)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULEPLUS_TEMPLATE.get())
                                .patternLine("ZSZ")
                                .patternLine("ZPZ")
                                .patternLine("ZZZ")
                                .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get()))),
                Dob.itemBuilder(FEATHERFALLING_MODULE)
                        .generatedItem("item/envmodules/featherfallingmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(FEATHERFALLING_MODULE.get(), new ResourceLocation("minecraft:chicken"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.FEATHER)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(FEATHERFALLINGPLUS_MODULE)
                        .generatedItem("item/envmodules/featherfallingplusmoduleitem")
                        .shaped(builder -> builder
                                        .define('P', MODULEPLUS_TEMPLATE.get())
                                        .define('X', FEATHERFALLING_MODULE.get())
                                        .define('f', Items.FEATHER)
                                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get())),
                                "fXf", "EPE", "fEf"),
                Dob.itemBuilder(HASTE_MODULE)
                        .generatedItem("item/envmodules/hastemoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(HASTE_MODULE.get(), new ResourceLocation("minecraft:pillager"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.REDSTONE)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(HASTEPLUS_MODULE)
                        .generatedItem("item/envmodules/hasteplusmoduleitem")
                        .shaped(builder -> builder
                                        .define('P', MODULEPLUS_TEMPLATE.get())
                                        .define('X', HASTE_MODULE.get())
                                        .define('f', Items.REDSTONE)
                                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get())),
                                "fXf", "EPE", "fEf"),
                Dob.itemBuilder(FLIGHT_MODULE)
                        .generatedItem("item/envmodules/flightmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(FLIGHT_MODULE.get(), new ResourceLocation("minecraft:ghast"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULEPLUS_TEMPLATE.get())
                                .define('f', Items.GHAST_TEAR)
                                .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fEf")
                                .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get()))),
                Dob.itemBuilder(GLOWING_MODULE)
                        .generatedItem("item/envmodules/glowingmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(GLOWING_MODULE.get(), new ResourceLocation("minecraft:creeper"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.GLOWSTONE)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(LUCK_MODULE)
                        .generatedItem("item/envmodules/luckmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(LUCK_MODULE.get(), new ResourceLocation("minecraft:cat"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.QUARTZ)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(NIGHTVISION_MODULE)
                        .generatedItem("item/envmodules/nightvisionmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(NIGHTVISION_MODULE.get(), new ResourceLocation("minecraft:drowned"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.GLOWSTONE)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(NOTELEPORT_MODULE)
                        .generatedItem("item/envmodules/noteleportmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(NOTELEPORT_MODULE.get(), new ResourceLocation("minecraft:enderman"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULEPLUS_TEMPLATE.get())
                                .define('f', Items.ENDER_PEARL)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get()))),
                Dob.itemBuilder(PEACEFUL_MODULE)
                        .generatedItem("item/envmodules/peacefulmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(PEACEFUL_MODULE.get(), new ResourceLocation("minecraft:iron_golem"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULEPLUS_TEMPLATE.get())
                                .define('f', Blocks.IRON_BLOCK)
                                .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                                .patternLine("fSf")
                                .patternLine("EPE")
                                .patternLine("fEf")
                                .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get()))),
                Dob.itemBuilder(POISON_MODULE)
                        .generatedItem("item/envmodules/poisonmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(POISON_MODULE.get(), new ResourceLocation("minecraft:cave_spider"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.POISONOUS_POTATO)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(REGENERATION_MODULE)
                        .generatedItem("item/envmodules/regenerationmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(REGENERATION_MODULE.get(), new ResourceLocation("minecraft:witch"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.GOLDEN_APPLE)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(REGENERATIONPLUS_MODULE)
                        .generatedItem("item/envmodules/regenerationplusmoduleitem")
                        .shaped(builder -> builder
                                        .define('P', MODULEPLUS_TEMPLATE.get())
                                        .define('X', REGENERATION_MODULE.get())
                                        .define('f', Items.GOLDEN_APPLE)
                                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get())),
                                "fXf", "EPE", "fEf"),
                Dob.itemBuilder(SATURATION_MODULE)
                        .generatedItem("item/envmodules/saturationmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(SATURATION_MODULE.get(), new ResourceLocation("minecraft:zombie"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.ROTTEN_FLESH)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(SATURATIONPLUS_MODULE)
                        .generatedItem("item/envmodules/saturationplusmoduleitem")
                        .shaped(builder -> builder
                                        .define('P', MODULEPLUS_TEMPLATE.get())
                                        .define('X', SATURATION_MODULE.get())
                                        .define('f', Items.ROTTEN_FLESH)
                                        .define('E', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get())),
                                "fXf", "EPE", "fEf"),
                Dob.itemBuilder(SLOWNESS_MODULE)
                        .generatedItem("item/envmodules/slownessmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(SLOWNESS_MODULE.get(), new ResourceLocation("minecraft:turtle"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Items.STRING)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(SPEED_MODULE)
                        .generatedItem("item/envmodules/speedmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(SPEED_MODULE.get(), new ResourceLocation("minecraft:wolf"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Blocks.POWERED_RAIL)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get()))),
                Dob.itemBuilder(SPEEDPLUS_MODULE)
                        .generatedItem("item/envmodules/speedplusmoduleitem")
                        .shaped(builder -> builder
                                        .define('P', MODULEPLUS_TEMPLATE.get())
                                        .define('X', SPEED_MODULE.get())
                                        .define('f', Blocks.POWERED_RAIL)
                                        .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get())),
                                "fXf", "EPE", "fEf"),
                Dob.itemBuilder(WATERBREATHING_MODULE)
                        .generatedItem("item/envmodules/waterbreathingmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(WATERBREATHING_MODULE.get(), new ResourceLocation("minecraft:guardian"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULEPLUS_TEMPLATE.get())
                                .define('f', Items.PRISMARINE_SHARD)
                                .define('E', VariousModule.INFUSED_ENDERPEARL.get())
                                .patternLine("fSf")
                                .patternLine("EPE")
                                .patternLine("fEf")
                                .unlockedBy("template", has(MODULEPLUS_TEMPLATE.get()))),
                Dob.itemBuilder(WEAKNESS_MODULE)
                        .generatedItem("item/envmodules/weaknessmoduleitem")
                        .recipe(() -> SyringeRecipeBuilder.shaped(WEAKNESS_MODULE.get(), new ResourceLocation("minecraft:piglin"), 1)
                                .define('S', SpawnerModule.SYRINGE.get())
                                .define('P', MODULE_TEMPLATE.get())
                                .define('f', Blocks.CACTUS)
                                .patternLine("fSf")
                                .patternLine("fPf")
                                .patternLine("fff")
                                .unlockedBy("template", has(MODULE_TEMPLATE.get())))
        );
    }
}
