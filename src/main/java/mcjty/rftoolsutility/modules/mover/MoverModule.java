package mcjty.rftoolsutility.modules.mover;


import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsutility.modules.mover.blocks.MoverTileEntity;
import mcjty.rftoolsutility.modules.mover.client.GuiMover;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.*;

public class MoverModule implements IModule {

    public static final RegistryObject<BaseBlock> MOVER = BLOCKS.register("mover", MoverTileEntity::createBlock);
    public static final RegistryObject<Item> MOVER_ITEM = ITEMS.register("mover", () -> new BlockItem(MOVER.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_MOVER = TILES.register("mover", () -> BlockEntityType.Builder.of(MoverTileEntity::new, MOVER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_MOVER = CONTAINERS.register("mover", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiMover.register();
        });
    }

    @Override
    public void initConfig() {
        MoverConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
