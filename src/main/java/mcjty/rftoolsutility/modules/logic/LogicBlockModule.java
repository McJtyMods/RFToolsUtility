package mcjty.rftoolsutility.modules.logic;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.AttachmentTypes;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.client.*;
import mcjty.rftoolsutility.modules.logic.data.*;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.screen.client.GuiTabletScreen;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class LogicBlockModule implements IModule {

    public static final RBlock<LogicSlabBlock, BlockItem, AnalogTileEntity> ANALOG = RBLOCKS.registerBlock("analog",
            AnalogTileEntity.class,
            AnalogTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            AnalogTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ANALOG = CONTAINERS.register("analog", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, CounterTileEntity> COUNTER = RBLOCKS.registerBlock("counter",
            CounterTileEntity.class,
            CounterTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            CounterTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_COUNTER = CONTAINERS.register("counter", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, DigitTileEntity> DIGIT = RBLOCKS.registerBlock("digit",
            DigitTileEntity.class,
            DigitTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            DigitTileEntity::new);

    public static final RBlock<LogicSlabBlock, BlockItem, InvCheckerTileEntity> INVCHECKER = RBLOCKS.registerBlock("invchecker",
            InvCheckerTileEntity.class,
            InvCheckerTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            InvCheckerTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_INVCHECKER = CONTAINERS.register("invchecker", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, SensorTileEntity> SENSOR = RBLOCKS.registerBlock("sensor",
            SensorTileEntity.class,
            SensorTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            SensorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SENSOR = CONTAINERS.register("sensor", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, SequencerTileEntity> SEQUENCER = RBLOCKS.registerBlock("sequencer",
            SequencerTileEntity.class,
            SequencerTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            SequencerTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SEQUENCER = CONTAINERS.register("sequencer", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, ThreeLogicTileEntity> LOGIC = RBLOCKS.registerBlock("logic",
            ThreeLogicTileEntity.class,
            ThreeLogicTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            ThreeLogicTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_LOGIC = CONTAINERS.register("logic", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, TimerTileEntity> TIMER = RBLOCKS.registerBlock("timer",
            TimerTileEntity.class,
            TimerTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            TimerTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_TIMER = CONTAINERS.register("timer", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, WireTileEntity> WIRE = RBLOCKS.registerBlock("wire",
            WireTileEntity.class,
            WireTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            WireTileEntity::new);

    public static final RBlock<LogicSlabBlock, BlockItem, RedstoneReceiverTileEntity> REDSTONE_RECEIVER = RBLOCKS.registerBlock("redstone_receiver",
            RedstoneReceiverTileEntity.class,
            RedstoneReceiverTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            RedstoneReceiverTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_REDSTONE_RECEIVER = CONTAINERS.register("redstone_receiver", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, RedstoneTransmitterTileEntity> REDSTONE_TRANSMITTER = RBLOCKS.registerBlock("redstone_transmitter",
            RedstoneTransmitterTileEntity.class,
            RedstoneTransmitterBlock::new,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            RedstoneTransmitterTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_REDSTONE_TRANSMITTER = CONTAINERS.register("redstone_transmitter", GenericContainer::createContainerType);

    public static final DeferredItem<RedstoneInformationItem> REDSTONE_INFORMATION = ITEMS.register("redstone_information", tab(RedstoneInformationItem::new));
    public static final Supplier<MenuType<RedstoneInformationContainer>> CONTAINER_REDSTONE_INFORMATION = CONTAINERS.register("redstone_information",
            () -> new MenuType<>((windowId, inv) -> new RedstoneInformationContainer(windowId, inv.player.blockPosition(), inv.player), FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredItem<TabletItem> TABLET_REDSTONE = ITEMS.register("tablet_redstone", TabletItem::new);

    public static final AttachmentType<AnalogData> ANALOG_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "analog_data"),
            builder -> builder.initializer(AnalogData::createDefault).persistent(AnalogData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AnalogData>> ITEM_ANALOG_DATA = COMPONENTS.registerComponentType(
            "analog_data",
            builder -> builder
                    .persistent(AnalogData.CODEC)
                    .networkSynchronized(AnalogData.STREAM_CODEC));

    public static final AttachmentType<CounterData> COUNTER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "counter_data"),
            builder -> builder.initializer(CounterData::createDefault).persistent(CounterData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CounterData>> ITEM_COUNTER_DATA = COMPONENTS.registerComponentType(
            "counter_data",
            builder -> builder
                    .persistent(CounterData.CODEC)
                    .networkSynchronized(CounterData.STREAM_CODEC));

    public static final AttachmentType<IncCheckerData> INVCHECKER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "invchecker_data"),
            builder -> builder.initializer(IncCheckerData::createDefault).persistent(IncCheckerData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IncCheckerData>> ITEM_INVCHECKER_DATA = COMPONENTS.registerComponentType(
            "invchecker_data",
            builder -> builder
                    .persistent(IncCheckerData.CODEC)
                    .networkSynchronized(IncCheckerData.STREAM_CODEC));

    public static final AttachmentType<RedstoneChannelData> REDSTONECHANNEL_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "redstonechannel_data"),
            builder -> builder.initializer(RedstoneChannelData::createDefault).persistent(RedstoneChannelData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneChannelData>> ITEM_REDSTONECHANNEL_DATA = COMPONENTS.registerComponentType(
            "redstonechannel_data",
            builder -> builder
                    .persistent(RedstoneChannelData.CODEC)
                    .networkSynchronized(RedstoneChannelData.STREAM_CODEC));

    public static final AttachmentType<RedstoneReceiverData> REDSTONERECEIVER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "redstonereceiver_data"),
            builder -> builder.initializer(RedstoneReceiverData::createDefault).persistent(RedstoneReceiverData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RedstoneReceiverData>> ITEM_REDSTONERECEIVER_DATA = COMPONENTS.registerComponentType(
            "redstonereceiver_data",
            builder -> builder
                    .persistent(RedstoneReceiverData.CODEC)
                    .networkSynchronized(RedstoneReceiverData.STREAM_CODEC));

    public static final AttachmentType<SensorData> SENSOR_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "sensor_data"),
            builder -> builder.initializer(SensorData::createDefault).persistent(SensorData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SensorData>> ITEM_SENSOR_DATA = COMPONENTS.registerComponentType(
            "sensor_data",
            builder -> builder
                    .persistent(SensorData.CODEC)
                    .networkSynchronized(SensorData.STREAM_CODEC));

    public static final AttachmentType<SequencerData> SEQUENCER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "sequencer_data"),
            builder -> builder.initializer(SequencerData::createDefault).persistent(SequencerData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SequencerData>> ITEM_SEQUENCER_DATA = COMPONENTS.registerComponentType(
            "sequencer_data",
            builder -> builder
                    .persistent(SequencerData.CODEC)
                    .networkSynchronized(SequencerData.STREAM_CODEC));

    public static final AttachmentType<ThreeLogicData> THREELOGIC_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "threelogic_data"),
            builder -> builder.initializer(ThreeLogicData::createDefault).persistent(ThreeLogicData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ThreeLogicData>> ITEM_THREELOGIC_DATA = COMPONENTS.registerComponentType(
            "threelogic_data",
            builder -> builder
                    .persistent(ThreeLogicData.CODEC)
                    .networkSynchronized(ThreeLogicData.STREAM_CODEC));

    public static final AttachmentType<TimerData> TIMER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "timer_data"),
            builder -> builder.initializer(TimerData::createDefault).persistent(TimerData.CODEC));
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

    @Override
    public void init() {

    }

    public void registerMenuScreens() {
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
    }

    @Override
    public void initClient() {
        registerMenuScreens();
        DigitRenderer.register();
    }


    @Override
    public void initConfig() {

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
                                        .define('P', ConventionalItemTags.GLASS_PANES)
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
                                        .define('C', ConventionalItemTags.CHESTS)
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
                                        .define('x', ConventionalItemTags.QUARTZ_GEMS)
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

    static {
        AttachmentTypes.remember(ANALOG_DATA);
        AttachmentTypes.remember(COUNTER_DATA);
        AttachmentTypes.remember(INVCHECKER_DATA);
        AttachmentTypes.remember(REDSTONECHANNEL_DATA);
        AttachmentTypes.remember(REDSTONERECEIVER_DATA);
        AttachmentTypes.remember(SENSOR_DATA);
        AttachmentTypes.remember(SEQUENCER_DATA);
        AttachmentTypes.remember(THREELOGIC_DATA);
        AttachmentTypes.remember(TIMER_DATA);
    }

}
