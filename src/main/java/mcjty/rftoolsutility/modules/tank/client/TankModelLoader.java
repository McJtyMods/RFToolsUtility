package mcjty.rftoolsutility.modules.tank.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TankModelLoader implements IModelLoader<TankModelLoader.TankModelGeometry> {

    public static void register(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(RFToolsUtility.MODID, "tankloader"), new TankModelLoader());
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public TankModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new TankModelGeometry();
    }

    public static class TankModelGeometry implements IModelGeometry<TankModelGeometry> {
        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
            return new TankBakedModel();
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            List<Material> materials = new ArrayList<>();
            for (int i = 0 ; i <= 8 ; i++) {
                materials.add(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsUtility.MODID, "block/tank" + i)));
            }
            return materials;
        }
    }
}
