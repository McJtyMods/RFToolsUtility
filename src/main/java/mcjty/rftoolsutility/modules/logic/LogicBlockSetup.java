package mcjty.rftoolsutility.modules.logic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockFlags;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.ItemStackTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.analog.AnalogTileEntity;
import mcjty.rftoolsutility.modules.logic.counter.CounterTileEntity;
import mcjty.rftoolsutility.modules.logic.digit.DigitTileEntity;
import mcjty.rftoolsutility.modules.logic.wireless.RedstoneReceiverBlock;
import mcjty.rftoolsutility.modules.logic.wireless.RedstoneTransmitterBlock;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class LogicBlockSetup {

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
    public static final RegistryObject<TileEntityType<?>> TYPE_DIGIT = TILES.register("digit", () -> TileEntityType.Builder.create(DigitTileEntity::new, DIGIT.get()).build(null));


    public static RedstoneTransmitterBlock redstoneTransmitterBlock;
    public static RedstoneReceiverBlock redstoneReceiverBlock;

    public static GenericBlock<SequencerTileEntity, GenericContainer> sequencerBlock;
    public static GenericBlock<CounterTileEntity, GenericContainer> counterBlock;
    public static GenericBlock<ThreeLogicTileEntity, GenericContainer> threeLogicBlock;
    public static GenericBlock<InvCheckerTileEntity, GenericContainer> invCheckerBlock;
    public static GenericBlock<SensorTileEntity, GenericContainer> sensorBlock;
    public static GenericBlock<AnalogTileEntity, GenericContainer> analogBlock;
    public static GenericBlock<DigitTileEntity, GenericContainer> digitBlock;
    public static GenericBlock<WireTileEntity, GenericContainer> wireBlock;
    public static GenericBlock<TimerTileEntity, GenericContainer> timerBlock;

    public static void init() {
        redstoneTransmitterBlock = new RedstoneTransmitterBlock();
        redstoneReceiverBlock = new RedstoneReceiverBlock();

        sequencerBlock = ModBlocks.logicFactory.<SequencerTileEntity> builder("sequencer_block")
                .tileEntityClass(SequencerTileEntity.class)
                .guiId(GuiProxy.GUI_SEQUENCER)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.sequencer")
                .infoExtendedParameter(ItemStackTools.intGetter("delay", 0))
                .infoExtendedParameter(stack -> ItemStackTools.mapTag(stack, compound -> SequencerMode.values()[compound.getInteger("mode")].getDescription(), "<none>"))
                .infoExtendedParameter(stack -> ItemStackTools.mapTag(stack, compound -> Long.toHexString(compound.getLong("bits")), "<unset>"))
                .build();
        counterBlock = ModBlocks.logicFactory.<CounterTileEntity> builder("counter_block")
                .tileEntityClass(CounterTileEntity.class)
                .guiId(GuiProxy.GUI_COUNTER)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.counter")
                .infoExtendedParameter(ItemStackTools.intGetter("counter", 0))
                .infoExtendedParameter(ItemStackTools.intGetter("current", 0))
                .build();
        threeLogicBlock = ModBlocks.logicFactory.<ThreeLogicTileEntity> builder("logic_block")
                .tileEntityClass(ThreeLogicTileEntity.class)
                .guiId(GuiProxy.GUI_THREE_LOGIC)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.threelogic")
                .build();
        invCheckerBlock = ModBlocks.logicFactory.<InvCheckerTileEntity> builder("invchecker_block")
                .tileEntityClass(InvCheckerTileEntity.class)
                .guiId(GuiProxy.GUI_INVCHECKER)
                .container(InvCheckerTileEntity.CONTAINER_FACTORY)
                .flags(BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.invchecker")
                .build();
        sensorBlock = ModBlocks.logicFactory.<SensorTileEntity> builder("sensor_block")
                .tileEntityClass(SensorTileEntity.class)
                .guiId(GuiProxy.GUI_SENSOR)
                .container(SensorTileEntity.CONTAINER_FACTORY)
                .flags(BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.sensor")
                .build();
        analogBlock = ModBlocks.logicFactory.<AnalogTileEntity> builder("analog_block")
                .tileEntityClass(AnalogTileEntity.class)
                .guiId(GuiProxy.GUI_ANALOG)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.analog")
                .build();
        digitBlock = ModBlocks.logicFactory.<DigitTileEntity> builder("digit_block")
                .tileEntityClass(DigitTileEntity.class)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE,
                        BlockFlags.RENDER_CUTOUT, BlockFlags.RENDER_SOLID)
                .property(DigitTileEntity.VALUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.digit")
                .build();
        wireBlock = ModBlocks.logicFactory.<WireTileEntity> builder("wire_block")
                .tileEntityClass(WireTileEntity.class)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.wire")
                .build();
        timerBlock = ModBlocks.logicFactory.<TimerTileEntity> builder("timer_block")
                .tileEntityClass(TimerTileEntity.class)
                .guiId(GuiProxy.GUI_TIMER)
                .emptyContainer()
                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.REDSTONE_OUTPUT, BlockFlags.NON_OPAQUE)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.timer")
                .infoExtendedParameter(ItemStackTools.intGetter("delay", 0))
                .build();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        sequencerBlock.initModel();
        sequencerBlock.setGuiFactory(GuiSequencer::new);

        timerBlock.initModel();
        timerBlock.setGuiFactory(GuiTimer::new);

        counterBlock.initModel();
        counterBlock.setGuiFactory(GuiCounter::new);

        redstoneTransmitterBlock.initModel();
        redstoneReceiverBlock.initModel();

        threeLogicBlock.initModel();
        threeLogicBlock.setGuiFactory(GuiThreeLogic::new);

        invCheckerBlock.initModel();
        invCheckerBlock.setGuiFactory(GuiInvChecker::new);

        sensorBlock.initModel();
        sensorBlock.setGuiFactory(GuiSensor::new);

        wireBlock.initModel();

        analogBlock.initModel();
        analogBlock.setGuiFactory(GuiAnalog::new);

        digitBlock.initModel();
    }
}
