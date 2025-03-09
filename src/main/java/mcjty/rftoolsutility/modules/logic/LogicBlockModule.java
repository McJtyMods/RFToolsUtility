package mcjty.rftoolsutility.modules.logic;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.client.*;
import mcjty.rftoolsutility.modules.logic.data.*;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.screen.client.GuiTabletScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class LogicBlockModule implements IModule {

    public static final RBlock<LogicSlabBlock, BlockItem, AnalogTileEntity> ANALOG = RBLOCKS.registerBlock("analog",
            AnalogTileEntity.class,
            AnalogTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            AnalogTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ANALOG = CONTAINERS.register("analog", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, CounterTileEntity> COUNTER = RBLOCKS.registerBlock("counter",
            CounterTileEntity.class,
            CounterTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            CounterTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_COUNTER = CONTAINERS.register("counter", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, DigitTileEntity> DIGIT = RBLOCKS.registerBlock("digit",
            DigitTileEntity.class,
            DigitTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            DigitTileEntity::new);

    public static final RBlock<LogicSlabBlock, BlockItem, InvCheckerTileEntity> INVCHECKER = RBLOCKS.registerBlock("invchecker",
            InvCheckerTileEntity.class,
            InvCheckerTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            InvCheckerTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_INVCHECKER = CONTAINERS.register("invchecker", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, SensorTileEntity> SENSOR = RBLOCKS.registerBlock("sensor",
            SensorTileEntity.class,
            SensorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SensorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SENSOR = CONTAINERS.register("sensor", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, SequencerTileEntity> SEQUENCER = RBLOCKS.registerBlock("sequencer",
            SequencerTileEntity.class,
            SequencerTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SequencerTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SEQUENCER = CONTAINERS.register("sequencer", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, ThreeLogicTileEntity> LOGIC = RBLOCKS.registerBlock("logic",
            ThreeLogicTileEntity.class,
            ThreeLogicTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            ThreeLogicTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_LOGIC = CONTAINERS.register("logic", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, TimerTileEntity> TIMER = RBLOCKS.registerBlock("timer",
            TimerTileEntity.class,
            TimerTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            TimerTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_TIMER = CONTAINERS.register("timer", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, WireTileEntity> WIRE = RBLOCKS.registerBlock("wire",
            WireTileEntity.class,
            WireTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            WireTileEntity::new);

    public static final RBlock<LogicSlabBlock, BlockItem, RedstoneReceiverTileEntity> REDSTONE_RECEIVER = RBLOCKS.registerBlock("redstone_receiver",
            RedstoneReceiverTileEntity.class,
            RedstoneReceiverTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            RedstoneReceiverTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_REDSTONE_RECEIVER = CONTAINERS.register("redstone_receiver", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, RedstoneTransmitterTileEntity> REDSTONE_TRANSMITTER = RBLOCKS.registerBlock("redstone_transmitter",
            RedstoneTransmitterTileEntity.class,
            RedstoneTransmitterBlock::new,
            block -> new BlockItem(block.get(), createStandardProperties()),
            RedstoneTransmitterTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_REDSTONE_TRANSMITTER = CONTAINERS.register("redstone_transmitter", GenericContainer::createContainerType);

    public static final DeferredItem<RedstoneInformationItem> REDSTONE_INFORMATION = ITEMS.register("redstone_information", tab(RedstoneInformationItem::new));
    public static final Supplier<MenuType<RedstoneInformationContainer>> CONTAINER_REDSTONE_INFORMATION = CONTAINERS.register("redstone_information",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new RedstoneInformationContainer(windowId, null, SafeClientTools.getClientPlayer())));
    public static final DeferredItem<TabletItem> TABLET_REDSTONE = ITEMS.register("tablet_redstone", tab(TabletItem::new));

    public static final Supplier<AttachmentType<AnalogData>> ANALOG_DATA = ATTACHMENT_TYPES.register(
            "analog_data", () -> AttachmentType.builder(AnalogData::createDefault)
                    .serialize(AnalogData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AnalogData>> ITEM_ANALOG_DATA = COMPONENTS.registerComponentType(
            "analog_data",
            builder -> builder
                    .persistent(AnalogData.CODEC)
                    .networkSynchronized(AnalogData.STREAM_CODEC));

    public static final Supplier<AttachmentType<CounterData>> COUNTER_DATA = ATTACHMENT_TYPES.register(
            "counter_data", () -> AttachmentType.builder(CounterData::createDefault)
                    .serialize(CounterData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CounterData>> ITEM_COUNTER_DATA = COMPONENTS.registerComponentType(
            "counter_data",
            builder -> builder
                    .persistent(CounterData.CODEC)
                    .networkSynchronized(CounterData.STREAM_CODEC));

    public static final Supplier<AttachmentType<IncCheckerData>> INVCHECKER_DATA = ATTACHMENT_TYPES.register(
            "invchecker_data", () -> AttachmentType.builder(IncCheckerData::createDefault)
                    .serialize(IncCheckerData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IncCheckerData>> ITEM_INVCHECKER_DATA = COMPONENTS.registerComponentType(
            "invchecker_data",
            builder -> builder
                    .persistent(IncCheckerData.CODEC)
                    .networkSynchronized(IncCheckerData.STREAM_CODEC));

    public static final Supplier<AttachmentType<RedstoneChannelData>> REDSTONECHANNEL_DATA = ATTACHMENT_TYPES.register(
            "redstonechannel_data", () -> AttachmentType.builder(RedstoneChannelData::createDefault)
                    .serialize(RedstoneChannelData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneChannelData>> ITEM_REDSTONECHANNEL_DATA = COMPONENTS.registerComponentType(
            "redstonechannel_data",
            builder -> builder
                    .persistent(RedstoneChannelData.CODEC)
                    .networkSynchronized(RedstoneChannelData.STREAM_CODEC));

    public static final Supplier<AttachmentType<RedstoneReceiverData>> REDSTONERECEIVER_DATA = ATTACHMENT_TYPES.register(
            "redstonereceiver_data", () -> AttachmentType.builder(RedstoneReceiverData::createDefault)
                    .serialize(RedstoneReceiverData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneReceiverData>> ITEM_REDSTONERECEIVER_DATA = COMPONENTS.registerComponentType(
            "redstonereceiver_data",
            builder -> builder
                    .persistent(RedstoneReceiverData.CODEC)
                    .networkSynchronized(RedstoneReceiverData.STREAM_CODEC));

    public static final Supplier<AttachmentType<SensorData>> SENSOR_DATA = ATTACHMENT_TYPES.register(
            "sensor_data", () -> AttachmentType.builder(SensorData::createDefault)
                    .serialize(SensorData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SensorData>> ITEM_SENSOR_DATA = COMPONENTS.registerComponentType(
            "sensor_data",
            builder -> builder
                    .persistent(SensorData.CODEC)
                    .networkSynchronized(SensorData.STREAM_CODEC));

    public static final Supplier<AttachmentType<SequencerData>> SEQUENCER_DATA = ATTACHMENT_TYPES.register(
            "sequencer_data", () -> AttachmentType.builder(SequencerData::createDefault)
                    .serialize(SequencerData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SequencerData>> ITEM_SEQUENCER_DATA = COMPONENTS.registerComponentType(
            "sequencer_data",
            builder -> builder
                    .persistent(SequencerData.CODEC)
                    .networkSynchronized(SequencerData.STREAM_CODEC));

    public static final Supplier<AttachmentType<ThreeLogicData>> THREELOGIC_DATA = ATTACHMENT_TYPES.register(
            "threelogic_data", () -> AttachmentType.builder(ThreeLogicData::createDefault)
                    .serialize(ThreeLogicData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ThreeLogicData>> ITEM_THREELOGIC_DATA = COMPONENTS.registerComponentType(
            "threelogic_data",
            builder -> builder
                    .persistent(ThreeLogicData.CODEC)
                    .networkSynchronized(ThreeLogicData.STREAM_CODEC));

    public static final Supplier<AttachmentType<TimerData>> TIMER_DATA = ATTACHMENT_TYPES.register(
            "timer_data", () -> AttachmentType.builder(TimerData::createDefault)
                    .serialize(TimerData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TimerData>> ITEM_TIMER_DATA = COMPONENTS.registerComponentType(
            "timer_data",
            builder -> builder
                    .persistent(TimerData.CODEC)
                    .networkSynchronized(TimerData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneInformationData>> ITEM_REDSTONE_INFORMATION_DATA = COMPONENTS.registerComponentType(
            "redstone_information_data",
            builder -> builder
                    .persistent(RedstoneInformationData.CODEC)
                    .networkSynchronized(RedstoneInformationData.STREAM_CODEC));

    public LogicBlockModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiAnalog.register(event);
        GuiCounter.register(event);
        GuiInvChecker.register(event);
        GuiSensor.register(event);
        GuiSequencer.register(event);
        GuiThreeLogic.register(event);
        GuiTimer.register(event);
        GuiRedstoneReceiver.register(event);
        GuiRedstoneTransmitter.register(event);
        GuiRedstoneInformation.register(event);
        GuiTabletScreen.register(event);
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DigitRenderer.register();
    }


    @Override
    public void initConfig(IEventBus bus) {

    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(ANALOG)
                        .ironPickaxeTags()
                        .parentedItem("block/analog_0")
                        .standardLoot(ITEM_ANALOG_DATA.get())
                        .blockState(p -> p.logicSlabBlock(ANALOG.block().get(), "analog", p.modLoc("block/logic/machineanalogtop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rAC"),
                Dob.blockBuilder(COUNTER)
                        .ironPickaxeTags()
                        .parentedItem("block/counter_0")
                        .standardLoot(ITEM_COUNTER_DATA.get())
                        .blockState(p -> p.logicSlabBlock(COUNTER.block().get(), "counter", p.modLoc("block/logic/machinecountertop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.CLOCK)
                                        .define('g', Items.GOLD_NUGGET)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "gCg", "TAT", "rTr"),
                Dob.blockBuilder(DIGIT)
                        .ironPickaxeTags()
                        .parentedItem("block/digit_0")
                        .simpleLoot()
                        .blockState(p -> p.logicSlabBlock(DIGIT.block().get(), "digit", p.modLoc("block/logic/machineoutput")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('P', Tags.Items.GLASS_PANES)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "PPP", "rAr", "PPP"),
                Dob.blockBuilder(INVCHECKER)
                        .ironPickaxeTags()
                        .parentedItem("block/invchecker_0")
                        .standardLoot(ITEM_INVCHECKER_DATA.get())
                        .blockState(p -> p.logicSlabBlock(INVCHECKER.block().get(), "invchecker", p.modLoc("block/logic/machineinvchecker")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('P', Items.COMPARATOR)
                                        .define('C', Tags.Items.CHESTS)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " P ", "rAr", " C "),
                Dob.blockBuilder(SENSOR)
                        .ironPickaxeTags()
                        .parentedItem("block/sensor_0")
                        .standardLoot(ITEM_SENSOR_DATA.get())
                        .blockState(p -> p.logicSlabBlock(SENSOR.block().get(), "sensor", p.modLoc("block/logic/machinesensor")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .define('x', Tags.Items.GEMS_QUARTZ)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "xCx", "rAr", "xCx"),
                Dob.blockBuilder(SEQUENCER)
                        .ironPickaxeTags()
                        .parentedItem("block/sequencer_0")
                        .standardLoot(ITEM_SEQUENCER_DATA.get())
                        .blockState(p -> p.logicSlabBlock(SEQUENCER.block().get(), "sequencer", p.modLoc("block/logic/machinesequencertop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rTr", "TAT", "rTr"),
                Dob.blockBuilder(LOGIC)
                        .ironPickaxeTags()
                        .parentedItem("block/logic_0")
                        .standardLoot(ITEM_THREELOGIC_DATA.get())
                        .blockState(p -> p.logicSlabBlock(LOGIC.block().get(), "logic", p.modLoc("block/logic/machinelogictop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rCr", "CAC", "rCr"),
                Dob.blockBuilder(TIMER)
                        .ironPickaxeTags()
                        .parentedItem("block/timer_0")
                        .standardLoot(ITEM_TIMER_DATA.get())
                        .blockState(p -> p.logicSlabBlock(TIMER.block().get(), "timer", p.modLoc("block/logic/machinetimertop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.CLOCK)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rCr", "TAT", "rTr"),
                Dob.blockBuilder(WIRE)
                        .ironPickaxeTags()
                        .parentedItem("block/wire_0")
                        .simpleLoot()
                        .blockState(p -> p.logicSlabBlock(WIRE.block().get(), "wire", p.modLoc("block/logic/machinewiretop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rAr"),
                Dob.blockBuilder(REDSTONE_RECEIVER)
                        .ironPickaxeTags()
                        .parentedItem("block/redstone_receiver_0")
                        .standardLoot(ITEM_REDSTONECHANNEL_DATA.get(), ITEM_REDSTONERECEIVER_DATA.get())
                        .blockState(p -> p.logicSlabBlock(REDSTONE_RECEIVER.block().get(), "redstone_receiver", p.modLoc("block/logic/machineredstonereceiver")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "ror", "CAC", "rRr"),
                Dob.blockBuilder(REDSTONE_TRANSMITTER)
                        .ironPickaxeTags()
                        .parentedItem("block/redstone_transmitter_0")
                        .standardLoot(ITEM_REDSTONECHANNEL_DATA.get())
                        .blockState(p -> p.logicSlabBlock(REDSTONE_TRANSMITTER.block().get(), "redstone_transmitter", p.modLoc("block/logic/machineredstonetransmitter")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "ror", "TAT", "rRr"),
                Dob.itemBuilder(REDSTONE_INFORMATION)
                        .shaped(builder -> builder
                                        .unlockedBy("redstone", has(Items.REDSTONE)),
                                "ror", "rRr", "rrr")
        );
    }
}
