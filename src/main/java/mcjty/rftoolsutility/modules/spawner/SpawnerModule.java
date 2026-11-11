package mcjty.rftoolsutility.modules.spawner;

import com.mojang.serialization.Codec;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.AttachmentTypes;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.modules.various.VariousModule;
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
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipe;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeBuilder;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeSerializer;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeType;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import mcjty.rftoolsutility.setup.Config;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class SpawnerModule implements IModule {

    public static final AttachmentType<Boolean> SPAWNED_BY_RFTOOLS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "spawner"),
            builder -> builder.initializer(() -> false).persistent(Codec.BOOL));

    public static final RBlock<BaseBlock, BlockItem, MatterBeamerTileEntity> MATTER_BEAMER = RBLOCKS.registerBlock("matter_beamer",
            MatterBeamerTileEntity.class,
            MatterBeamerBlock::new,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            MatterBeamerTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_BEAMER = CONTAINERS.register("matter_beamer", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, SpawnerTileEntity> SPAWNER = RBLOCKS.registerBlock("spawner",
            SpawnerTileEntity.class,
            SpawnerTileEntity::createBlock,
            (block, properties) -> new BaseBlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            SpawnerTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SPAWNER = CONTAINERS.register("spawner", GenericContainer::createContainerType);

    public static final DeferredItem<SyringeItem> SYRINGE = ITEMS.register("syringe", tab(SyringeItem::new));

    public static final Supplier<RecipeSerializer<SpawnerRecipe>> SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register("spawner", SpawnerRecipeSerializer::create);

    public static final Identifier SPAWNER_RECIPE_TYPE_ID = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "spawner");
    public static final Supplier<SpawnerRecipeType> SPAWNER_RECIPE_TYPE = RECIPE_TYPES.register("spawner", SpawnerRecipeType::new);

    public static final AttachmentType<SpawnerData> SPAWNER_DATA = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "spawner_data"),
            builder -> builder.initializer(SpawnerData::createDefault).persistent(SpawnerData.CODEC));
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

    public void registerMenuScreens() {
        GuiMatterBeamer.register();
        GuiSpawner.register();
    }

    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        registerMenuScreens();


        MatterBeamerRenderer.register();
    }

    @Override
    public void initConfig() {
        SpawnerConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(MATTER_BEAMER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_beamer_on")
                        .standardLoot(ITEM_INFUSABLE.get())
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
                        .standardLoot(ITEM_SPAWNER_DATA.get(), ITEM_INFUSABLE.get())
                        .blockState(p -> p.orientedBlock(SPAWNER.block().get(), p.frontBasedModel("spawner", p.modLoc("block/machinespawner"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('z', Items.ROTTEN_FLESH)
                                        .define('P', ConventionalItemTags.BONES)
                                        .define('X', ConventionalItemTags.BLAZE_RODS)
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                "rzr", "oFX", "rPr"),
                Dob.itemBuilder(SYRINGE)
                        .shaped(builder -> builder
                                        .define('z', Items.GLASS_BOTTLE)
                                        .unlockedBy("machine_frame", has(VariousModule.MACHINE_FRAME.get())),
                                "i  ", " i ", "  z")
        );

        dataGen.add(Dob.entityBuilder(() -> EntityTypes.PIG).recipeConsumer(() -> output -> {
            Map<String, SpawnerRecipes.MobData> data = DataGenHelper.getDefaultMobData();
            for (Map.Entry<String, SpawnerRecipes.MobData> entry : data.entrySet()) {
                EntityType<?> type = Tools.getEntity(Identifier.parse(entry.getKey()));
                SpawnerRecipes.MobData value = entry.getValue();
                SpawnerRecipeBuilder builder = SpawnerRecipeBuilder.create(type);
                builder.power(value.getSpawnRf());
                builder.item1(value.getItem1().getObject(), value.getItem1().getAmount());
                builder.item2(value.getItem2().getObject(), value.getItem2().getAmount());
                builder.item3(value.getItem3().getObject(), value.getItem3().getAmount());
                builder.build(output);
            }
        }));
    }

    static {
        AttachmentTypes.remember(SPAWNER_DATA);
    }

}
