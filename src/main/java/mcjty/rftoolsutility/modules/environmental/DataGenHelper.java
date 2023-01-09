package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolsbase.RFToolsBase;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

public class DataGenHelper {

    public static void createEnvController(BaseBlockStateProvider provider) {
        BlockModelBuilder model = provider.models().getBuilder("block/environmental_controller");
        model.element().from(0f, 0f, 0f).to(16f, 16f, 16f).allFaces((direction, faceBuilder) -> {
            if (direction == Direction.UP) {
                faceBuilder.texture("#top");
            } else if (direction == Direction.DOWN) {
                faceBuilder.texture("#bottom");
            } else {
                faceBuilder.texture("#side");
            }
        }).end().renderType("translucent");

        model.element().from(0f, 3f, 0f).to(16f, 3f, 16f).face(Direction.UP).texture("#bottom").end();
        model.element().from(0f, 16f, 0f).to(16f, 16f, 16f).face(Direction.DOWN).texture("#top").end();

        model.element().from(0f, 0, 16f).to(16f, 16f, 16f).face(Direction.NORTH).texture("#side").end();
        model.element().from(0f, 0, 0f).to(16f, 16f, 0f).face(Direction.SOUTH).texture("#side").end();
        model.element().from(16f, 0, 0f).to(16f, 16f, 16f).face(Direction.WEST).texture("#side").end();
        model.element().from(0f, 0, 0f).to(0f, 16f, 16f).face(Direction.EAST).texture("#side").end();

        model
                .texture("top", new ResourceLocation(RFToolsBase.MODID, "block/base/machinetop"))
                .texture("side", provider.modLoc("block/machineenvironmentalcontroller"))
                .texture("bottom", new ResourceLocation(RFToolsBase.MODID, "block/base/machinebottom"));

        MultiPartBlockStateBuilder bld = provider.getMultipartBuilder(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get());
        bld.part().modelFile(model).addModel();
    }


}
