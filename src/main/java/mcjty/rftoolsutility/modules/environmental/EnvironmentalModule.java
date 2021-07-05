package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.client.ClientSetup;
import mcjty.rftoolsutility.modules.environmental.client.EnvironmentalTESR;
import mcjty.rftoolsutility.modules.environmental.client.GuiEnvironmentalController;
import mcjty.rftoolsutility.modules.environmental.items.*;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class EnvironmentalModule implements IModule {

    public static final RegistryObject<BaseBlock> ENVIRONENTAL_CONTROLLER = BLOCKS.register("environmental_controller", EnvironmentalControllerTileEntity::createBlock);
    public static final RegistryObject<Item> ENVIRONENTAL_CONTROLLER_ITEM = ITEMS.register("environmental_controller", () -> new BlockItem(ENVIRONENTAL_CONTROLLER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<EnvironmentalControllerTileEntity>> TYPE_ENVIRONENTAL_CONTROLLER = TILES.register("environmental_controller", () -> TileEntityType.Builder.of(EnvironmentalControllerTileEntity::new, ENVIRONENTAL_CONTROLLER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ENVIRONENTAL_CONTROLLER = CONTAINERS.register("environmental_controller", GenericContainer::createContainerType);

    public static final RegistryObject<RegenerationEModuleItem> REGENERATION_MODULE = ITEMS.register("regeneration_module", RegenerationEModuleItem::new);
    public static final RegistryObject<RegenerationPlusEModuleItem> REGENERATIONPLUS_MODULE = ITEMS.register("regenerationplus_module", RegenerationPlusEModuleItem::new);
    public static final RegistryObject<SpeedEModuleItem> SPEED_MODULE = ITEMS.register("speed_module", SpeedEModuleItem::new);
    public static final RegistryObject<SpeedPlusEModuleItem> SPEEDPLUS_MODULE = ITEMS.register("speedplus_module", SpeedPlusEModuleItem::new);
    public static final RegistryObject<HasteEModuleItem> HASTE_MODULE = ITEMS.register("haste_module", HasteEModuleItem::new);
    public static final RegistryObject<HastePlusEModuleItem> HASTEPLUS_MODULE = ITEMS.register("hasteplus_module", HastePlusEModuleItem::new);
    public static final RegistryObject<SaturationEModuleItem> SATURATION_MODULE = ITEMS.register("saturation_module", SaturationEModuleItem::new);
    public static final RegistryObject<SaturationPlusEModuleItem> SATURATIONPLUS_MODULE = ITEMS.register("saturationplus_module", SaturationPlusEModuleItem::new);
    public static final RegistryObject<FeatherFallingEModuleItem> FEATHERFALLING_MODULE = ITEMS.register("featherfalling_module", FeatherFallingEModuleItem::new);
    public static final RegistryObject<FeatherFallingPlusEModuleItem> FEATHERFALLINGPLUS_MODULE = ITEMS.register("featherfallingplus_module", FeatherFallingPlusEModuleItem::new);
    public static final RegistryObject<FlightEModuleItem> FLIGHT_MODULE = ITEMS.register("flight_module", FlightEModuleItem::new);
    public static final RegistryObject<PeacefulEModuleItem> PEACEFUL_MODULE = ITEMS.register("peaceful_module", PeacefulEModuleItem::new);
    public static final RegistryObject<WaterBreathingEModuleItem> WATERBREATHING_MODULE = ITEMS.register("waterbreathing_module", WaterBreathingEModuleItem::new);
    public static final RegistryObject<NightVisionEModuleItem> NIGHTVISION_MODULE = ITEMS.register("nightvision_module", NightVisionEModuleItem::new);
    public static final RegistryObject<GlowingEModuleItem> GLOWING_MODULE = ITEMS.register("glowing_module", GlowingEModuleItem::new);
    public static final RegistryObject<LuckEModuleItem> LUCK_MODULE = ITEMS.register("luck_module", LuckEModuleItem::new);
    public static final RegistryObject<NoTeleportEModuleItem> NOTELEPORT_MODULE = ITEMS.register("noteleport_module", NoTeleportEModuleItem::new);
    public static final RegistryObject<BlindnessEModuleItem> BLINDNESS_MODULE = ITEMS.register("blindness_module", BlindnessEModuleItem::new);
    public static final RegistryObject<WeaknessEModuleItem> WEAKNESS_MODULE = ITEMS.register("weakness_module", WeaknessEModuleItem::new);
    public static final RegistryObject<PoisonEModuleItem> POISON_MODULE = ITEMS.register("poison_module", PoisonEModuleItem::new);
    public static final RegistryObject<SlownessEModuleItem> SLOWNESS_MODULE = ITEMS.register("slowness_module", SlownessEModuleItem::new);

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

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiEnvironmentalController.register();
        });
        ClientSetup.initClient();
        EnvironmentalTESR.register();
    }

    @Override
    public void initConfig() {

    }
}
