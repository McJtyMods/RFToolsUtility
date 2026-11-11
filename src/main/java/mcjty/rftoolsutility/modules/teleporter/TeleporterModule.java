package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.AttachmentTypes;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.*;
import mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer;
import mcjty.rftoolsutility.modules.teleporter.client.GuiDialingDevice;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterReceiver;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterTransmitter;
import mcjty.rftoolsutility.modules.teleporter.data.*;
import mcjty.rftoolsutility.modules.teleporter.items.porter.AdvancedChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.porter.ChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.teleportprobe.TeleportProbeItem;
import mcjty.rftoolsutility.setup.Config;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class TeleporterModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, MatterTransmitterTileEntity> MATTER_TRANSMITTER = RBLOCKS.registerBlock("matter_transmitter",
            MatterTransmitterTileEntity.class,
            MatterTransmitterBlock::new,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            MatterTransmitterTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_TRANSMITTER = CONTAINERS.register("matter_transmitter", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, MatterReceiverTileEntity> MATTER_RECEIVER = RBLOCKS.registerBlock("matter_receiver",
            MatterReceiverTileEntity.class,
            MatterReceiverBlock::new,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            MatterReceiverTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_RECEIVER = CONTAINERS.register("matter_receiver", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, DialingDeviceTileEntity> DIALING_DEVICE = RBLOCKS.registerBlock("dialing_device",
            DialingDeviceTileEntity.class,
            DialingDeviceBlock::new,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            DialingDeviceTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIALING_DEVICE = CONTAINERS.register("dialing_device", GenericContainer::createContainerType);

    public static final DeferredBlock<DestinationAnalyzerBlock> DESTINATION_ANALYZER = BLOCKS.register("destination_analyzer", DestinationAnalyzerBlock::new);
    public static final DeferredItem<Item> DESTINATION_ANALYZER_ITEM = ITEMS.register("destination_analyzer", tab(properties -> new BaseBlockItem(DESTINATION_ANALYZER.get(), properties.useBlockDescriptionPrefix())));

    public static final DeferredBlock<MatterBoosterBlock> MATTER_BOOSTER = BLOCKS.register("matter_booster", MatterBoosterBlock::new);
    public static final DeferredItem<Item> MATTER_BOOSTER_ITEM = ITEMS.register("matter_booster", tab(properties -> new BaseBlockItem(MATTER_BOOSTER.get(), properties.useBlockDescriptionPrefix())));

    public static final RBlock<LogicSlabBlock, BlockItem, SimpleDialerTileEntity> SIMPLE_DIALER = RBLOCKS.registerBlock("simple_dialer",
            SimpleDialerTileEntity.class,
            SimpleDialerBlock::new,
            (block, properties) -> new SimpleDialerItemBlock(block.get(), properties),
            SimpleDialerTileEntity::new
    );

    public static final DeferredItem<TeleportProbeItem> TELEPORT_PROBE = ITEMS.register("teleport_probe", tab(TeleportProbeItem::new));
    public static final DeferredItem<ChargedPorterItem> CHARGED_PORTER = ITEMS.register("charged_porter", tab(ChargedPorterItem::new));
    public static final DeferredItem<AdvancedChargedPorterItem> ADVANCED_CHARGED_PORTER = ITEMS.register("advanced_charged_porter", tab(AdvancedChargedPorterItem::new));

    public static final AttachmentType<DialingDeviceData> DIALINGDEVICE_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "dialingdevice_data"),
            builder -> builder.initializer(DialingDeviceData::createDefault).persistent(DialingDeviceData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DialingDeviceData>> ITEM_DIALINGDEVICE_DATA = COMPONENTS.registerComponentType(
            "dialingdevice_data",
            builder -> builder
                    .persistent(DialingDeviceData.CODEC)
                    .networkSynchronized(DialingDeviceData.STREAM_CODEC));

    public static final AttachmentType<MatterReceiverData> MATTERRECEIVER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "matterreceiver_data"),
            builder -> builder.initializer(() -> MatterReceiverData.DEFAULT).persistent(MatterReceiverData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MatterReceiverData>> ITEM_MATTERRECEIVER_DATA = COMPONENTS.registerComponentType(
            "matterreceiver_data",
            builder -> builder
                    .persistent(MatterReceiverData.CODEC)
                    .networkSynchronized(MatterReceiverData.STREAM_CODEC));

    public static final AttachmentType<MatterTransmitterData> MATTERTRANSMITTER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "mattertransmitter_data"),
            builder -> builder.initializer(MatterTransmitterData::createDefault).persistent(MatterTransmitterData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MatterTransmitterData>> ITEM_MATTERTRANSMITTER_DATA = COMPONENTS.registerComponentType(
            "mattertransmitter_data",
            builder -> builder
                    .persistent(MatterTransmitterData.CODEC)
                    .networkSynchronized(MatterTransmitterData.STREAM_CODEC));

    public static final AttachmentType<SimpleDialerData> SIMPLEDIALER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "simpledialer_data"),
            builder -> builder.initializer(SimpleDialerData::createDefault).persistent(SimpleDialerData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleDialerData>> ITEM_SIMPLEDIALER_DATA = COMPONENTS.registerComponentType(
            "simpledialer_data",
            builder -> builder
                    .persistent(SimpleDialerData.CODEC)
                    .networkSynchronized(SimpleDialerData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ChargedPorterData>> ITEM_CHARGEDPORTER_DATA = COMPONENTS.registerComponentType(
            "chargedporter_data",
            builder -> builder
                    .persistent(ChargedPorterData.CODEC)
                    .networkSynchronized(ChargedPorterData.STREAM_CODEC));

    @Override
    public void init() {

    }

    public void registerMenuScreens() {
        GuiDialingDevice.register();
        GuiMatterTransmitter.register();
        GuiMatterReceiver.register();
    }

    @Override
    public void initClient() {
        registerMenuScreens();
        ClientCommandHandler.registerCommands();

        BeamRenderer.register();
    }

    @Override
    public void initConfig() {
        TeleportConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider lookup) {
        dataGen.add(
                Dob.blockBuilder(DESTINATION_ANALYZER)
                        .ironPickaxeTags()
                        .parentedItem("block/destination_analyzer")
                        .simpleLoot()
                        .blockState(p -> p.orientedBlock(DESTINATION_ANALYZER.get(), p.frontBasedModel("destination_analyzer", p.modLoc("block/machinedestinationanalyzer"))))
                        .shaped(builder -> builder
                                        .define('q', Items.QUARTZ)
                                        .define('C', Items.COMPARATOR)
                                        .define('f', Items.REPEATER)
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "ror", "CFf", "qrq"),
                Dob.blockBuilder(DIALING_DEVICE)
                        .ironPickaxeTags()
                        .parentedItem("block/dialing_device")
                        .standardLoot(ITEM_DIALINGDEVICE_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(DIALING_DEVICE.block().get(), p.frontBasedModel("dialing_device", p.modLoc("block/machinedialingdevice"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "rrr", "TFT", "rrr"),
                Dob.blockBuilder(MATTER_BOOSTER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_booster")
                        .simpleLoot()
                        .blockState(p -> p.orientedBlock(MATTER_BOOSTER.get(), p.frontBasedModel("matter_booster", p.modLoc("block/machinematterbooster"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                " R ", "RFR", " R "),
                Dob.blockBuilder(MATTER_RECEIVER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_receiver")
                        .standardLoot(ITEM_MATTERRECEIVER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.simpleBlock(MATTER_RECEIVER.block().get(), p.topBasedModel("matter_receiver", p.modLoc("block/machinereceiver"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "iii", "rFr", "ooo"),
                Dob.blockBuilder(MATTER_TRANSMITTER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_transmitter")
                        .standardLoot(ITEM_MATTERTRANSMITTER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.simpleBlock(MATTER_TRANSMITTER.block().get(), p.topBasedModel("matter_transmitter", p.modLoc("block/machinetransmitter"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "ooo", "rFr", "iii"),
                Dob.blockBuilder(SIMPLE_DIALER)
                        .ironPickaxeTags()
                        .parentedItem("block/simple_dialer_0")
                        .standardLoot(ITEM_SIMPLEDIALER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.logicSlabBlock(SIMPLE_DIALER.block().get(), "simple_dialer", p.modLoc("block/machinesimpledialer")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                "rRr", "TAT", "rRr"),
                Dob.itemBuilder(CHARGED_PORTER)
                        .shaped(builder -> builder
                                        .unlockedBy("pearl", has(Items.ENDER_PEARL)),
                                " o ", "oRo", "ioi"),
                Dob.itemBuilder(ADVANCED_CHARGED_PORTER)
                        .shapedComponentPreserve(builder -> builder
                                        .define('M', CHARGED_PORTER.get())
                                        .unlockedBy("porter", has(CHARGED_PORTER.get())),
                                "RdR", "dMd", "RdR")
        );
    }

    static {
        AttachmentTypes.remember(DIALINGDEVICE_DATA);
        AttachmentTypes.remember(MATTERRECEIVER_DATA);
        AttachmentTypes.remember(MATTERTRANSMITTER_DATA);
        AttachmentTypes.remember(SIMPLEDIALER_DATA);
    }

}
