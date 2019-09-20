package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.blocks.BaseBlockItem;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.blocks.*;
import mcjty.rftoolsutility.modules.teleporter.items.porter.AdvancedChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.porter.ChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.teleportprobe.TeleportProbeItem;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class TeleporterSetup {

    @ObjectHolder("rftoolsutility:matter_transmitter")
    public static MatterTransmitterBlock MATTER_TRANSMITTER;

    @ObjectHolder("rftoolsutility:matter_receiver")
    public static MatterReceiverBlock MATTER_RECEIVER;

    @ObjectHolder("rftoolsutility:dialing_device")
    public static DialingDeviceBlock DIALING_DEVICE;

    @ObjectHolder("rftoolsutility:destination_analyzer")
    public static DestinationAnalyzerBlock DESTINATION_ANALYZER;

    @ObjectHolder("rftoolsutility:matter_booster")
    public static MatterBoosterBlock MATTER_BOOSTER;

    @ObjectHolder("rftoolsutility:simple_dialer")
    public static SimpleDialerBlock SIMPLE_DIALER;

    @ObjectHolder("rftoolsutility:teleport_probe")
    public static TeleportProbeItem TELEPORT_PROBE;

    @ObjectHolder("rftoolsutility:charged_porter")
    public static ChargedPorterItem CHARGED_PORTER;

    @ObjectHolder("rftoolsutility:advanced_charged_porter")
    public static AdvancedChargedPorterItem ADVANCED_CHARGED_PORTER;

    @ObjectHolder("rftoolsutility:matter_receiver")
    public static TileEntityType<?> TYPE_MATTER_RECEIVER;

    @ObjectHolder("rftoolsutility:matter_transmitter")
    public static TileEntityType<?> TYPE_MATTER_TRANSMITTER;

    @ObjectHolder("rftoolsutility:simple_dialer")
    public static TileEntityType<?> TYPE_SIMPLE_DIALER;

    @ObjectHolder("rftoolsutility:dialing_device")
    public static TileEntityType<?> TYPE_DIALING_DEVICE;

    @ObjectHolder("rftoolsutility:dialing_device")
    public static ContainerType<GenericContainer> CONTAINER_DIALING_DEVICE;

    @ObjectHolder("rftoolsutility:matter_transmitter")
    public static ContainerType<GenericContainer> CONTAINER_MATTER_TRANSMITTER;

    @ObjectHolder("rftoolsutility:matter_receiver")
    public static ContainerType<GenericContainer> CONTAINER_MATTER_RECEIVER;

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsUtility.setup.getTab());
        event.getRegistry().register(new BaseBlockItem(MATTER_TRANSMITTER, properties));
        event.getRegistry().register(new BaseBlockItem(MATTER_RECEIVER, properties));
        event.getRegistry().register(new BaseBlockItem(DIALING_DEVICE, properties));
        event.getRegistry().register(new BaseBlockItem(SIMPLE_DIALER, properties));
        event.getRegistry().register(new BaseBlockItem(DESTINATION_ANALYZER, properties));
        event.getRegistry().register(new BaseBlockItem(MATTER_BOOSTER, properties));

        event.getRegistry().register(TELEPORT_PROBE = new TeleportProbeItem());
        event.getRegistry().register(CHARGED_PORTER = new ChargedPorterItem());
        event.getRegistry().register(ADVANCED_CHARGED_PORTER = new AdvancedChargedPorterItem());
    }

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new MatterTransmitterBlock());
        event.getRegistry().register(new MatterReceiverBlock());
        event.getRegistry().register(new DialingDeviceBlock());
        event.getRegistry().register(new SimpleDialerBlock());
        event.getRegistry().register(new DestinationAnalyzerBlock());
        event.getRegistry().register(new MatterBoosterBlock());
    }

    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(MatterTransmitterTileEntity::new, MATTER_TRANSMITTER).build(null).setRegistryName("matter_transmitter"));
        event.getRegistry().register(TileEntityType.Builder.create(MatterReceiverTileEntity::new, MATTER_RECEIVER).build(null).setRegistryName("matter_receiver"));
        event.getRegistry().register(TileEntityType.Builder.create(DialingDeviceTileEntity::new, DIALING_DEVICE).build(null).setRegistryName("dialing_device"));
        event.getRegistry().register(TileEntityType.Builder.create(SimpleDialerTileEntity::new, SIMPLE_DIALER).build(null).setRegistryName("simple_dialer"));
    }

    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(GenericContainer.createContainerType("dialing_device"));
        event.getRegistry().register(GenericContainer.createContainerType("matter_transmitter"));
        event.getRegistry().register(GenericContainer.createContainerType("matter_receiver"));
    }

    public static void initClient() {
        MATTER_TRANSMITTER.initModel();
    }
}
