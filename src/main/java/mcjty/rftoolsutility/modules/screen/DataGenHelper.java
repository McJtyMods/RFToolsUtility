package mcjty.rftoolsutility.modules.screen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.lib.datagen.model.BlockModelBuilder;
import mcjty.lib.datagen.model.ModelFile;
import mcjty.lib.datagen.model.ModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class DataGenHelper {

    public static ModelFile screenModel(BaseBlockStateProvider provider, String modelName, Identifier texture) {
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
                .texture("side", Identifier.fromNamespaceAndPath("rftoolsbase", "block/base/machineside"))
                .texture("front", texture);
        return model;
    }


}
