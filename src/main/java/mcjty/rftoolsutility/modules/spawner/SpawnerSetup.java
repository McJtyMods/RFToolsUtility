package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsbase.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class SpawnerSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<Block> MATTER_BEAMER = BLOCKS.register("matter_beamer", MatterBeamerBlock::new);
    public static final RegistryObject<Item> MATTER_BEAMER_ITEM = ITEMS.register("matter_beamer", () -> new BlockItem(MATTER_BEAMER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_MATTER_BEAMER = TILES.register("matter_beamer", () -> TileEntityType.Builder.create(MatterBeamerTileEntity::new, MATTER_BEAMER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_MATTER_BEAMER = CONTAINERS.register("matter_beamer", GenericContainer::createContainerType);

//    public static GenericBlock<SpawnerTileEntity, GenericContainer> spawnerBlock;
//    public static MatterBeamerBlock matterBeamerBlock;

    public static void init() {
        spawnerBlock = ModBlocks.builderFactory.<SpawnerTileEntity> builder("spawner")
                .tileEntityClass(SpawnerTileEntity.class)
                .container(SpawnerTileEntity.CONTAINER_FACTORY)
                .infusable()
                .guiId(GuiProxy.GUI_SPAWNER)
                .moduleSupport(SpawnerTileEntity.MODULE_SUPPORT)
                .info("message.rftools.shiftmessage")
                .infoExtended("message.rftools.spawner")
                .build();
        matterBeamerBlock = new MatterBeamerBlock();
    }
}
