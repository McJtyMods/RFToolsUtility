package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.*;
import mcjty.rftoolsutility.modules.screen.items.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;


public class ScreenSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<ScreenBlock> SCREEN = BLOCKS.register("screen", () -> new ScreenBlock(ScreenTileEntity::new, false));
    public static final RegistryObject<ScreenBlockItem> SCREEN_ITEM = ITEMS.register("screen", () -> new ScreenBlockItem(SCREEN.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<ScreenTileEntity>> TYPE_SCREEN = TILES.register("screen", () -> TileEntityType.Builder.create(ScreenTileEntity::new, SCREEN.get()).build(null));

    public static final RegistryObject<ScreenBlock> CREATIVE_SCREEN = BLOCKS.register("creative_screen", () -> new ScreenBlock(CreativeScreenTileEntity::new, true));
    public static final RegistryObject<ScreenBlockItem> CREATIVE_SCREEN_ITEM = ITEMS.register("creative_screen", () -> new ScreenBlockItem(CREATIVE_SCREEN.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<CreativeScreenTileEntity>> TYPE_CREATIVE_SCREEN = TILES.register("creative_screen", () -> TileEntityType.Builder.create(CreativeScreenTileEntity::new, CREATIVE_SCREEN.get()).build(null));

    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_SCREEN = CONTAINERS.register("screen", GenericContainer::createContainerType);

    public static final RegistryObject<ScreenHitBlock> SCREEN_HIT = BLOCKS.register("screen_hitblock", ScreenHitBlock::new);
    public static final RegistryObject<TileEntityType<?>> TYPE_SCREEN_HIT = TILES.register("screen_hitblock", () -> TileEntityType.Builder.create(ScreenHitTileEntity::new, SCREEN_HIT.get()).build(null));

    public static final RegistryObject<ScreenControllerBlock> SCREEN_CONTROLLER = BLOCKS.register("screen_controller", ScreenControllerBlock::new);
    public static final RegistryObject<BlockItem> SCREEN_CONTROLLER_ITEM = ITEMS.register("screen_controller", () -> new BlockItem(SCREEN_CONTROLLER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_SCREEN_CONTROLLER = TILES.register("screen_controller", () -> TileEntityType.Builder.create(ScreenControllerTileEntity::new, SCREEN_CONTROLLER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_SCREEN_CONTROLLER = CONTAINERS.register("screen_controller", GenericContainer::createContainerType);

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
    public static final RegistryObject<ContainerType<ScreenTabletContainer>> CONTAINER_TABLET_SCREEN = CONTAINERS.register("tablet_screen",
            () -> IForgeContainerType.create((windowId, inv, data) -> new ScreenTabletContainer(windowId, null, McJtyLib.proxy.getClientPlayer())));

    public static void initClient() {
        SCREEN.get().initModel();
    }
}
