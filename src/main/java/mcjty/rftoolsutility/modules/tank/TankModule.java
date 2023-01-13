package mcjty.rftoolsutility.modules.tank;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.modules.tank.client.TankModelLoader;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class TankModule implements IModule {

    public static final RegistryObject<BaseBlock> TANK = BLOCKS.register("tank", TankTE::createBlock);
    public static final RegistryObject<Item> TANK_ITEM = ITEMS.register("tank", tab(() -> new BlockItem(TANK.get(), createStandardProperties())));
    public static final RegistryObject<BlockEntityType<?>> TYPE_TANK = TILES.register("tank", () -> BlockEntityType.Builder.of(TankTE::new, TANK.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_TANK = CONTAINERS.register("tank", GenericContainer::createContainerType);

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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(TANK)
                        .ironPickaxeTags()
                        .parentedItem("block/tank_inventory")
                        .standardLoot(TYPE_TANK)
                        .blockState(p -> p.frontBasedModel("tank_inventory", p.modLoc("block/tank0")))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "GGG", "bFb", "iii")
        );
    }
}
