package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.modules.teleporter.blocks.*;
import mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer;
import mcjty.rftoolsutility.modules.teleporter.items.porter.AdvancedChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.porter.ChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.teleportprobe.TeleportProbeItem;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class TeleporterSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<MatterTransmitterBlock> MATTER_TRANSMITTER = BLOCKS.register("matter_transmitter", MatterTransmitterBlock::new);
    public static final RegistryObject<Item> MATTER_TRANSMITTER_ITEM = ITEMS.register("matter_transmitter", () -> new BlockItem(MATTER_TRANSMITTER.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<MatterTransmitterTileEntity>> TYPE_MATTER_TRANSMITTER = TILES.register("matter_transmitter", () -> TileEntityType.Builder.create(MatterTransmitterTileEntity::new, MATTER_TRANSMITTER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_MATTER_TRANSMITTER = CONTAINERS.register("matter_transmitter", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> MATTER_RECEIVER = BLOCKS.register("matter_receiver", MatterReceiverBlock::new);
    public static final RegistryObject<Item> MATTER_RECEIVER_ITEM = ITEMS.register("matter_receiver", () -> new BlockItem(MATTER_RECEIVER.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_MATTER_RECEIVER = TILES.register("matter_receiver", () -> TileEntityType.Builder.create(MatterReceiverTileEntity::new, MATTER_RECEIVER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_MATTER_RECEIVER = CONTAINERS.register("matter_receiver", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> DIALING_DEVICE = BLOCKS.register("dialing_device", DialingDeviceBlock::new);
    public static final RegistryObject<Item> DIALING_DEVICE_ITEM = ITEMS.register("dialing_device", () -> new BlockItem(DIALING_DEVICE.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_DIALING_DEVICE = TILES.register("dialing_device", () -> TileEntityType.Builder.create(DialingDeviceTileEntity::new, DIALING_DEVICE.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_DIALING_DEVICE = CONTAINERS.register("dialing_device", GenericContainer::createContainerType);

    public static final RegistryObject<DestinationAnalyzerBlock> DESTINATION_ANALYZER = BLOCKS.register("destination_analyzer", DestinationAnalyzerBlock::new);
    public static final RegistryObject<Item> DESTINATION_ANALYZER_ITEM = ITEMS.register("destination_analyzer", () -> new BlockItem(DESTINATION_ANALYZER.get(), createStandardProperties()));

    public static final RegistryObject<MatterBoosterBlock> MATTER_BOOSTER = BLOCKS.register("matter_booster", MatterBoosterBlock::new);
    public static final RegistryObject<Item> MATTER_BOOSTER_ITEM = ITEMS.register("matter_booster", () -> new BlockItem(MATTER_BOOSTER.get(), createStandardProperties()));

    public static final RegistryObject<SimpleDialerBlock> SIMPLE_DIALER = BLOCKS.register("simple_dialer", SimpleDialerBlock::new);
    public static final RegistryObject<Item> SIMPLE_DIALER_ITEM = ITEMS.register("simple_dialer", () -> new SimpleDialerItemBlock(SIMPLE_DIALER.get()));
    public static final RegistryObject<TileEntityType<?>> TYPE_SIMPLE_DIALER = TILES.register("simple_dialer", () -> TileEntityType.Builder.create(DialingDeviceTileEntity::new, SIMPLE_DIALER.get()).build(null));

    public static final RegistryObject<TeleportProbeItem> TELEPORT_PROBE = ITEMS.register("teleport_probe", TeleportProbeItem::new);
    public static final RegistryObject<ChargedPorterItem> CHARGED_PORTER = ITEMS.register("charged_porter", ChargedPorterItem::new);
    public static final RegistryObject<AdvancedChargedPorterItem> ADVANCED_CHARGED_PORTER = ITEMS.register("advanced_charged_porter", AdvancedChargedPorterItem::new);

    public static void initClient() {
        BeamRenderer.register();
    }
}
