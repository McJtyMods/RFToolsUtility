package mcjty.rftoolsutility.setup;


import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsUtility.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//        event.getRegistry().register(new InformationScreenBlock());
//        event.getRegistry().register(new PowerCellBlock(Tier.TIER1));
//        event.getRegistry().register(new PowerCellBlock(Tier.TIER2));
//        event.getRegistry().register(new PowerCellBlock(Tier.TIER3));
//        event.getRegistry().register(new Block(Block.Properties.create(Material.IRON)).setRegistryName("celltextures"));
//        if (CoalGeneratorConfig.ENABLED.get()) {
//            event.getRegistry().register(CoalGeneratorTileEntity.createBlock());
//        }
        CrafterSetup.registerBlocks(event);
        TeleporterSetup.registerBlocks(event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
//        Item.Properties properties = new Item.Properties().group(RFToolsUtility.setup.getTab());
//        event.getRegistry().register(new BaseBlockItem(ModBlocks.INFORMATION_SCREEN, properties));
//        event.getRegistry().register(new BaseBlockItem(ModBlocks.CELL1, properties));
//        event.getRegistry().register(new BaseBlockItem(ModBlocks.CELL2, properties));
//        event.getRegistry().register(new BaseBlockItem(ModBlocks.CELL3, properties));
//
//        event.getRegistry().register(new PowerCoreItem("1"));
//        event.getRegistry().register(new PowerCoreItem("2"));
//        event.getRegistry().register(new PowerCoreItem("3"));
//
//        if (CoalGeneratorConfig.ENABLED.get()) {
//            event.getRegistry().register(new BaseBlockItem(ModBlocks.COALGENERATOR, properties));
//        }
        CrafterSetup.registerItems(event);
        TeleporterSetup.registerItems(event);
    }

    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
//        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER1), ModBlocks.CELL1).build(null).setRegistryName(ModBlocks.CELL1.getRegistryName()));
//        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER2), ModBlocks.CELL2).build(null).setRegistryName(ModBlocks.CELL2.getRegistryName()));
//        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER3), ModBlocks.CELL3).build(null).setRegistryName(ModBlocks.CELL3.getRegistryName()));
//        event.getRegistry().register(TileEntityType.Builder.create(InformationScreenTileEntity::new, ModBlocks.INFORMATION_SCREEN).build(null).setRegistryName(ModBlocks.INFORMATION_SCREEN.getRegistryName()));
//        if (CoalGeneratorConfig.ENABLED.get()) {
//            event.getRegistry().register(TileEntityType.Builder.create(CoalGeneratorTileEntity::new, ModBlocks.COALGENERATOR).build(null).setRegistryName(ModBlocks.COALGENERATOR.getRegistryName()));
//        }
        CrafterSetup.registerTiles(event);
        TeleporterSetup.registerTiles(event);
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        CrafterSetup.registerContainers(event);
        TeleporterSetup.registerContainers(event);
    }

}
