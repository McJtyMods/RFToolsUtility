package mcjty.rftoolsutility.modules.logic;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.client.*;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationContainer;
import mcjty.rftoolsutility.modules.logic.items.RedstoneInformationItem;
import mcjty.rftoolsutility.modules.screen.client.GuiTabletScreen;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class LogicBlockModule implements IModule {

    public static final DeferredBlock<LogicSlabBlock> ANALOG = BLOCKS.register("analog", AnalogTileEntity::createBlock);
    public static final DeferredItem<Item> ANALOG_ITEM = ITEMS.register("analog", tab(() -> new BlockItem(ANALOG.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_ANALOG = TILES.register("analog", () -> BlockEntityType.Builder.of(AnalogTileEntity::new, ANALOG.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ANALOG = CONTAINERS.register("analog", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> COUNTER = BLOCKS.register("counter", CounterTileEntity::createBlock);
    public static final DeferredItem<Item> COUNTER_ITEM = ITEMS.register("counter", tab(() -> new BlockItem(COUNTER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_COUNTER = TILES.register("counter", () -> BlockEntityType.Builder.of(CounterTileEntity::new, COUNTER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_COUNTER = CONTAINERS.register("counter", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> DIGIT = BLOCKS.register("digit", DigitTileEntity::createBlock);
    public static final DeferredItem<Item> DIGIT_ITEM = ITEMS.register("digit", tab(() -> new BlockItem(DIGIT.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<DigitTileEntity>> TYPE_DIGIT = TILES.register("digit", () -> BlockEntityType.Builder.of(DigitTileEntity::new, DIGIT.get()).build(null));

    public static final DeferredBlock<LogicSlabBlock> INVCHECKER = BLOCKS.register("invchecker", InvCheckerTileEntity::createBlock);
    public static final DeferredItem<Item> INVCHECKER_ITEM = ITEMS.register("invchecker", tab(() -> new BlockItem(INVCHECKER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_INVCHECKER = TILES.register("invchecker", () -> BlockEntityType.Builder.of(InvCheckerTileEntity::new, INVCHECKER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_INVCHECKER = CONTAINERS.register("invchecker", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> SENSOR = BLOCKS.register("sensor", SensorTileEntity::createBlock);
    public static final DeferredItem<Item> SENSOR_ITEM = ITEMS.register("sensor", tab(() -> new BlockItem(SENSOR.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<SensorTileEntity>> TYPE_SENSOR = TILES.register("sensor", () -> BlockEntityType.Builder.of(SensorTileEntity::new, SENSOR.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SENSOR = CONTAINERS.register("sensor", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> SEQUENCER = BLOCKS.register("sequencer", SequencerTileEntity::createBlock);
    public static final DeferredItem<Item> SEQUENCER_ITEM = ITEMS.register("sequencer", tab(() -> new BlockItem(SEQUENCER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_SEQUENCER = TILES.register("sequencer", () -> BlockEntityType.Builder.of(SequencerTileEntity::new, SEQUENCER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SEQUENCER = CONTAINERS.register("sequencer", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> LOGIC = BLOCKS.register("logic", ThreeLogicTileEntity::createBlock);
    public static final DeferredItem<Item> LOGIC_ITEM = ITEMS.register("logic", tab(() -> new BlockItem(LOGIC.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_LOGIC = TILES.register("logic", () -> BlockEntityType.Builder.of(ThreeLogicTileEntity::new, LOGIC.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_LOGIC = CONTAINERS.register("logic", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> TIMER = BLOCKS.register("timer", TimerTileEntity::createBlock);
    public static final DeferredItem<Item> TIMER_ITEM = ITEMS.register("timer", tab(() -> new BlockItem(TIMER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_TIMER = TILES.register("timer", () -> BlockEntityType.Builder.of(TimerTileEntity::new, TIMER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_TIMER = CONTAINERS.register("timer", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> WIRE = BLOCKS.register("wire", WireTileEntity::createBlock);
    public static final DeferredItem<Item> WIRE_ITEM = ITEMS.register("wire", tab(() -> new BlockItem(WIRE.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_WIRE = TILES.register("wire", () -> BlockEntityType.Builder.of(WireTileEntity::new, WIRE.get()).build(null));

    public static final DeferredBlock<LogicSlabBlock> REDSTONE_RECEIVER = BLOCKS.register("redstone_receiver", RedstoneReceiverTileEntity::createBlock);
    public static final DeferredItem<Item> REDSTONE_RECEIVER_ITEM = ITEMS.register("redstone_receiver", tab(() -> new BlockItem(REDSTONE_RECEIVER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_REDSTONE_RECEIVER = TILES.register("redstone_receiver", () -> BlockEntityType.Builder.of(RedstoneReceiverTileEntity::new, REDSTONE_RECEIVER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_REDSTONE_RECEIVER = CONTAINERS.register("redstone_receiver", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> REDSTONE_TRANSMITTER = BLOCKS.register("redstone_transmitter", RedstoneTransmitterBlock::new);
    public static final DeferredItem<Item> REDSTONE_TRANSMITTER_ITEM = ITEMS.register("redstone_transmitter", tab(() -> new BlockItem(REDSTONE_TRANSMITTER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_REDSTONE_TRANSMITTER = TILES.register("redstone_transmitter", () -> BlockEntityType.Builder.of(RedstoneTransmitterTileEntity::new, REDSTONE_TRANSMITTER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_REDSTONE_TRANSMITTER = CONTAINERS.register("redstone_transmitter", GenericContainer::createContainerType);

    public static final DeferredItem<RedstoneInformationItem> REDSTONE_INFORMATION = ITEMS.register("redstone_information", tab(RedstoneInformationItem::new));
    public static final Supplier<MenuType<RedstoneInformationContainer>> CONTAINER_REDSTONE_INFORMATION = CONTAINERS.register("redstone_information",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new RedstoneInformationContainer(windowId, null, SafeClientTools.getClientPlayer())));
    public static final DeferredItem<TabletItem> TABLET_REDSTONE = ITEMS.register("tablet_redstone", tab(TabletItem::new));

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
    public void initConfig(IEventBus bus) {

    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(ANALOG)
                        .ironPickaxeTags()
                        .parentedItem("block/analog_0")
                        .standardLoot(TYPE_ANALOG)
                        .blockState(p -> p.logicSlabBlock(ANALOG.get(), "analog", p.modLoc("block/logic/machineanalogtop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rAC"),
                Dob.blockBuilder(COUNTER)
                        .ironPickaxeTags()
                        .parentedItem("block/counter_0")
                        .standardLoot(TYPE_COUNTER)
                        .blockState(p -> p.logicSlabBlock(COUNTER.get(), "counter", p.modLoc("block/logic/machinecountertop")))
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
                        .blockState(p -> p.logicSlabBlock(DIGIT.get(), "digit", p.modLoc("block/logic/machineoutput")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('P', Tags.Items.GLASS_PANES)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "PPP", "rAr", "PPP"),
                Dob.blockBuilder(INVCHECKER)
                        .ironPickaxeTags()
                        .parentedItem("block/invchecker_0")
                        .standardLoot(TYPE_INVCHECKER)
                        .blockState(p -> p.logicSlabBlock(INVCHECKER.get(), "invchecker", p.modLoc("block/logic/machineinvchecker")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('P', Items.COMPARATOR)
                                        .define('C', Tags.Items.CHESTS)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " P ", "rAr", " C "),
                Dob.blockBuilder(SENSOR)
                        .ironPickaxeTags()
                        .parentedItem("block/sensor_0")
                        .standardLoot(TYPE_SENSOR)
                        .blockState(p -> p.logicSlabBlock(SENSOR.get(), "sensor", p.modLoc("block/logic/machinesensor")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .define('x', Tags.Items.GEMS_QUARTZ)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "xCx", "rAr", "xCx"),
                Dob.blockBuilder(SEQUENCER)
                        .ironPickaxeTags()
                        .parentedItem("block/sequencer_0")
                        .standardLoot(TYPE_SEQUENCER)
                        .blockState(p -> p.logicSlabBlock(SEQUENCER.get(), "sequencer", p.modLoc("block/logic/machinesequencertop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rTr", "TAT", "rTr"),
                Dob.blockBuilder(LOGIC)
                        .ironPickaxeTags()
                        .parentedItem("block/logic_0")
                        .standardLoot(TYPE_LOGIC)
                        .blockState(p -> p.logicSlabBlock(LOGIC.get(), "logic", p.modLoc("block/logic/machinelogictop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rCr", "CAC", "rCr"),
                Dob.blockBuilder(TIMER)
                        .ironPickaxeTags()
                        .parentedItem("block/timer_0")
                        .standardLoot(TYPE_TIMER)
                        .blockState(p -> p.logicSlabBlock(TIMER.get(), "timer", p.modLoc("block/logic/machinetimertop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.CLOCK)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rCr", "TAT", "rTr"),
                Dob.blockBuilder(WIRE)
                        .ironPickaxeTags()
                        .parentedItem("block/wire_0")
                        .simpleLoot()
                        .blockState(p -> p.logicSlabBlock(WIRE.get(), "wire", p.modLoc("block/logic/machinewiretop")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rAr"),
                Dob.blockBuilder(REDSTONE_RECEIVER)
                        .ironPickaxeTags()
                        .parentedItem("block/redstone_receiver_0")
                        .standardLoot(TYPE_REDSTONE_RECEIVER)
                        .blockState(p -> p.logicSlabBlock(REDSTONE_RECEIVER.get(), "redstone_receiver", p.modLoc("block/logic/machineredstonereceiver")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .define('C', Items.COMPARATOR)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "ror", "CAC", "rRr"),
                Dob.blockBuilder(REDSTONE_TRANSMITTER)
                        .ironPickaxeTags()
                        .parentedItem("block/redstone_transmitter_0")
                        .standardLoot(TYPE_REDSTONE_TRANSMITTER)
                        .blockState(p -> p.logicSlabBlock(REDSTONE_TRANSMITTER.get(), "redstone_transmitter", p.modLoc("block/logic/machineredstonetransmitter")))
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
