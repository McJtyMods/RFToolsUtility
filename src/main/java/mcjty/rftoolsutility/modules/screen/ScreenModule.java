package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsutility.modules.screen.blocks.*;
import mcjty.rftoolsutility.modules.screen.client.GuiScreen;
import mcjty.rftoolsutility.modules.screen.client.GuiScreenController;
import mcjty.rftoolsutility.modules.screen.client.ScreenRenderer;
import mcjty.rftoolsutility.modules.screen.items.ScreenLinkItem;
import mcjty.rftoolsutility.modules.screen.items.modules.*;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;


public class ScreenModule implements IModule {

    public static final RegistryObject<ScreenBlock> SCREEN = BLOCKS.register("screen", () -> new ScreenBlock(ScreenTileEntity::new, false));
    public static final RegistryObject<BlockItem> SCREEN_ITEM = ITEMS.register("screen", () -> new BlockItem(SCREEN.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<ScreenTileEntity>> TYPE_SCREEN = TILES.register("screen", () -> BlockEntityType.Builder.of(ScreenTileEntity::new, SCREEN.get()).build(null));

    public static final RegistryObject<ScreenBlock> CREATIVE_SCREEN = BLOCKS.register("creative_screen", () -> new ScreenBlock(CreativeScreenTileEntity::new, true));
    public static final RegistryObject<BlockItem> CREATIVE_SCREEN_ITEM = ITEMS.register("creative_screen", () -> new BlockItem(CREATIVE_SCREEN.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<CreativeScreenTileEntity>> TYPE_CREATIVE_SCREEN = TILES.register("creative_screen", () -> BlockEntityType.Builder.of(CreativeScreenTileEntity::new, CREATIVE_SCREEN.get()).build(null));

    public static final RegistryObject<MenuType<ScreenContainer>> CONTAINER_SCREEN = CONTAINERS.register("screen", GenericContainer::createContainerType);
    public static final RegistryObject<MenuType<ScreenContainer>> CONTAINER_SCREEN_REMOTE = CONTAINERS.register("screen_remote",
            () -> GenericContainer.createRemoteContainerType(ScreenTileEntity::new, ScreenContainer::createRemote, ScreenContainer.SCREEN_MODULES));
    public static final RegistryObject<MenuType<ScreenContainer>> CONTAINER_SCREEN_REMOTE_CREATIVE = CONTAINERS.register("screen_remote_creative",
            () -> GenericContainer.createRemoteContainerType(CreativeScreenTileEntity::new, ScreenContainer::createRemoteCreative, ScreenContainer.SCREEN_MODULES));

    public static final RegistryObject<ScreenHitBlock> SCREEN_HIT = BLOCKS.register("screen_hitblock", ScreenHitBlock::new);
    public static final RegistryObject<BlockEntityType<?>> TYPE_SCREEN_HIT = TILES.register("screen_hitblock", () -> BlockEntityType.Builder.of(ScreenHitTileEntity::new, SCREEN_HIT.get()).build(null));

    public static final RegistryObject<ScreenControllerBlock> SCREEN_CONTROLLER = BLOCKS.register("screen_controller", ScreenControllerBlock::new);
    public static final RegistryObject<BlockItem> SCREEN_CONTROLLER_ITEM = ITEMS.register("screen_controller", () -> new BlockItem(SCREEN_CONTROLLER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_SCREEN_CONTROLLER = TILES.register("screen_controller", () -> BlockEntityType.Builder.of(ScreenControllerTileEntity::new, SCREEN_CONTROLLER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_SCREEN_CONTROLLER = CONTAINERS.register("screen_controller", GenericContainer::createContainerType);

    public static final RegistryObject<Item> TEXT_MODULE = ITEMS.register("text_module", TextModuleItem::new);
    public static final RegistryObject<Item> ENERGY_MODULE = ITEMS.register("energy_module", EnergyModuleItem::new);
    public static final RegistryObject<Item> ENERGYPLUS_MODULE = ITEMS.register("energyplus_module", EnergyPlusModuleItem::new);
    public static final RegistryObject<Item> INVENTORY_MODULE = ITEMS.register("inventory_module", InventoryModuleItem::new);
    public static final RegistryObject<Item> INVENTORYPLUS_MODULE = ITEMS.register("inventoryplus_module", InventoryPlusModuleItem::new);
    public static final RegistryObject<Item> CLOCK_MODULE = ITEMS.register("clock_module", ClockModuleItem::new);
    public static final RegistryObject<Item> FLUID_MODULE = ITEMS.register("fluid_module", FluidModuleItem::new);
    public static final RegistryObject<Item> FLUIDPLUS_MODULE = ITEMS.register("fluidplus_module", FluidPlusModuleItem::new);
    public static final RegistryObject<Item> MACHINEINFORMATION_MODULE = ITEMS.register("machineinformation_module", MachineInformationModuleItem::new);
    public static final RegistryObject<Item> COMPUTER_MODULE = ITEMS.register("computer_module", ComputerModuleItem::new);
    public static final RegistryObject<Item> BUTTON_MODULE = ITEMS.register("button_module", ButtonModuleItem::new);
    public static final RegistryObject<Item> ELEVATOR_MODULE = ITEMS.register("elevator_button_module", ElevatorButtonModuleItem::new);
    public static final RegistryObject<Item> REDSTONE_MODULE = ITEMS.register("redstone_module", RedstoneModuleItem::new);
    public static final RegistryObject<Item> COUNTER_MODULE = ITEMS.register("counter_module", CounterModuleItem::new);
    public static final RegistryObject<Item> COUNTERPLUS_MODULE = ITEMS.register("counterplus_module", CounterPlusModuleItem::new);

    public static final RegistryObject<TabletItem> TABLET_SCREEN = ITEMS.register("tablet_screen", TabletItem::new);
    public static final RegistryObject<ScreenLinkItem> SCREEN_LINK = ITEMS.register("screen_link", ScreenLinkItem::new);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiScreen.register();
            GuiScreenController.register();
        });

        ScreenRenderer.register();
    }

    @Override
    public void initConfig() {
        ScreenConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
