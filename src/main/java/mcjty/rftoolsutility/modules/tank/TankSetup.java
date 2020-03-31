package mcjty.rftoolsutility.modules.tank;

import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class TankSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<Block> TANK = BLOCKS.register("tank", TankTE::createBlock);
    public static final RegistryObject<Item> TANK_ITEM = ITEMS.register("tank", () -> new BlockItem(TANK.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_TANK = TILES.register("tank", () -> TileEntityType.Builder.create(TankTE::new, TANK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_TANK = CONTAINERS.register("tank", GenericContainer::createContainerType);
}
