package mcjty.rftoolsutility.modules.tank;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.modules.tank.client.TankModelLoader;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class TankModule implements IModule {

    public static final DeferredBlock<BaseBlock> TANK = BLOCKS.register("tank", TankTE::createBlock);
    public static final DeferredItem<Item> TANK_ITEM = ITEMS.register("tank", tab(() -> new BlockItem(TANK.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_TANK = TILES.register("tank", () -> BlockEntityType.Builder.of(TankTE::new, TANK.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_TANK = CONTAINERS.register("tank", GenericContainer::createContainerType);

    public TankModule(IEventBus bus, Dist dist) {
        bus.addListener(TankModelLoader::register);
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
    public void initConfig(IEventBus bus) {
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
