package mcjty.rftoolsutility.modules.crafter;


import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.crafter.blocks.*;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class CrafterModule implements IModule {

    public static final DeferredBlock<BaseBlock> CRAFTER1 = BLOCKS.register("crafter1", () -> new CrafterBlock(CrafterBlockTileEntity1::new));
    public static final DeferredItem<Item> CRAFTER1_ITEM = ITEMS.register("crafter1", tab(() -> new BlockItem(CRAFTER1.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_CRAFTER1 = TILES.register("crafter1", () -> BlockEntityType.Builder.of(CrafterBlockTileEntity1::new, CRAFTER1.get()).build(null));

    public static final DeferredBlock<BaseBlock> CRAFTER2 = BLOCKS.register("crafter2", () -> new CrafterBlock(CrafterBlockTileEntity2::new));
    public static final DeferredItem<Item> CRAFTER2_ITEM = ITEMS.register("crafter2", tab(() -> new BlockItem(CRAFTER2.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_CRAFTER2 = TILES.register("crafter2", () -> BlockEntityType.Builder.of(CrafterBlockTileEntity2::new, CRAFTER2.get()).build(null));

    public static final DeferredBlock<BaseBlock> CRAFTER3 = BLOCKS.register("crafter3", () -> new CrafterBlock(CrafterBlockTileEntity3::new));
    public static final DeferredItem<Item> CRAFTER3_ITEM = ITEMS.register("crafter3", tab(() -> new BlockItem(CRAFTER3.get(), createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_CRAFTER3 = TILES.register("crafter3", () -> BlockEntityType.Builder.of(CrafterBlockTileEntity3::new, CRAFTER3.get()).build(null));

    public static final Supplier<MenuType<CrafterContainer>> CONTAINER_CRAFTER = CONTAINERS.register("crafter", GenericContainer::createContainerType);

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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(CRAFTER1)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter1")
                        .standardLoot(TYPE_CRAFTER1)
                        .blockState(p -> p.orientedBlock(CRAFTER1.get(), p.frontBasedModel("crafter1", p.modLoc("block/machinecrafter1"))))
                        .shaped(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                " T ", "CFC", " T "),
                Dob.blockBuilder(CRAFTER2)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter2")
                        .standardLoot(TYPE_CRAFTER2)
                        .blockState(p -> p.orientedBlock(CRAFTER2.get(), p.frontBasedModel("crafter2", p.modLoc("block/machinecrafter2"))))
                        .shapedNBT(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('M', CRAFTER1.get())
                                        .unlockedBy("crafter1", has(CRAFTER1.get())),
                                " T ", "CMC", " T "),
                Dob.blockBuilder(CRAFTER3)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter3")
                        .standardLoot(TYPE_CRAFTER3)
                        .blockState(p -> p.orientedBlock(CRAFTER3.get(), p.frontBasedModel("crafter3", p.modLoc("block/machinecrafter3"))))
                        .shapedNBT(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('M', CRAFTER2.get())
                                        .unlockedBy("crafter2", has(CRAFTER2.get())),
                                " T ", "CMC", " T ")
        );
    }
}
