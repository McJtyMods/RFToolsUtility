package mcjty.rftoolsutility.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mcjty.rftoolsbase.tools.ClientItemDefinitions;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.data.PackOutput;

import java.util.Map;

public final class UtilityItemModels {

    private UtilityItemModels() {
    }

    public static ClientItemDefinitions provider(PackOutput output) {
        return new ClientItemDefinitions(output, RFToolsUtility.MODID, Map.of(
                "syringe", range("level", "syringe", 0, 5),
                "charged_porter", range("charge", "charged_porter", 1, 9),
                "advanced_charged_porter", range("charge", "advanced_charged_porter", 1, 9)));
    }

    private static JsonObject range(String property, String modelName, int first, int last) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:range_dispatch");
        model.addProperty("property", RFToolsUtility.MODID + ":" + property);
        JsonArray entries = new JsonArray();
        for (int value = first; value <= last; value++) {
            JsonObject entry = new JsonObject();
            entry.addProperty("threshold", value);
            entry.add("model", itemModel(modelName + (value - first)));
            entries.add(entry);
        }
        model.add("entries", entries);
        model.add("fallback", itemModel(modelName + "0"));
        JsonObject definition = new JsonObject();
        definition.add("model", model);
        return definition;
    }

    private static JsonObject itemModel(String name) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", RFToolsUtility.MODID + ":item/" + name);
        return model;
    }
}
