package mcjty.rftoolsutility.modules.crafter;


import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsutility.modules.crafter.blocks.*;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class CrafterModule implements IModule {

    public static final RegistryObject<BaseBlock> CRAFTER1 = BLOCKS.register("crafter1", () -> new CrafterBlock(CrafterBlockTileEntity1::new));
    public static final RegistryObject<Item> CRAFTER1_ITEM = ITEMS.register("crafter1", () -> new BlockItem(CRAFTER1.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_CRAFTER1 = TILES.register("crafter1", () -> BlockEntityType.Builder.of(CrafterBlockTileEntity1::new, CRAFTER1.get()).build(null));

    public static final RegistryObject<BaseBlock> CRAFTER2 = BLOCKS.register("crafter2", () -> new CrafterBlock(CrafterBlockTileEntity2::new));
    public static final RegistryObject<Item> CRAFTER2_ITEM = ITEMS.register("crafter2", () -> new BlockItem(CRAFTER2.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_CRAFTER2 = TILES.register("crafter2", () -> BlockEntityType.Builder.of(CrafterBlockTileEntity2::new, CRAFTER2.get()).build(null));

    public static final RegistryObject<BaseBlock> CRAFTER3 = BLOCKS.register("crafter3", () -> new CrafterBlock(CrafterBlockTileEntity3::new));
    public static final RegistryObject<Item> CRAFTER3_ITEM = ITEMS.register("crafter3", () -> new BlockItem(CRAFTER3.get(), createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_CRAFTER3 = TILES.register("crafter3", () -> BlockEntityType.Builder.of(CrafterBlockTileEntity3::new, CRAFTER3.get()).build(null));

    public static final RegistryObject<MenuType<CrafterContainer>> CONTAINER_CRAFTER = CONTAINERS.register("crafter", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiCrafter.register();
        });
    }

    @Override
    public void initConfig() {
        CrafterConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
