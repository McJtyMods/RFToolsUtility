package mcjty.rftoolsutility.modules.logic;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.client.*;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.screen.client.GuiTabletScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class LogicBlockModule implements IModule {

    public static final RegistryObject<LogicSlabBlock> ANALOG = BLOCKS.register("analog", AnalogTileEntity::createBlock);
    public static final RegistryObject<Item> ANALOG_ITEM = ITEMS.register("analog", () -> new BlockItem(ANALOG.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_ANALOG = TILES.register("analog", () -> TileEntityType.Builder.create(AnalogTileEntity::new, ANALOG.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ANALOG = CONTAINERS.register("analog", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> COUNTER = BLOCKS.register("counter", CounterTileEntity::createBlock);
    public static final RegistryObject<Item> COUNTER_ITEM = ITEMS.register("counter", () -> new BlockItem(COUNTER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_COUNTER = TILES.register("counter", () -> TileEntityType.Builder.create(AnalogTileEntity::new, COUNTER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_COUNTER = CONTAINERS.register("counter", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> DIGIT = BLOCKS.register("digit", DigitTileEntity::createBlock);
    public static final RegistryObject<Item> DIGIT_ITEM = ITEMS.register("digit", () -> new BlockItem(DIGIT.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<DigitTileEntity>> TYPE_DIGIT = TILES.register("digit", () -> TileEntityType.Builder.create(DigitTileEntity::new, DIGIT.get()).build(null));

    public static final RegistryObject<LogicSlabBlock> INVCHECKER = BLOCKS.register("invchecker", InvCheckerTileEntity::createBlock);
    public static final RegistryObject<Item> INVCHECKER_ITEM = ITEMS.register("invchecker", () -> new BlockItem(INVCHECKER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_INVCHECKER = TILES.register("invchecker", () -> TileEntityType.Builder.create(InvCheckerTileEntity::new, INVCHECKER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_INVCHECKER = CONTAINERS.register("invchecker", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> SENSOR = BLOCKS.register("sensor", SensorTileEntity::createBlock);
    public static final RegistryObject<Item> SENSOR_ITEM = ITEMS.register("sensor", () -> new BlockItem(SENSOR.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<SensorTileEntity>> TYPE_SENSOR = TILES.register("sensor", () -> TileEntityType.Builder.create(SensorTileEntity::new, SENSOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_SENSOR = CONTAINERS.register("sensor", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> SEQUENCER = BLOCKS.register("sequencer", SequencerTileEntity::createBlock);
    public static final RegistryObject<Item> SEQUENCER_ITEM = ITEMS.register("sequencer", () -> new BlockItem(SEQUENCER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_SEQUENCER = TILES.register("sequencer", () -> TileEntityType.Builder.create(SequencerTileEntity::new, SEQUENCER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_SEQUENCER = CONTAINERS.register("sequencer", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> LOGIC = BLOCKS.register("logic", ThreeLogicTileEntity::createBlock);
    public static final RegistryObject<Item> LOGIC_ITEM = ITEMS.register("logic", () -> new BlockItem(LOGIC.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_LOGIC = TILES.register("logic", () -> TileEntityType.Builder.create(ThreeLogicTileEntity::new, LOGIC.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_LOGIC = CONTAINERS.register("logic", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> TIMER = BLOCKS.register("timer", TimerTileEntity::createBlock);
    public static final RegistryObject<Item> TIMER_ITEM = ITEMS.register("timer", () -> new BlockItem(TIMER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_TIMER = TILES.register("timer", () -> TileEntityType.Builder.create(TimerTileEntity::new, TIMER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_TIMER = CONTAINERS.register("timer", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> WIRE = BLOCKS.register("wire", WireTileEntity::createBlock);
    public static final RegistryObject<Item> WIRE_ITEM = ITEMS.register("wire", () -> new BlockItem(WIRE.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_WIRE = TILES.register("wire", () -> TileEntityType.Builder.create(WireTileEntity::new, WIRE.get()).build(null));

    public static final RegistryObject<LogicSlabBlock> REDSTONE_RECEIVER = BLOCKS.register("redstone_receiver", RedstoneReceiverTileEntity::createBlock);
    public static final RegistryObject<Item> REDSTONE_RECEIVER_ITEM = ITEMS.register("redstone_receiver", () -> new BlockItem(REDSTONE_RECEIVER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_REDSTONE_RECEIVER = TILES.register("redstone_receiver", () -> TileEntityType.Builder.create(RedstoneReceiverTileEntity::new, REDSTONE_RECEIVER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_REDSTONE_RECEIVER = CONTAINERS.register("redstone_receiver", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> REDSTONE_TRANSMITTER = BLOCKS.register("redstone_transmitter", RedstoneTransmitterBlock::new);
    public static final RegistryObject<Item> REDSTONE_TRANSMITTER_ITEM = ITEMS.register("redstone_transmitter", () -> new BlockItem(REDSTONE_TRANSMITTER.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_REDSTONE_TRANSMITTER = TILES.register("redstone_transmitter", () -> TileEntityType.Builder.create(RedstoneTransmitterTileEntity::new, REDSTONE_TRANSMITTER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_REDSTONE_TRANSMITTER = CONTAINERS.register("redstone_transmitter", GenericContainer::createContainerType);

    public static final RegistryObject<RedstoneInformationItem> REDSTONE_INFORMATION = ITEMS.register("redstone_information", RedstoneInformationItem::new);
    public static final RegistryObject<ContainerType<RedstoneInformationContainer>> CONTAINER_REDSTONE_INFORMATION = CONTAINERS.register("redstone_information",
            () -> IForgeContainerType.create((windowId, inv, data) -> new RedstoneInformationContainer(windowId, null, McJtyLib.proxy.getClientPlayer())));
    public static final RegistryObject<TabletItem> TABLET_REDSTONE = ITEMS.register("tablet_redstone", TabletItem::new);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_ANALOG.get(), GuiAnalog::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_COUNTER.get(), GuiCounter::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_INVCHECKER.get(), GuiInvChecker::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_SENSOR.get(), GuiSensor::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_SEQUENCER.get(), GuiSequencer::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_LOGIC.get(), GuiThreeLogic::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_TIMER.get(), GuiTimer::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_REDSTONE_RECEIVER.get(), GuiRedstoneReceiver::new);
            GenericGuiContainer.register(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER.get(), GuiRedstoneTransmitter::new);

            ScreenManager.registerFactory(LogicBlockModule.CONTAINER_REDSTONE_INFORMATION.get(), LogicBlockModule::createRedstoneInformationGui);
            ScreenManager.IScreenFactory<ScreenContainer, GuiTabletScreen> factory = (container, inventory, title) -> {
                TileEntity te = container.getTe();
                return Tools.safeMap(te, (ScreenTileEntity tile) -> new GuiTabletScreen(tile, container, inventory), "Invalid tile entity!");
            };
            ScreenManager.registerFactory(ScreenModule.CONTAINER_SCREEN_REMOTE.get(), factory);

        });
        DigitRenderer.register();
    }

    private static GuiRedstoneInformation createRedstoneInformationGui(RedstoneInformationContainer container, PlayerInventory inventory, ITextComponent textComponent) {
        return new GuiRedstoneInformation(container, inventory);
    }


    @Override
    public void initConfig() {

    }
}