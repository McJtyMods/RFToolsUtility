package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.blocks.BaseBlockItem;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.blocks.*;
import mcjty.rftoolsutility.modules.screen.items.*;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;


public class ScreenSetup {
    @ObjectHolder("rftoolsutility:screen")
    public static ScreenBlock SCREEN;
    @ObjectHolder("rftoolsutility:creative_screen")
    public static ScreenBlock CREATIVE_SCREEN;
    @ObjectHolder("rftoolsutility:screen_hitblock")
    public static ScreenHitBlock SCREEN_HIT;
    @ObjectHolder("rftoolsutility:screen_controller")
    public static ScreenControllerBlock SCREEN_CONTROLLER;

    @ObjectHolder("rftoolsutility:text_module")
    public static TextModuleItem TEXT_MODULE;
    @ObjectHolder("rftoolsutility:energy_module")
    public static EnergyModuleItem ENERGY_MODULE;
    @ObjectHolder("rftoolsutility:energyplus_module")
    public static EnergyPlusModuleItem ENERGYPLUS_MODULE;
    @ObjectHolder("rftoolsutility:inventory_module")
    public static InventoryModuleItem INVENTORY_MODULE;
    @ObjectHolder("rftoolsutility:inventoryplus_module")
    public static InventoryPlusModuleItem INVENTORYPLUS_MODULE;
    @ObjectHolder("rftoolsutility:clock_module")
    public static ClockModuleItem CLOCK_MODULE;
    @ObjectHolder("rftoolsutility:fluid_module")
    public static FluidModuleItem FLUID_MODULE;
    @ObjectHolder("rftoolsutility:fluidplus_module")
    public static FluidPlusModuleItem FLUIDPLUS_MODULE;
    @ObjectHolder("rftoolsutility:machineinformation_module")
    public static MachineInformationModuleItem MACHINEINFORMATION_MODULE;
    @ObjectHolder("rftoolsutility:computer_module")
    public static ComputerModuleItem COMPUTER_MODULE;
    @ObjectHolder("rftoolsutility:button_module")
    public static ButtonModuleItem BUTTON_MODULE;
    @ObjectHolder("rftoolsutility:elevator_button_module")
    public static ElevatorButtonModuleItem ELEVATOR_MODULE;
    @ObjectHolder("rftoolsutility:redstone_module")
    public static RedstoneModuleItem REDSTONE_MODULE;
    @ObjectHolder("rftoolsutility:counter_module")
    public static CounterModuleItem COUNTER_MODULE;
    @ObjectHolder("rftoolsutility:counterplus_module")
    public static CounterPlusModuleItem COUNTERPLUS_MODULE;
    @ObjectHolder("rftoolsutility:storage_control_module")
    public static StorageControlModuleItem STORAGECONTROL_MODULE;
    @ObjectHolder("rftoolsutility:dump_module")
    public static DumpModuleItem DUMP_MODULE;

    @ObjectHolder("rftoolsutility:screen")
    public static TileEntityType<?> TYPE_SCREEN;
    @ObjectHolder("rftoolsutility:creative_screen")
    public static TileEntityType<?> TYPE_CREATIVE_SCREEN;
    @ObjectHolder("rftoolsutility:screen_hit")
    public static TileEntityType<?> TYPE_SCREEN_HIT;
    @ObjectHolder("rftoolsutility:screen_controller")
    public static TileEntityType<?> TYPE_SCREEN_CONTROLLER;

    @ObjectHolder("rftoolsutility:screen")
    public static ContainerType<GenericContainer> CONTAINER_SCREEN;
    @ObjectHolder("rftoolsutility:screen_controller")
    public static ContainerType<GenericContainer> CONTAINER_SCREEN_CONTROLLER;

    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new ScreenBlock("screen", ScreenTileEntity::new));
        event.getRegistry().register(new ScreenBlock("creative_screen", CreativeScreenTileEntity::new) {
            @Override
            public boolean isCreative() {
                return true;
            }
        });
        event.getRegistry().register(new ScreenHitBlock());
        event.getRegistry().register(new ScreenControllerBlock());
    }

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsUtility.setup.getTab());
        event.getRegistry().register(new BaseBlockItem(SCREEN, properties));
        event.getRegistry().register(new BaseBlockItem(CREATIVE_SCREEN, properties));
        event.getRegistry().register(new BaseBlockItem(SCREEN_HIT, properties));
        event.getRegistry().register(new BaseBlockItem(SCREEN_CONTROLLER, properties));

        event.getRegistry().register(new TextModuleItem());
        event.getRegistry().register(new InventoryModuleItem());
        event.getRegistry().register(new InventoryPlusModuleItem());
        event.getRegistry().register(new EnergyModuleItem());
        event.getRegistry().register(new EnergyPlusModuleItem());
        event.getRegistry().register(new ClockModuleItem());
        event.getRegistry().register(new FluidModuleItem());
        event.getRegistry().register(new FluidPlusModuleItem());
        event.getRegistry().register(new MachineInformationModuleItem());
        event.getRegistry().register(new ComputerModuleItem());
        event.getRegistry().register(new ButtonModuleItem());
        event.getRegistry().register(new ElevatorButtonModuleItem());
        event.getRegistry().register(new RedstoneModuleItem());
        event.getRegistry().register(new CounterModuleItem());
        event.getRegistry().register(new CounterPlusModuleItem());
        event.getRegistry().register(new StorageControlModuleItem());
        event.getRegistry().register(new DumpModuleItem());
    }

    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(ScreenTileEntity::new, SCREEN).build(null).setRegistryName("screen"));
        event.getRegistry().register(TileEntityType.Builder.create(CreativeScreenTileEntity::new, CREATIVE_SCREEN).build(null).setRegistryName("creative_screen"));
        event.getRegistry().register(TileEntityType.Builder.create(ScreenHitTileEntity::new, SCREEN_HIT).build(null).setRegistryName("screen_hit"));
        event.getRegistry().register(TileEntityType.Builder.create(ScreenControllerTileEntity::new, SCREEN_CONTROLLER).build(null).setRegistryName("screen_controller"));
    }

    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(GenericContainer.createContainerType("screen"));
        event.getRegistry().register(GenericContainer.createContainerType("screen_controller"));
    }

    public static void initClient() {
        SCREEN.initModel();
    }
}
