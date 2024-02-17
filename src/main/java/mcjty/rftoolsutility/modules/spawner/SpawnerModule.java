package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
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
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeBuilder;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeSerializer;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeType;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipes;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolsutility.RFToolsUtility.tab;
import static mcjty.rftoolsutility.setup.Registration.*;

public class SpawnerModule implements IModule {

    public static final DeferredBlock<BaseBlock> MATTER_BEAMER = BLOCKS.register("matter_beamer", MatterBeamerBlock::new);
    public static final DeferredItem<Item> MATTER_BEAMER_ITEM = ITEMS.register("matter_beamer", tab(() -> new BlockItem(MATTER_BEAMER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<MatterBeamerTileEntity>> TYPE_MATTER_BEAMER = TILES.register("matter_beamer", () -> BlockEntityType.Builder.of(MatterBeamerTileEntity::new, MATTER_BEAMER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_MATTER_BEAMER = CONTAINERS.register("matter_beamer", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> SPAWNER = BLOCKS.register("spawner", SpawnerTileEntity::createBlock);
    public static final DeferredItem<Item> SPAWNER_ITEM = ITEMS.register("spawner", tab(() -> new BlockItem(SPAWNER.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_SPAWNER = TILES.register("spawner", () -> BlockEntityType.Builder.of(SpawnerTileEntity::new, SPAWNER.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_SPAWNER = CONTAINERS.register("spawner", GenericContainer::createContainerType);

    public static final DeferredItem<SyringeItem> SYRINGE = ITEMS.register("syringe", tab(SyringeItem::new));

    public static final Supplier<SpawnerRecipeSerializer> SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register("spawner", SpawnerRecipeSerializer::new);

    public static final ResourceLocation SPAWNER_RECIPE_TYPE_ID = new ResourceLocation(RFToolsUtility.MODID, "spawner");
    public static final Supplier<SpawnerRecipeType> SPAWNER_RECIPE_TYPE = RECIPE_TYPES.register("spawner", SpawnerRecipeType::new);

    public SpawnerModule() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiMatterBeamer.register();
            GuiSpawner.register();
            SyringeItem.initOverrides(SYRINGE.get());
        });

        MatterBeamerRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {
        SpawnerConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(MATTER_BEAMER)
                        .ironPickaxeTags()
                        .parentedItem("block/matter_beamer_on")
                        .standardLoot(TYPE_MATTER_BEAMER)
                        .blockState(p -> {
                            p.variantBlock(MATTER_BEAMER.get(), blockState -> {
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
                        .standardLoot(TYPE_SPAWNER)
                        .blockState(p -> p.orientedBlock(SPAWNER.get(), p.frontBasedModel("spawner", p.modLoc("block/machinespawner"))))
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
            EntityType<?> type = Tools.getEntity(new ResourceLocation(entry.getKey()));
            SpawnerRecipes.MobData value = entry.getValue();
            dataGen.add(
                    Dob.entityBuilder(() -> type)
                            .recipeConsumer(() -> consumer -> {
                                SpawnerRecipeBuilder builder = SpawnerRecipeBuilder.create(type);
                                builder.power(value.getSpawnRf());
                                builder.item1(value.getItem1().getObject(), value.getItem1().getAmount());
                                builder.item2(value.getItem2().getObject(), value.getItem2().getAmount());
                                builder.item3(value.getItem3().getObject(), value.getItem3().getAmount());
                                builder.build(consumer);
                            })
            );
        }
    }
}
