package mcjty.rftoolsutility.modules.logic;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.client.*;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.screen.client.GuiTabletScreen;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class LogicBlockModule implements IModule {

    public static final RegistryObject<LogicSlabBlock> ANALOG = BLOCKS.register("analog", AnalogTileEntity::createBlock);
    public static final RegistryObject<Item> ANALOG_ITEM = ITEMS.register("analog", () -> new BlockItem(ANALOG.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_ANALOG = TILES.register("analog", () -> BlockEntityType.Builder.of(AnalogTileEntity::new, ANALOG.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_ANALOG = CONTAINERS.register("analog", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> COUNTER = BLOCKS.register("counter", CounterTileEntity::createBlock);
    public static final RegistryObject<Item> COUNTER_ITEM = ITEMS.register("counter", () -> new BlockItem(COUNTER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_COUNTER = TILES.register("counter", () -> BlockEntityType.Builder.of(CounterTileEntity::new, COUNTER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_COUNTER = CONTAINERS.register("counter", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> DIGIT = BLOCKS.register("digit", DigitTileEntity::createBlock);
    public static final RegistryObject<Item> DIGIT_ITEM = ITEMS.register("digit", () -> new BlockItem(DIGIT.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<DigitTileEntity>> TYPE_DIGIT = TILES.register("digit", () -> BlockEntityType.Builder.of(DigitTileEntity::new, DIGIT.get()).build(null));

    public static final RegistryObject<LogicSlabBlock> INVCHECKER = BLOCKS.register("invchecker", InvCheckerTileEntity::createBlock);
    public static final RegistryObject<Item> INVCHECKER_ITEM = ITEMS.register("invchecker", () -> new BlockItem(INVCHECKER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_INVCHECKER = TILES.register("invchecker", () -> BlockEntityType.Builder.of(InvCheckerTileEntity::new, INVCHECKER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_INVCHECKER = CONTAINERS.register("invchecker", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> SENSOR = BLOCKS.register("sensor", SensorTileEntity::createBlock);
    public static final RegistryObject<Item> SENSOR_ITEM = ITEMS.register("sensor", () -> new BlockItem(SENSOR.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<SensorTileEntity>> TYPE_SENSOR = TILES.register("sensor", () -> BlockEntityType.Builder.of(SensorTileEntity::new, SENSOR.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_SENSOR = CONTAINERS.register("sensor", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> SEQUENCER = BLOCKS.register("sequencer", SequencerTileEntity::createBlock);
    public static final RegistryObject<Item> SEQUENCER_ITEM = ITEMS.register("sequencer", () -> new BlockItem(SEQUENCER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_SEQUENCER = TILES.register("sequencer", () -> BlockEntityType.Builder.of(SequencerTileEntity::new, SEQUENCER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_SEQUENCER = CONTAINERS.register("sequencer", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> LOGIC = BLOCKS.register("logic", ThreeLogicTileEntity::createBlock);
    public static final RegistryObject<Item> LOGIC_ITEM = ITEMS.register("logic", () -> new BlockItem(LOGIC.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_LOGIC = TILES.register("logic", () -> BlockEntityType.Builder.of(ThreeLogicTileEntity::new, LOGIC.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_LOGIC = CONTAINERS.register("logic", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> TIMER = BLOCKS.register("timer", TimerTileEntity::createBlock);
    public static final RegistryObject<Item> TIMER_ITEM = ITEMS.register("timer", () -> new BlockItem(TIMER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_TIMER = TILES.register("timer", () -> BlockEntityType.Builder.of(TimerTileEntity::new, TIMER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_TIMER = CONTAINERS.register("timer", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> WIRE = BLOCKS.register("wire", WireTileEntity::createBlock);
    public static final RegistryObject<Item> WIRE_ITEM = ITEMS.register("wire", () -> new BlockItem(WIRE.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_WIRE = TILES.register("wire", () -> BlockEntityType.Builder.of(WireTileEntity::new, WIRE.get()).build(null));

    public static final RegistryObject<LogicSlabBlock> REDSTONE_RECEIVER = BLOCKS.register("redstone_receiver", RedstoneReceiverTileEntity::createBlock);
    public static final RegistryObject<Item> REDSTONE_RECEIVER_ITEM = ITEMS.register("redstone_receiver", () -> new BlockItem(REDSTONE_RECEIVER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_REDSTONE_RECEIVER = TILES.register("redstone_receiver", () -> BlockEntityType.Builder.of(RedstoneReceiverTileEntity::new, REDSTONE_RECEIVER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_REDSTONE_RECEIVER = CONTAINERS.register("redstone_receiver", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> REDSTONE_TRANSMITTER = BLOCKS.register("redstone_transmitter", RedstoneTransmitterBlock::new);
    public static final RegistryObject<Item> REDSTONE_TRANSMITTER_ITEM = ITEMS.register("redstone_transmitter", () -> new BlockItem(REDSTONE_TRANSMITTER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_REDSTONE_TRANSMITTER = TILES.register("redstone_transmitter", () -> BlockEntityType.Builder.of(RedstoneTransmitterTileEntity::new, REDSTONE_TRANSMITTER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_REDSTONE_TRANSMITTER = CONTAINERS.register("redstone_transmitter", GenericContainer::createContainerType);

    public static final RegistryObject<RedstoneInformationItem> REDSTONE_INFORMATION = ITEMS.register("redstone_information", RedstoneInformationItem::new);
    public static final RegistryObject<MenuType<RedstoneInformationContainer>> CONTAINER_REDSTONE_INFORMATION = CONTAINERS.register("redstone_information",
            () -> IForgeContainerType.create((windowId, inv, data) -> new RedstoneInformationContainer(windowId, null, SafeClientTools.getClientPlayer())));
    public static final RegistryObject<TabletItem> TABLET_REDSTONE = ITEMS.register("tablet_redstone", TabletItem::new);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiAnalog.register();
            GuiCounter.register();
            GuiInvChecker.register();
            GuiSensor.register();
            GuiSequencer.register();
            GuiThreeLogic.register();
            GuiTimer.register();
            GuiRedstoneReceiver.register();
            GuiRedstoneTransmitter.register();
            GuiRedstoneInformation.register();
            GuiTabletScreen.register();

        });
        DigitRenderer.register();
    }


    @Override
    public void initConfig() {

    }
}
