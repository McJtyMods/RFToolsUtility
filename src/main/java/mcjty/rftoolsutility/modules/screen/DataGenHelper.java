package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;

public class DataGenHelper {

    public static ModelFile screenModel(BaseBlockStateProvider provider, String modelName, ResourceLocation texture) {
        BlockModelBuilder model = provider.models().getBuilder(ModelProvider.BLOCK_FOLDER + "/" + modelName)
                .parent(provider.models().getExistingFile(provider.mcLoc("block")));
        model.element().from(0, 0, 13).to(16, 16, 16)
                .face(Direction.DOWN).cullface(Direction.DOWN).texture("#side").end()
                .face(Direction.UP).cullface(Direction.UP).texture("#side").end()
                .face(Direction.EAST).cullface(Direction.EAST).texture("#side").end()
                .face(Direction.WEST).cullface(Direction.WEST).texture("#side").end()
                .face(Direction.NORTH).texture("#front").end()
                .face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#side").end()
                .end()
                .texture("side", new ResourceLocation("rftoolsbase", "block/base/machineside"))
                .texture("front", texture);
        return model;
    }


}
