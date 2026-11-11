package mcjty.rftoolsutility.modules.tank;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.tank.blocks.TankTE;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.lib.setup.Registration.ITEM_FLUIDS;
import static mcjty.rftoolsutility.setup.Registration.*;

public class TankModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, TankTE> TANK = RBLOCKS.registerBlock("tank",
            TankTE.class,
            TankTE::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            TankTE::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_TANK = CONTAINERS.register("tank", GenericContainer::createContainerType);

    @Override
    public void init() {

    }

    public void registerMenuScreens() {
        GuiTank.register();
    }

    @Override
    public void initClient() {
        registerMenuScreens();
    }

    @Override
    public void initConfig() {
        TankConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(TANK)
                        .ironPickaxeTags()
                        .parentedItem("block/tank_inventory")
                        .standardLoot(ITEM_FLUIDS.get())
                        .blockState(p -> p.frontBasedModel("tank_inventory", p.modLoc("block/tank0")))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "GGG", "bFb", "iii")
        );
    }
}
