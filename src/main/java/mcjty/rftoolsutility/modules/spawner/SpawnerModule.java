package mcjty.rftoolsutility.modules.spawner;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.setup.Registration;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerBlock;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.modules.spawner.client.GuiMatterBeamer;
import mcjty.rftoolsutility.modules.spawner.client.GuiSpawner;
import mcjty.rftoolsutility.modules.spawner.client.MatterBeamerRenderer;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeSerializer;
import mcjty.rftoolsutility.modules.spawner.recipes.SpawnerRecipeType;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolsutility.setup.Registration.*;

public class SpawnerModule implements IModule {

    public static final RegistryObject<BaseBlock> MATTER_BEAMER = BLOCKS.register("matter_beamer", MatterBeamerBlock::new);
    public static final RegistryObject<Item> MATTER_BEAMER_ITEM = ITEMS.register("matter_beamer", () -> new BlockItem(MATTER_BEAMER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<MatterBeamerTileEntity>> TYPE_MATTER_BEAMER = TILES.register("matter_beamer", () -> BlockEntityType.Builder.of(MatterBeamerTileEntity::new, MATTER_BEAMER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_MATTER_BEAMER = CONTAINERS.register("matter_beamer", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> SPAWNER = BLOCKS.register("spawner", SpawnerTileEntity::createBlock);
    public static final RegistryObject<Item> SPAWNER_ITEM = ITEMS.register("spawner", () -> new BlockItem(SPAWNER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_SPAWNER = TILES.register("spawner", () -> BlockEntityType.Builder.of(SpawnerTileEntity::new, SPAWNER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_SPAWNER = CONTAINERS.register("spawner", GenericContainer::createContainerType);

    public static final RegistryObject<SyringeItem> SYRINGE = ITEMS.register("syringe", SyringeItem::new);

    public static final RegistryObject<SpawnerRecipeSerializer> SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register("spawner", SpawnerRecipeSerializer::new);

    public static final ResourceLocation SPAWNER_RECIPE_TYPE_ID = new ResourceLocation(RFToolsUtility.MODID, "spawner");
    public static final RegistryObject<SpawnerRecipeType> SPAWNER_RECIPE_TYPE = RECIPE_TYPES.register("spawner", SpawnerRecipeType::new);

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
    public void initConfig() {
        SpawnerConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
