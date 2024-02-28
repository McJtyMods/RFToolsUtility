package mcjty.rftoolsutility.modules.tank.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mcjty.lib.client.BaseGeometry;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TankModelLoader implements IGeometryLoader<TankModelLoader.TankModelGeometry> {


    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("tankloader", new TankModelLoader());
    }

    @Override
    public TankModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new TankModelGeometry();
    }


    public static class TankModelGeometry extends BaseGeometry<TankModelGeometry> {

        @Override
        public BakedModel bake() {
            return new TankBakedModel();
        }

        @Override
        public Collection<Material> getMaterials() {
            List<Material> materials = new ArrayList<>();
            for (int i = 0 ; i <= 8 ; i++) {
                materials.add(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(RFToolsUtility.MODID, "block/tank" + i)));
            }
            return materials;
        }
    }
}
