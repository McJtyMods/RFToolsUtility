package mcjty.rftoolsutility.modules.crafter;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.AttachmentTypes;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredHolder;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.crafter.blocks.*;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.modules.crafter.data.CrafterData;
import mcjty.rftoolsutility.setup.Config;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;
import static mcjty.rftoolsutility.setup.Registration.*;

public class CrafterModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, CrafterBaseTE> CRAFTER1 = RBLOCKS.registerBlock("crafter1",
            CrafterBaseTE.class,
            properties -> new CrafterBlock(properties, CrafterBaseTE::createTier1),
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            CrafterBaseTE::createTier1);
    public static final RBlock<BaseBlock, BlockItem, CrafterBaseTE> CRAFTER2 = RBLOCKS.registerBlock("crafter2",
            CrafterBaseTE.class,
            properties -> new CrafterBlock(properties, CrafterBaseTE::createTier2),
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            CrafterBaseTE::createTier2);
    public static final RBlock<BaseBlock, BlockItem, CrafterBaseTE> CRAFTER3 = RBLOCKS.registerBlock("crafter3",
            CrafterBaseTE.class,
            properties -> new CrafterBlock(properties, CrafterBaseTE::createTier3),
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            CrafterBaseTE::createTier3);

    public static final Supplier<MenuType<CrafterContainer>> CONTAINER_CRAFTER = CONTAINERS.register("crafter", GenericContainer::createContainerType);

    public static final AttachmentType<CrafterData> CRAFTER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "crafter_data"),
            builder -> builder.initializer(CrafterData::createDefault).persistent(CrafterData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CrafterData>> ITEM_CRAFTER_DATA = COMPONENTS.registerComponentType(
            "crafter_data",
            builder -> builder
                    .persistent(CrafterData.CODEC)
                    .networkSynchronized(CrafterData.STREAM_CODEC));

    @Override
    public void init() {

    }

    public void registerMenuScreens() {
        GuiCrafter.register();
    }

    @Override
    public void initClient() {
        registerMenuScreens();
    }

    @Override
    public void initConfig() {
        CrafterConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(CRAFTER1)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter1")
                        .standardLoot(ITEM_CRAFTER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(CRAFTER1.block().get(), p.frontBasedModel("crafter1", p.modLoc("block/machinecrafter1"))))
                        .shaped(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                " T ", "CFC", " T "),
                Dob.blockBuilder(CRAFTER2)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter2")
                        .standardLoot(ITEM_CRAFTER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(CRAFTER2.block().get(), p.frontBasedModel("crafter2", p.modLoc("block/machinecrafter2"))))
                        .shapedComponentPreserve(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('M', CRAFTER1.item().get())
                                        .unlockedBy("crafter1", has(CRAFTER1.item().get())),
                                " T ", "CMC", " T "),
                Dob.blockBuilder(CRAFTER3)
                        .ironPickaxeTags()
                        .parentedItem("block/crafter3")
                        .standardLoot(ITEM_CRAFTER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(CRAFTER3.block().get(), p.frontBasedModel("crafter3", p.modLoc("block/machinecrafter3"))))
                        .shapedComponentPreserve(builder -> builder
                                        .define('C', Blocks.CRAFTING_TABLE)
                                        .define('M', CRAFTER2.item().get())
                                        .unlockedBy("crafter2", has(CRAFTER2.item().get())),
                                " T ", "CMC", " T ")
        );
    }

    static {
        AttachmentTypes.remember(CRAFTER_DATA);
    }

}
