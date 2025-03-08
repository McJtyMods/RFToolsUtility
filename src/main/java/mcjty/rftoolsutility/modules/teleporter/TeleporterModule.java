package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.Registration;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class TeleporterModule implements IModule {

    public static final DeferredBlock<MatterTransmitterBlock> MATTER_TRANSMITTER = BLOCKS.register("matter_transmitter", MatterTransmitterBlock::new);
    public static final DeferredItem<Item> MATTER_TRANSMITTER_ITEM = ITEMS.register("matter_transmitter", tab(() -> new BlockItem(MATTER_TRANSMITTER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<MatterTransmitterTileEntity>> TYPE_MATTER_TRANSMITTER = TILES.register("matter_transmitter", () -> BlockEntityType.Builder.of(MatterTransmitterTileEntity::new, MATTER_TRANSMITTER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_TRANSMITTER = CONTAINERS.register("matter_transmitter", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> MATTER_RECEIVER = BLOCKS.register("matter_receiver", MatterReceiverBlock::new);
    public static final DeferredItem<Item> MATTER_RECEIVER_ITEM = ITEMS.register("matter_receiver", tab(() -> new BlockItem(MATTER_RECEIVER.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_MATTER_RECEIVER = TILES.register("matter_receiver", () -> BlockEntityType.Builder.of(MatterReceiverTileEntity::new, MATTER_RECEIVER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_RECEIVER = CONTAINERS.register("matter_receiver", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> DIALING_DEVICE = BLOCKS.register("dialing_device", DialingDeviceBlock::new);
    public static final DeferredItem<Item> DIALING_DEVICE_ITEM = ITEMS.register("dialing_device", tab(() -> new BlockItem(DIALING_DEVICE.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_DIALING_DEVICE = TILES.register("dialing_device", () -> BlockEntityType.Builder.of(DialingDeviceTileEntity::new, DIALING_DEVICE.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIALING_DEVICE = CONTAINERS.register("dialing_device", GenericContainer::createContainerType);

    public static final DeferredBlock<DestinationAnalyzerBlock> DESTINATION_ANALYZER = BLOCKS.register("destination_analyzer", DestinationAnalyzerBlock::new);
    public static final DeferredItem<Item> DESTINATION_ANALYZER_ITEM = ITEMS.register("destination_analyzer", tab(() -> new BlockItem(DESTINATION_ANALYZER.get(), createStandardProperties())));

    public static final DeferredBlock<MatterBoosterBlock> MATTER_BOOSTER = BLOCKS.register("matter_booster", MatterBoosterBlock::new);
    public static final DeferredItem<Item> MATTER_BOOSTER_ITEM = ITEMS.register("matter_booster", tab(() -> new BlockItem(MATTER_BOOSTER.get(), createStandardProperties())));

    public static final DeferredBlock<SimpleDialerBlock> SIMPLE_DIALER = BLOCKS.register("simple_dialer", SimpleDialerBlock::new);
    public static final DeferredItem<Item> SIMPLE_DIALER_ITEM = ITEMS.register("simple_dialer", () -> new SimpleDialerItemBlock(SIMPLE_DIALER.get()));
    public static final Supplier<BlockEntityType<?>> TYPE_SIMPLE_DIALER = TILES.register("simple_dialer", () -> BlockEntityType.Builder.of(SimpleDialerTileEntity::new, SIMPLE_DIALER.get()).build(null));

    public static final DeferredItem<TeleportProbeItem> TELEPORT_PROBE = ITEMS.register("teleport_probe", tab(TeleportProbeItem::new));
    public static final DeferredItem<ChargedPorterItem> CHARGED_PORTER = ITEMS.register("charged_porter", tab(ChargedPorterItem::new));
    public static final DeferredItem<AdvancedChargedPorterItem> ADVANCED_CHARGED_PORTER = ITEMS.register("advanced_charged_porter", tab(AdvancedChargedPorterItem::new));

    public static final Supplier<AttachmentType<DialingDeviceData>> DIALINGDEVICE_DATA = ATTACHMENT_TYPES.register(
            "dialingdevice_data", () -> AttachmentType.builder(DialingDeviceData::createDefault)
                    .serialize(DialingDeviceData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DialingDeviceData>> ITEM_DIALINGDEVICE_DATA = COMPONENTS.registerComponentType(
            "dialingdevice_data",
            builder -> builder
                    .persistent(DialingDeviceData.CODEC)
                    .networkSynchronized(DialingDeviceData.STREAM_CODEC));

    public static final Supplier<AttachmentType<MatterReceiverData>> MATTERRECEIVER_DATA = ATTACHMENT_TYPES.register(
            "matterreceiver_data", () -> AttachmentType.builder(MatterReceiverData::createDefault)
                    .serialize(MatterReceiverData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MatterReceiverData>> ITEM_MATTERRECEIVER_DATA = COMPONENTS.registerComponentType(
            "matterreceiver_data",
            builder -> builder
                    .persistent(MatterReceiverData.CODEC)
                    .networkSynchronized(MatterReceiverData.STREAM_CODEC));

    public static final Supplier<AttachmentType<MatterTransmitterData>> MATTERTRANSMITTER_DATA = ATTACHMENT_TYPES.register(
            "mattertransmitter_data", () -> AttachmentType.builder(MatterTransmitterData::createDefault)
                    .serialize(MatterTransmitterData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MatterTransmitterData>> ITEM_MATTERTRANSMITTER_DATA = COMPONENTS.registerComponentType(
            "mattertransmitter_data",
            builder -> builder
                    .persistent(MatterTransmitterData.CODEC)
                    .networkSynchronized(MatterTransmitterData.STREAM_CODEC));

    public static final Supplier<AttachmentType<SimpleDialerData>> SIMPLEDIALER_DATA = ATTACHMENT_TYPES.register(
            "simpledialer_data", () -> AttachmentType.builder(SimpleDialerData::createDefault)
                    .serialize(SimpleDialerData.CODEC)
                    .build());
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

    public TeleporterModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiDialingDevice.register(event);
        GuiMatterTransmitter.register(event);
        GuiMatterReceiver.register(event);
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientCommandHandler.registerCommands();
            ChargedPorterItem.initOverrides(CHARGED_PORTER.get());
            ChargedPorterItem.initOverrides(ADVANCED_CHARGED_PORTER.get());
        });
        BeamRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
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
                        .standardLoot(ITEM_DIALINGDEVICE_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(DIALING_DEVICE.get(), p.frontBasedModel("dialing_device", p.modLoc("block/machinedialingdevice"))))
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
                        .standardLoot(ITEM_MATTERRECEIVER_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.simpleBlock(MATTER_RECEIVER.get(), p.topBasedModel("matter_receiver", p.modLoc("block/machinereceiver"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "iii", "rFr", "ooo"),
                Dob.blockBuilder(MATTER_TRANSMITTER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_transmitter")
                        .standardLoot(ITEM_MATTERTRANSMITTER_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.simpleBlock(MATTER_TRANSMITTER.get(), p.topBasedModel("matter_transmitter", p.modLoc("block/machinetransmitter"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "ooo", "rFr", "iii"),
                Dob.blockBuilder(SIMPLE_DIALER)
                        .ironPickaxeTags()
                        .parentedItem("block/simple_dialer_0")
                        .standardLoot(ITEM_SIMPLEDIALER_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.logicSlabBlock(SIMPLE_DIALER.get(), "simple_dialer", p.modLoc("block/machinesimpledialer")))
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
}
