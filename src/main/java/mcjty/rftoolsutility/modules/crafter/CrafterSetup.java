package mcjty.rftoolsutility.modules.crafter;


import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.modules.crafter.blocks.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class CrafterSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<BaseBlock> CRAFTER1 = BLOCKS.register("crafter1", () -> new CrafterBlock(CrafterBlockTileEntity1::new));
    public static final RegistryObject<Item> CRAFTER1_ITEM = ITEMS.register("crafter1", () -> new BlockItem(CRAFTER1.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CRAFTER1 = TILES.register("crafter1", () -> TileEntityType.Builder.create(CrafterBlockTileEntity1::new, CRAFTER1.get()).build(null));

    public static final RegistryObject<BaseBlock> CRAFTER2 = BLOCKS.register("crafter2", () -> new CrafterBlock(CrafterBlockTileEntity2::new));
    public static final RegistryObject<Item> CRAFTER2_ITEM = ITEMS.register("crafter2", () -> new BlockItem(CRAFTER2.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CRAFTER2 = TILES.register("crafter2", () -> TileEntityType.Builder.create(CrafterBlockTileEntity2::new, CRAFTER2.get()).build(null));

    public static final RegistryObject<BaseBlock> CRAFTER3 = BLOCKS.register("crafter3", () -> new CrafterBlock(CrafterBlockTileEntity3::new));
    public static final RegistryObject<Item> CRAFTER3_ITEM = ITEMS.register("crafter3", () -> new BlockItem(CRAFTER3.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CRAFTER3 = TILES.register("crafter3", () -> TileEntityType.Builder.create(CrafterBlockTileEntity3::new, CRAFTER3.get()).build(null));

    public static final RegistryObject<ContainerType<CrafterContainer>> CONTAINER_CRAFTER = CONTAINERS.register("crafter", GenericContainer::createContainerType);
}
