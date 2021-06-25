package mcjty.rftoolsutility.modules.tank;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.modules.tank.client.TankModelLoader;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolsutility.setup.Registration.*;

public class TankModule implements IModule {

    public static final RegistryObject<BaseBlock> TANK = BLOCKS.register("tank", TankTE::createBlock);
    public static final RegistryObject<Item> TANK_ITEM = ITEMS.register("tank", () -> new BlockItem(TANK.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_TANK = TILES.register("tank", () -> TileEntityType.Builder.of(TankTE::new, TANK.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_TANK = CONTAINERS.register("tank", GenericContainer::createContainerType);

    public TankModule() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TankModelLoader::register);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiTank.register();
        });
    }

    @Override
    public void initConfig() {
        TankConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
