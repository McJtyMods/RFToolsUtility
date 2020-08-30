package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.setup.Registration;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerBlock;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.modules.spawner.client.GuiMatterBeamer;
import mcjty.rftoolsutility.modules.spawner.client.GuiSpawner;
import mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class SpawnerModule implements IModule {

    public static final RegistryObject<Block> MATTER_BEAMER = BLOCKS.register("matter_beamer", MatterBeamerBlock::new);
    public static final RegistryObject<Item> MATTER_BEAMER_ITEM = ITEMS.register("matter_beamer", () -> new BlockItem(MATTER_BEAMER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<MatterBeamerTileEntity>> TYPE_MATTER_BEAMER = TILES.register("matter_beamer", () -> TileEntityType.Builder.create(MatterBeamerTileEntity::new, MATTER_BEAMER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_MATTER_BEAMER = CONTAINERS.register("matter_beamer", GenericContainer::createContainerType);

    public static final RegistryObject<Block> SPAWNER = BLOCKS.register("spawner", SpawnerTileEntity::createBlock);
    public static final RegistryObject<Item> SPAWNER_ITEM = ITEMS.register("spawner", () -> new BlockItem(SPAWNER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_SPAWNER = TILES.register("spawner", () -> TileEntityType.Builder.create(SpawnerTileEntity::new, SPAWNER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_SPAWNER = CONTAINERS.register("spawner", GenericContainer::createContainerType);

    public static final RegistryObject<SyringeItem> SYRINGE = ITEMS.register("syringe", SyringeItem::new);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GenericGuiContainer.register(SpawnerModule.CONTAINER_MATTER_BEAMER.get(), GuiMatterBeamer::new);
            GenericGuiContainer.register(SpawnerModule.CONTAINER_SPAWNER.get(), GuiSpawner::new);
        });

        MatterBeamerRenderer.register();
    }

    @Override
    public void initConfig() {
        SpawnerConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
