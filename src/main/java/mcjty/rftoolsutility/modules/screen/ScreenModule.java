package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.screen.blocks.*;
import mcjty.rftoolsutility.modules.screen.client.GuiScreen;
import mcjty.rftoolsutility.modules.screen.client.GuiScreenController;
import mcjty.rftoolsutility.modules.screen.client.ScreenRenderer;
import mcjty.rftoolsutility.modules.screen.data.ScreenData;
import mcjty.rftoolsutility.modules.screen.items.ScreenLinkItem;
import mcjty.rftoolsutility.modules.screen.items.modules.*;
import mcjty.rftoolsutility.modules.screen.modules.*;
import mcjty.rftoolsutility.modules.screen.modulesclient.*;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;


public class ScreenModule implements IModule {

    public static final DeferredBlock<ScreenBlock> SCREEN = BLOCKS.register("screen", () -> new ScreenBlock(ScreenTileEntity::new, false));
    public static final DeferredItem<BlockItem> SCREEN_ITEM = ITEMS.register("screen", tab(() -> new BlockItem(SCREEN.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<ScreenTileEntity>> TYPE_SCREEN = TILES.register("screen", () -> BlockEntityType.Builder.of(ScreenTileEntity::new, SCREEN.get()).build(null));

    public static final DeferredBlock<ScreenBlock> CREATIVE_SCREEN = BLOCKS.register("creative_screen", () -> new ScreenBlock(CreativeScreenTileEntity::new, true));
    public static final DeferredItem<BlockItem> CREATIVE_SCREEN_ITEM = ITEMS.register("creative_screen", tab(() -> new BlockItem(CREATIVE_SCREEN.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<CreativeScreenTileEntity>> TYPE_CREATIVE_SCREEN = TILES.register("creative_screen", () -> BlockEntityType.Builder.of(CreativeScreenTileEntity::new, CREATIVE_SCREEN.get()).build(null));

    public static final Supplier<MenuType<ScreenContainer>> CONTAINER_SCREEN = CONTAINERS.register("screen", GenericContainer::createContainerType);
    public static final Supplier<MenuType<ScreenContainer>> CONTAINER_SCREEN_REMOTE = CONTAINERS.register("screen_remote",
            () -> GenericContainer.createRemoteContainerType(ScreenTileEntity::new, ScreenContainer::createRemote, ScreenContainer.SCREEN_MODULES));
    public static final Supplier<MenuType<ScreenContainer>> CONTAINER_SCREEN_REMOTE_CREATIVE = CONTAINERS.register("screen_remote_creative",
            () -> GenericContainer.createRemoteContainerType(CreativeScreenTileEntity::new, ScreenContainer::createRemoteCreative, ScreenContainer.SCREEN_MODULES));

    public static final DeferredBlock<ScreenHitBlock> SCREEN_HIT = BLOCKS.register("screen_hitblock", ScreenHitBlock::new);
    public static final Supplier<BlockEntityType<?>> TYPE_SCREEN_HIT = TILES.register("screen_hitblock", () -> BlockEntityType.Builder.of(ScreenHitTileEntity::new, SCREEN_HIT.get()).build(null));

    public static final DeferredBlock<ScreenControllerBlock> SCREEN_CONTROLLER = BLOCKS.register("screen_controller", ScreenControllerBlock::new);
    public static final DeferredItem<BlockItem> SCREEN_CONTROLLER_ITEM = ITEMS.register("screen_controller", tab(() -> new BlockItem(SCREEN_CONTROLLER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_SCREEN_CONTROLLER = TILES.register("screen_controller", () -> BlockEntityType.Builder.of(ScreenControllerTileEntity::new, SCREEN_CONTROLLER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SCREEN_CONTROLLER = CONTAINERS.register("screen_controller", GenericContainer::createContainerType);

    public static final DeferredItem<Item> TEXT_MODULE = ITEMS.register("text_module", tab(TextModuleItem::new));
    public static final DeferredItem<Item> ENERGY_MODULE = ITEMS.register("energy_module", tab(EnergyModuleItem::new));
    public static final DeferredItem<Item> ENERGYPLUS_MODULE = ITEMS.register("energyplus_module", tab(EnergyPlusModuleItem::new));
    public static final DeferredItem<Item> INVENTORY_MODULE = ITEMS.register("inventory_module", tab(InventoryModuleItem::new));
    public static final DeferredItem<Item> INVENTORYPLUS_MODULE = ITEMS.register("inventoryplus_module", tab(InventoryPlusModuleItem::new));
    public static final DeferredItem<Item> CLOCK_MODULE = ITEMS.register("clock_module", tab(ClockModuleItem::new));
    public static final DeferredItem<Item> FLUID_MODULE = ITEMS.register("fluid_module", tab(FluidModuleItem::new));
    public static final DeferredItem<Item> FLUIDPLUS_MODULE = ITEMS.register("fluidplus_module", tab(FluidPlusModuleItem::new));
    public static final DeferredItem<Item> MACHINEINFORMATION_MODULE = ITEMS.register("machineinformation_module", tab(MachineInformationModuleItem::new));
    public static final DeferredItem<Item> BUTTON_MODULE = ITEMS.register("button_module", tab(ButtonModuleItem::new));
    public static final DeferredItem<Item> REDSTONE_MODULE = ITEMS.register("redstone_module", tab(RedstoneModuleItem::new));
    public static final DeferredItem<Item> COUNTER_MODULE = ITEMS.register("counter_module", tab(CounterModuleItem::new));
    public static final DeferredItem<Item> COUNTERPLUS_MODULE = ITEMS.register("counterplus_module", tab(CounterPlusModuleItem::new));

    public static final DeferredItem<TabletItem> TABLET_SCREEN = ITEMS.register("tablet_screen", tab(TabletItem::new));
    public static final DeferredItem<ScreenLinkItem> SCREEN_LINK = ITEMS.register("screen_link", tab(ScreenLinkItem::new));

    public static final Supplier<AttachmentType<ScreenData>> SCREEN_DATA = ATTACHMENT_TYPES.register(
            "screen_data", () -> AttachmentType.builder(ScreenData::createDefault)
                    .serialize(ScreenData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ScreenData>> ITEM_SCREEN_DATA = COMPONENTS.registerComponentType(
            "screen_data",
            builder -> builder
                    .persistent(ScreenData.CODEC)
                    .networkSynchronized(ScreenData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyBarScreenModule>> MODULE_ENERGY_BAR_DATA = COMPONENTS.registerComponentType(
            "module_energy_bar_data",
            builder -> builder
                    .persistent(EnergyBarScreenModule.CODEC)
                    .networkSynchronized(EnergyBarScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyBarClientScreenModule>> CLIENTMODULE_ENERGY_BAR_DATA = COMPONENTS.registerComponentType(
            "clientmodule_energy_bar_data",
            builder -> builder
                    .persistent(EnergyBarClientScreenModule.CODEC)
                    .networkSynchronized(EnergyBarClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ButtonScreenModule>> MODULE_BUTTON_DATA = COMPONENTS.registerComponentType(
            "module_button_data",
            builder -> builder
                    .persistent(ButtonScreenModule.CODEC)
                    .networkSynchronized(ButtonScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ButtonClientScreenModule>> CLIENTMODULE_BUTTON_DATA = COMPONENTS.registerComponentType(
            "clientmodule_button_data",
            builder -> builder
                    .persistent(ButtonClientScreenModule.CODEC)
                    .networkSynchronized(ButtonClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ClockClientScreenModule>> CLIENTMODULE_CLOCK_DATA = COMPONENTS.registerComponentType(
            "clientmodule_clock_data",
            builder -> builder
                    .persistent(ClockClientScreenModule.CODEC)
                    .networkSynchronized(ClockClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CounterScreenModule>> MODULE_COUNTER_DATA = COMPONENTS.registerComponentType(
            "module_counter_data",
            builder -> builder
                    .persistent(CounterScreenModule.CODEC)
                    .networkSynchronized(CounterScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CounterClientScreenModule>> CLIENTMODULE_COUNTER_DATA = COMPONENTS.registerComponentType(
            "clientmodule_counter_data",
            builder -> builder
                    .persistent(CounterClientScreenModule.CODEC)
                    .networkSynchronized(CounterClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidBarScreenModule>> MODULE_FLUIDBAR_DATA = COMPONENTS.registerComponentType(
            "module_fluidbar_data",
            builder -> builder
                    .persistent(FluidBarScreenModule.CODEC)
                    .networkSynchronized(FluidBarScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidBarClientScreenModule>> CLIENTMODULE_FLUIDBAR_DATA = COMPONENTS.registerComponentType(
            "clientmodule_fluidbar_data",
            builder -> builder
                    .persistent(FluidBarClientScreenModule.CODEC)
                    .networkSynchronized(FluidBarClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InventoryScreenModule>> MODULE_INVENTORY_DATA = COMPONENTS.registerComponentType(
            "module_inventory_data",
            builder -> builder
                    .persistent(InventoryScreenModule.CODEC)
                    .networkSynchronized(InventoryScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InventoryClientScreenModule>> CLIENTMODULE_INVENTORY_DATA = COMPONENTS.registerComponentType(
            "clientmodule_inventory_data",
            builder -> builder
                    .persistent(InventoryClientScreenModule.CODEC)
                    .networkSynchronized(InventoryClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineInformationScreenModule>> MODULE_MACHINEINFO_DATA = COMPONENTS.registerComponentType(
            "module_machineinfo_data",
            builder -> builder
                    .persistent(MachineInformationScreenModule.CODEC)
                    .networkSynchronized(MachineInformationScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineInformationClientScreenModule>> CLIENTMODULE_MACHINEINFO_DATA = COMPONENTS.registerComponentType(
            "clientmodule_machineinfo_data",
            builder -> builder
                    .persistent(MachineInformationClientScreenModule.CODEC)
                    .networkSynchronized(MachineInformationClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneScreenModule>> MODULE_REDSTONE_DATA = COMPONENTS.registerComponentType(
            "module_redstone_data",
            builder -> builder
                    .persistent(RedstoneScreenModule.CODEC)
                    .networkSynchronized(RedstoneScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneClientScreenModule>> CLIENTMODULE_REDSTONE_DATA = COMPONENTS.registerComponentType(
            "clientmodule_redstone_data",
            builder -> builder
                    .persistent(RedstoneClientScreenModule.CODEC)
                    .networkSynchronized(RedstoneClientScreenModule.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TextScreenModule>> MODULE_TEXT_DATA = COMPONENTS.registerComponentType(
            "module_text_data",
            builder -> builder
                    .persistent(TextScreenModule.CODEC)
                    .networkSynchronized(TextScreenModule.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TextClientScreenModule>> CLIENTMODULE_TEXT_DATA = COMPONENTS.registerComponentType(
            "clientmodule_text_data",
            builder -> builder
                    .persistent(TextClientScreenModule.CODEC)
                    .networkSynchronized(TextClientScreenModule.STREAM_CODEC));

    public ScreenModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiScreen.register(event);
        GuiScreenController.register(event);
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        ScreenRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
        ScreenConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(SCREEN)
                        .ironPickaxeTags()
                        .parentedItem("block/screen")
                        .standardLoot(ITEM_SCREEN_DATA.get())
                        .blockState(p -> p.orientedBlock(SCREEN.get(), DataGenHelper.screenModel(p, "screen", p.modLoc("block/screenframe_icon"))))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("base", has(VariousModule.MACHINE_BASE.get())),
                                "GGG", "GAG", "iii"),
                Dob.blockBuilder(CREATIVE_SCREEN)
                        .ironPickaxeTags()
                        .parentedItem("block/creative_screen")
                        .standardLoot(ITEM_SCREEN_DATA.get())
                        .blockState(p -> p.orientedBlock(CREATIVE_SCREEN.get(), DataGenHelper.screenModel(p, "creative_screen", p.modLoc("block/creative_screenframe_icon")))),
                Dob.blockBuilder(SCREEN_HIT)
                        .blockState(p -> p.orientedBlock(SCREEN_HIT.get(), DataGenHelper.screenModel(p, "screen", p.modLoc("block/screenframe_icon")))),
                Dob.blockBuilder(SCREEN_CONTROLLER)
                        .ironPickaxeTags()
                        .parentedItem("block/screen_controller")
                        .standardLoot(ITEM_SCREEN_DATA.get())
                        .blockState(p -> p.orientedBlock(SCREEN_CONTROLLER.get(), p.frontBasedModel("screen_controller", p.modLoc("block/machinescreencontroller"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "ror", "GFG", "rGr"),
                Dob.itemBuilder(TEXT_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " p ", "rir", " Z "),
                Dob.itemBuilder(ENERGY_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " r ", "rir", " Z "),
                Dob.itemBuilder(ENERGYPLUS_MODULE)
                        .shaped(builder -> builder
                                        .define('z', Tags.Items.INGOTS_GOLD)
                                        .define('M', ENERGY_MODULE.get())
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " o ", "zMz", " o "),
                Dob.itemBuilder(INVENTORY_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Tags.Items.CHESTS)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(INVENTORYPLUS_MODULE)
                        .shaped(builder -> builder
                                        .define('z', Tags.Items.INGOTS_GOLD)
                                        .define('M', INVENTORY_MODULE.get())
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " o ", "zMz", " o "),
                Dob.itemBuilder(CLOCK_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Items.CLOCK)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(FLUID_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Items.BUCKET)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(FLUIDPLUS_MODULE)
                        .shaped(builder -> builder
                                        .define('z', Tags.Items.INGOTS_GOLD)
                                        .define('M', FLUID_MODULE.get())
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " o ", "zMz", " o "),
                Dob.itemBuilder(MACHINEINFORMATION_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Items.FURNACE)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(BUTTON_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Items.STONE_BUTTON)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(REDSTONE_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Items.REPEATER)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(COUNTER_MODULE)
                        .shaped(builder -> builder
                                        .define('Z', Tags.Items.DYES_BLACK)
                                        .define('X', Items.COMPARATOR)
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " X ", "rir", " Z "),
                Dob.itemBuilder(COUNTERPLUS_MODULE)
                        .shaped(builder -> builder
                                        .define('z', Tags.Items.INGOTS_GOLD)
                                        .define('M', COUNTER_MODULE.get())
                                        .unlockedBy("ingot", has(Items.IRON_INGOT)),
                                " o ", "zMz", " o "),
                Dob.itemBuilder(SCREEN_LINK)
                        .shaped(builder -> builder
                                        .define('P', Tags.Items.GLASS_PANES)
                                        .unlockedBy("redstone", has(Items.REDSTONE)),
                                "ror", "PPP", "rrr")
        );
    }
}
