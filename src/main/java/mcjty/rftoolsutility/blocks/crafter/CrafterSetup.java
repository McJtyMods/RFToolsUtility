package mcjty.rftoolsutility.blocks.crafter;


import mcjty.lib.blocks.BaseBlockItem;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class CrafterSetup {

    @ObjectHolder("rftoolsutility:crafter1")
    public static CrafterBlock BLOCK_CRAFTER1;
    @ObjectHolder("rftoolsutility:crafter2")
    public static CrafterBlock BLOCK_CRAFTER2;
    @ObjectHolder("rftoolsutility:crafter3")
    public static CrafterBlock BLOCK_CRAFTER3;

    @ObjectHolder("rftoolsutility:crafter1")
    public static TileEntityType<?> TYPE_CRAFTER1;
    @ObjectHolder("rftoolsutility:crafter2")
    public static TileEntityType<?> TYPE_CRAFTER2;
    @ObjectHolder("rftoolsutility:crafter3")
    public static TileEntityType<?> TYPE_CRAFTER3;

    @ObjectHolder("rftoolsutility:crafter")
    public static ContainerType<CrafterContainer> CONTAINER_CRAFTER;

    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        if (!CrafterConfiguration.enabled.get()) {
            return;
        }
        event.getRegistry().register(new CrafterBlock("crafter1", CrafterBlockTileEntity1::new));
        event.getRegistry().register(new CrafterBlock("crafter2", CrafterBlockTileEntity2::new));
        event.getRegistry().register(new CrafterBlock("crafter3", CrafterBlockTileEntity3::new));
    }

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        if (!CrafterConfiguration.enabled.get()) {
            return;
        }
        Item.Properties properties = new Item.Properties().group(RFToolsUtility.setup.getTab());
        event.getRegistry().register(new BaseBlockItem(BLOCK_CRAFTER1, properties));
        event.getRegistry().register(new BaseBlockItem(BLOCK_CRAFTER2, properties));
        event.getRegistry().register(new BaseBlockItem(BLOCK_CRAFTER3, properties));
    }

    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        if (!CrafterConfiguration.enabled.get()) {
            return;
        }
        event.getRegistry().register(TileEntityType.Builder.create(CrafterBlockTileEntity1::new, BLOCK_CRAFTER1).build(null).setRegistryName("crafter1"));
        event.getRegistry().register(TileEntityType.Builder.create(CrafterBlockTileEntity2::new, BLOCK_CRAFTER2).build(null).setRegistryName("crafter2"));
        event.getRegistry().register(TileEntityType.Builder.create(CrafterBlockTileEntity3::new, BLOCK_CRAFTER3).build(null).setRegistryName("crafter3"));
    }

    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        if (!CrafterConfiguration.enabled.get()) {
            return;
        }
        event.getRegistry().register(GenericContainer.createContainerType("crafter"));
    }


    // @todo 1.14
//    @SideOnly(Side.CLIENT)
//    public static void initClient() {
//        if(!CrafterConfiguration.enabled.get()) return;
//        BLOCK_CRAFTER1.initModel();
//        BLOCK_CRAFTER2.initModel();
//        BLOCK_CRAFTER3.initModel();
//    }
}
