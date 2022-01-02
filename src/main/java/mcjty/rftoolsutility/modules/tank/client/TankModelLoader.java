package mcjty.rftoolsutility.modules.tank.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

public class TankModelLoader implements IModelLoader<TankModelLoader.TankModelGeometry> {

    public static void register(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(RFToolsUtility.MODID, "tankloader"), new TankModelLoader());
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {

    }

    @Nonnull
    @Override
    public TankModelGeometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        return new TankModelGeometry();
    }

    public static class TankModelGeometry implements IModelGeometry<TankModelGeometry> {
        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new TankBakedModel();
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            List<Material> materials = new ArrayList<>();
            for (int i = 0 ; i <= 8 ; i++) {
                materials.add(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(RFToolsUtility.MODID, "block/tank" + i)));
            }
            return materials;
        }
    }
}
