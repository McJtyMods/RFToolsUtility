package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.client.ClientSetup;
import mcjty.rftoolsutility.modules.environmental.client.EnvironmentalRenderer;
import mcjty.rftoolsutility.modules.environmental.client.GuiEnvironmentalController;
import mcjty.rftoolsutility.modules.environmental.items.EnvironmentalControllerItem;
import mcjty.rftoolsutility.modules.environmental.recipes.SyringeRecipeSerializer;
import mcjty.rftoolsutility.modules.environmental.recipes.SyringeRecipeType;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class EnvironmentalModule implements IModule {

    public static final RegistryObject<BaseBlock> ENVIRONENTAL_CONTROLLER = BLOCKS.register("environmental_controller", EnvironmentalControllerTileEntity::createBlock);
    public static final RegistryObject<Item> ENVIRONENTAL_CONTROLLER_ITEM = ITEMS.register("environmental_controller", () -> new BlockItem(ENVIRONENTAL_CONTROLLER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<EnvironmentalControllerTileEntity>> TYPE_ENVIRONENTAL_CONTROLLER = TILES.register("environmental_controller", () -> BlockEntityType.Builder.of(EnvironmentalControllerTileEntity::new, ENVIRONENTAL_CONTROLLER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_ENVIRONENTAL_CONTROLLER = CONTAINERS.register("environmental_controller", GenericContainer::createContainerType);

    public static final RegistryObject<Item> MODULE_TEMPLATE = ITEMS.register("module_template", () -> new Item(createStandardProperties()));
    public static final RegistryObject<Item> MODULEPLUS_TEMPLATE = ITEMS.register("moduleplus_template", () -> new Item(createStandardProperties()));

    public static final RegistryObject<EnvironmentalControllerItem> BLINDNESS_MODULE = ITEMS.register("blindness_module", EnvironmentalControllerItem::createBlindnessModule);
    public static final RegistryObject<EnvironmentalControllerItem> FEATHERFALLING_MODULE = ITEMS.register("featherfalling_module", EnvironmentalControllerItem::createFeatherfallingModule);
    public static final RegistryObject<EnvironmentalControllerItem> FEATHERFALLINGPLUS_MODULE = ITEMS.register("featherfallingplus_module", EnvironmentalControllerItem::createFeatherfallingPlusModule);
    public static final RegistryObject<EnvironmentalControllerItem> HASTE_MODULE = ITEMS.register("haste_module", EnvironmentalControllerItem::createHasteModule);
    public static final RegistryObject<EnvironmentalControllerItem> HASTEPLUS_MODULE = ITEMS.register("hasteplus_module", EnvironmentalControllerItem::createHastePlusModule);
    public static final RegistryObject<EnvironmentalControllerItem> FLIGHT_MODULE = ITEMS.register("flight_module", EnvironmentalControllerItem::createFlightModule);
    public static final RegistryObject<EnvironmentalControllerItem> GLOWING_MODULE = ITEMS.register("glowing_module", EnvironmentalControllerItem::createGlowingModule);
    public static final RegistryObject<EnvironmentalControllerItem> LUCK_MODULE = ITEMS.register("luck_module", EnvironmentalControllerItem::createLuckModule);
    public static final RegistryObject<EnvironmentalControllerItem> NIGHTVISION_MODULE = ITEMS.register("nightvision_module", EnvironmentalControllerItem::createNightvisionModule);
    public static final RegistryObject<EnvironmentalControllerItem> NOTELEPORT_MODULE = ITEMS.register("noteleport_module", EnvironmentalControllerItem::createNoteleportModule);
    public static final RegistryObject<EnvironmentalControllerItem> PEACEFUL_MODULE = ITEMS.register("peaceful_module", EnvironmentalControllerItem::createPeacefulModule);
    public static final RegistryObject<EnvironmentalControllerItem> POISON_MODULE = ITEMS.register("poison_module", EnvironmentalControllerItem::createPoisonModule);
    public static final RegistryObject<EnvironmentalControllerItem> REGENERATION_MODULE = ITEMS.register("regeneration_module", EnvironmentalControllerItem::createRegenerationModule);
    public static final RegistryObject<EnvironmentalControllerItem> REGENERATIONPLUS_MODULE = ITEMS.register("regenerationplus_module", EnvironmentalControllerItem::createRegenerationPlusModule);
    public static final RegistryObject<EnvironmentalControllerItem> SATURATION_MODULE = ITEMS.register("saturation_module", EnvironmentalControllerItem::createSaturationModule);
    public static final RegistryObject<EnvironmentalControllerItem> SATURATIONPLUS_MODULE = ITEMS.register("saturationplus_module", EnvironmentalControllerItem::createSaturationPlusModule);
    public static final RegistryObject<EnvironmentalControllerItem> SLOWNESS_MODULE = ITEMS.register("slowness_module", EnvironmentalControllerItem::createSlownessModule);
    public static final RegistryObject<EnvironmentalControllerItem> SPEED_MODULE = ITEMS.register("speed_module", EnvironmentalControllerItem::createSpeedModule);
    public static final RegistryObject<EnvironmentalControllerItem> SPEEDPLUS_MODULE = ITEMS.register("speedplus_module", EnvironmentalControllerItem::createSpeedPlusModule);
    public static final RegistryObject<EnvironmentalControllerItem> WATERBREATHING_MODULE = ITEMS.register("waterbreathing_module", EnvironmentalControllerItem::createWaterbreathingModule);
    public static final RegistryObject<EnvironmentalControllerItem> WEAKNESS_MODULE = ITEMS.register("weakness_module", EnvironmentalControllerItem::createWeaknessModule);

    public static final RegistryObject<SyringeRecipeSerializer> SYRINGE_SERIALIZER = RECIPE_SERIALIZERS.register("syringe", SyringeRecipeSerializer::new);

    public static final ResourceLocation SYRINGE_RECIPE_TYPE_ID = new ResourceLocation(RFToolsUtility.MODID, "syringe");
    public static final RegistryObject<SyringeRecipeType> SYRINGE_RECIPE_TYPE = RECIPE_TYPES.register("syringe", SyringeRecipeType::new);

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


    public EnvironmentalModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
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
    public void initConfig() {
        EnvironmentalConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
