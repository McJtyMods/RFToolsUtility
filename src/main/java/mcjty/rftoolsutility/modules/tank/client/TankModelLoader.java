package mcjty.rftoolsutility.modules.tank.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TankModelLoader implements IGeometryLoader<TankModelLoader.TankModelGeometry> {


    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("tankloader", new TankModelLoader());
    }

    @Override
    public TankModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new TankModelGeometry();
    }

    public static class TankModelGeometry implements IUnbakedGeometry<TankModelGeometry> {

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new TankBakedModel();
        }

        // @todo 1.19.3
//        @Override
//        public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
//            List<Material> materials = new ArrayList<>();
//            for (int i = 0 ; i <= 8 ; i++) {
//                materials.add(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(RFToolsUtility.MODID, "block/tank" + i)));
//            }
//            return materials;
//        }
    }
}
