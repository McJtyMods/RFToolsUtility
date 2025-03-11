package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsbase.setup.Registration;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerBlock;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.modules.spawner.client.GuiMatterBeamer;
import mcjty.rftoolsutility.modules.spawner.client.GuiSpawner;
import mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer;
import mcjty.rftoolsutility.modules.spawner.data.SpawnerData;
import mcjty.rftoolsutility.modules.spawner.data.SyringeData;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeBuilder;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Map;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class SpawnerModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, MatterBeamerTileEntity> MATTER_BEAMER = RBLOCKS.registerBlock("matter_beamer",
            MatterBeamerTileEntity.class,
            MatterBeamerBlock::new,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            MatterBeamerTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_BEAMER = CONTAINERS.register("matter_beamer", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, SpawnerTileEntity> SPAWNER = RBLOCKS.registerBlock("spawner",
            SpawnerTileEntity.class,
            SpawnerTileEntity::createBlock,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            SpawnerTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SPAWNER = CONTAINERS.register("spawner", GenericContainer::createContainerType);

    public static final DeferredItem<SyringeItem> SYRINGE = ITEMS.register("syringe", tab(SyringeItem::new));

    // @todo 1.21 recipe
//    public static final Supplier<SpawnerRecipeSerializer> SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register("spawner", SpawnerRecipeSerializer::new);

    // @todo 1.21 recipe
    public static final ResourceLocation SPAWNER_RECIPE_TYPE_ID = ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "spawner");
//    public static final Supplier<SpawnerRecipeType> SPAWNER_RECIPE_TYPE = RECIPE_TYPES.register("spawner", SpawnerRecipeType::new);

    public static final Supplier<AttachmentType<SpawnerData>> SPAWNER_DATA = ATTACHMENT_TYPES.register(
            "spawner_data", () -> AttachmentType.builder(SpawnerData::createDefault)
                    .serialize(SpawnerData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpawnerData>> ITEM_SPAWNER_DATA = COMPONENTS.registerComponentType(
            "spawner_data",
            builder -> builder
                    .persistent(SpawnerData.CODEC)
                    .networkSynchronized(SpawnerData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SyringeData>> ITEM_SYRINGE_DATA = COMPONENTS.registerComponentType(
            "syringe_data",
            builder -> builder
                    .persistent(SyringeData.CODEC)
                    .networkSynchronized(SyringeData.STREAM_CODEC));

    public SpawnerModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiMatterBeamer.register(event);
        GuiSpawner.register(event);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SyringeItem.initOverrides(SYRINGE.get());
        });

        MatterBeamerRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
        SpawnerConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(MATTER_BEAMER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_beamer_on")
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> {
                            p.variantBlock(MATTER_BEAMER.block().get(), blockState -> {
                                if (blockState.getValue(BlockStateProperties.LIT)) {
                                    return p.models().cubeAll("matter_beamer_on", p.modLoc("block/machinebeamer"));
                                } else {
                                    return p.models().cubeAll("matter_beamer_off", p.modLoc("block/machinebeameroff"));
                                }
                            });
                        })
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('z', Blocks.GLOWSTONE)
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                "RzR", "zFz", "RzR"),
                Dob.blockBuilder(SPAWNER)
                        .ironPickaxeTags()
                        .parentedItem("block/spawner")
                        .standardLoot(ITEM_SPAWNER_DATA.get(), mcjty.lib.setup.Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(SPAWNER.block().get(), p.frontBasedModel("spawner", p.modLoc("block/machinespawner"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('z', Items.ROTTEN_FLESH)
                                        .define('P', Tags.Items.BONES)
                                        .define('X', Tags.Items.RODS_BLAZE)
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                "rzr", "oFX", "rPr"),
                Dob.itemBuilder(SYRINGE)
                        .shaped(builder -> builder
                                        .define('z', Items.GLASS_BOTTLE)
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                "i  ", " i ", "  z")
        );

        Map<String, SpawnerRecipes.MobData> data = DataGenHelper.getDefaultMobData();
        for (Map.Entry<String, SpawnerRecipes.MobData> entry : data.entrySet()) {
            EntityType<?> type = Tools.getEntity(ResourceLocation.parse(entry.getKey()));
            SpawnerRecipes.MobData value = entry.getValue();
            dataGen.add(
                    Dob.entityBuilder(() -> type)
                            .recipeConsumer(() -> consumer -> {
                                SpawnerRecipeBuilder builder = SpawnerRecipeBuilder.create(type);
                                builder.power(value.getSpawnRf());
                                builder.item1(value.getItem1().getObject(), value.getItem1().getAmount());
                                builder.item2(value.getItem2().getObject(), value.getItem2().getAmount());
                                builder.item3(value.getItem3().getObject(), value.getItem3().getAmount());
                                // @todo 1.21 recipe
//                                builder.build(consumer);
                            })
            );
        }
    }
}
