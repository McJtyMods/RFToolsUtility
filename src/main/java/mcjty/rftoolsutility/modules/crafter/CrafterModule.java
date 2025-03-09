package mcjty.rftoolsutility.modules.crafter;


import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.Registration;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.crafter.blocks.*;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.modules.crafter.data.CrafterData;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.setup.Registration.*;

public class CrafterModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, CrafterBaseTE> CRAFTER1 = RBLOCKS.registerBlock("crafter1",
            CrafterBaseTE.class,
            () -> new CrafterBlock(CrafterBaseTE::createTier1),
            block -> new BlockItem(block.get(), createStandardProperties()),
            CrafterBaseTE::createTier1);
    public static final RBlock<BaseBlock, BlockItem, CrafterBaseTE> CRAFTER2 = RBLOCKS.registerBlock("crafter2",
            CrafterBaseTE.class,
            () -> new CrafterBlock(CrafterBaseTE::createTier2),
            block -> new BlockItem(block.get(), createStandardProperties()),
            CrafterBaseTE::createTier2);
    public static final RBlock<BaseBlock, BlockItem, CrafterBaseTE> CRAFTER3 = RBLOCKS.registerBlock("crafter3",
            CrafterBaseTE.class,
            () -> new CrafterBlock(CrafterBaseTE::createTier3),
            block -> new BlockItem(block.get(), createStandardProperties()),
            CrafterBaseTE::createTier3);

    public static final Supplier<MenuType<CrafterContainer>> CONTAINER_CRAFTER = CONTAINERS.register("crafter", GenericContainer::createContainerType);

    public static final Supplier<AttachmentType<CrafterData>> CRAFTER_DATA = ATTACHMENT_TYPES.register(
            "crafter_data", () -> AttachmentType.builder(CrafterData::createDefault)
                    .serialize(CrafterData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CrafterData>> ITEM_CRAFTER_DATA = COMPONENTS.registerComponentType(
            "crafter_data",
            builder -> builder
                    .persistent(CrafterData.CODEC)
                    .networkSynchronized(CrafterData.STREAM_CODEC));

    public CrafterModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiCrafter.register(event);
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    @Override
    public void initConfig(IEventBus bus) {
        CrafterConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(CRAFTER1)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter1")
                        .standardLoot(ITEM_CRAFTER_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(CRAFTER1.block().get(), p.frontBasedModel("crafter1", p.modLoc("block/machinecrafter1"))))
                        .shaped(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                " T ", "CFC", " T "),
                Dob.blockBuilder(CRAFTER2)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter2")
                        .standardLoot(ITEM_CRAFTER_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(CRAFTER2.block().get(), p.frontBasedModel("crafter2", p.modLoc("block/machinecrafter2"))))
                        .shapedComponentPreserve(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('M', CRAFTER1.item().get())
                                        .unlockedBy("crafter1", has(CRAFTER1.item().get())),
                                " T ", "CMC", " T "),
                Dob.blockBuilder(CRAFTER3)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter3")
                        .standardLoot(ITEM_CRAFTER_DATA.get(), Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(CRAFTER3.block().get(), p.frontBasedModel("crafter3", p.modLoc("block/machinecrafter3"))))
                        .shapedComponentPreserve(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('M', CRAFTER2.item().get())
                                        .unlockedBy("crafter2", has(CRAFTER2.item().get())),
                                " T ", "CMC", " T ")
        );
    }
}
