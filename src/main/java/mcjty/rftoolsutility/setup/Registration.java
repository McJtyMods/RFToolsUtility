package mcjty.rftoolsutility.setup;

import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.container.AttachmentTypes;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.setup.DeferredRegister;
import mcjty.rftoolsbase.modules.tablet.TabletModule;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.playerprops.BuffProperties;
import mcjty.rftoolsutility.playerprops.FavoriteDestinationsProperties;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

import static mcjty.rftoolsutility.RFToolsUtility.MODID;

public class Registration {

    public static final RBlockRegistry RBLOCKS = new RBlockRegistry(MODID, RFToolsUtility.setup::addTabItem);
    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(MODID);
    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final AttachmentType<BuffProperties> ATTACHMENT_TYPE_BUFF_PROPERTIES = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "buff_properties"),
            builder -> builder.initializer(BuffProperties::new).persistent(BuffProperties.CODEC).copyOnDeath());

    public static final AttachmentType<FavoriteDestinationsProperties> ATTACHMENT_TYPE_FAVORITE_DESTINATIONS_PROPERTIES = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolsutility", "favorite_destinations_properties"),
            builder -> builder.initializer(FavoriteDestinationsProperties::new).persistent(FavoriteDestinationsProperties.CODEC).copyOnDeath());

    public static void register() {
        ModSounds.init();
        RBLOCKS.register();
        BLOCKS.register();
        ITEMS.register();
        TILES.register();
        CONTAINERS.register();
        SOUNDS.register();
        ENTITIES.register();
        RECIPE_SERIALIZERS.register();
        RECIPE_TYPES.register();
        TABS.register();
        COMPONENTS.register();
    }

    public static BlockBehaviour.Properties standardBlockProperties(BlockBehaviour.Properties properties) {
        return properties.mapColor(MapColor.METAL).strength(2.0f).requiresCorrectToolForDrops().sound(SoundType.METAL);
    }

    public static Item.Properties createStandardProperties() {
        return RFToolsUtility.setup.defaultProperties();
    }

    public static Supplier<CreativeModeTab> TAB = TABS.register("rftoolsutility", () -> FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(TeleporterModule.CHARGED_PORTER.get()))
            .displayItems((featureFlags, output) -> {
                RFToolsUtility.setup.populateTab(output);
                // Filled tablet variants share this single empty-tablet entry.
                output.accept(TabletModule.TABLET.get());
            })
            .build());

    static {
        AttachmentTypes.remember(ATTACHMENT_TYPE_BUFF_PROPERTIES);
        AttachmentTypes.remember(ATTACHMENT_TYPE_FAVORITE_DESTINATIONS_PROPERTIES);
    }

}
