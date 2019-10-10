package mcjty.rftoolsutility.modules.tank;

import mcjty.lib.blocks.BaseBlockItem;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class TankSetup {

    @ObjectHolder("rftoolsutility:tank")
    public static Block BLOCK_TANK;

    @ObjectHolder("rftoolsutility:tank")
    public static TileEntityType<?> TYPE_TANK;

    @ObjectHolder("rftoolsutility:tank")
    public static ContainerType<GenericContainer> CONTAINER_TANK;

    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(TankTE.createBlock());
    }

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsUtility.setup.getTab());
        event.getRegistry().register(new BaseBlockItem(BLOCK_TANK, properties));
    }

    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(TankTE::new, BLOCK_TANK).build(null).setRegistryName("tank"));
    }

    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(GenericContainer.createContainerType("tank"));
    }

}
