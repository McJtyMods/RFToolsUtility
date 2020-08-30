package mcjty.rftoolsutility.modules.crafter;


import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.blocks.*;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class CrafterModule implements IModule {

    public static final RegistryObject<BaseBlock> CRAFTER1 = BLOCKS.register("crafter1", () -> new CrafterBlock(CrafterBlockTileEntity1::new));
    public static final RegistryObject<Item> CRAFTER1_ITEM = ITEMS.register("crafter1", () -> new BlockItem(CRAFTER1.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CRAFTER1 = TILES.register("crafter1", () -> TileEntityType.Builder.create(CrafterBlockTileEntity1::new, CRAFTER1.get()).build(null));

    public static final RegistryObject<BaseBlock> CRAFTER2 = BLOCKS.register("crafter2", () -> new CrafterBlock(CrafterBlockTileEntity2::new));
    public static final RegistryObject<Item> CRAFTER2_ITEM = ITEMS.register("crafter2", () -> new BlockItem(CRAFTER2.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CRAFTER2 = TILES.register("crafter2", () -> TileEntityType.Builder.create(CrafterBlockTileEntity2::new, CRAFTER2.get()).build(null));

    public static final RegistryObject<BaseBlock> CRAFTER3 = BLOCKS.register("crafter3", () -> new CrafterBlock(CrafterBlockTileEntity3::new));
    public static final RegistryObject<Item> CRAFTER3_ITEM = ITEMS.register("crafter3", () -> new BlockItem(CRAFTER3.get(), RFToolsUtility.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CRAFTER3 = TILES.register("crafter3", () -> TileEntityType.Builder.create(CrafterBlockTileEntity3::new, CRAFTER3.get()).build(null));

    public static final RegistryObject<ContainerType<CrafterContainer>> CONTAINER_CRAFTER = CONTAINERS.register("crafter", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GenericGuiContainer.register(CrafterModule.CONTAINER_CRAFTER.get(), GuiCrafter::new);
        });
    }

    @Override
    public void initConfig() {
        CrafterConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
