package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsUtility.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        variantBlock(SpawnerModule.MATTER_BEAMER.get(), blockState -> {
            if (blockState.getValue(BlockStateProperties.LIT)) {
                return models().cubeAll("matter_beamer_on", modLoc("block/machinebeamer"));
            } else {
                return models().cubeAll("matter_beamer_off", modLoc("block/machinebeameroff"));
            }
        });
        orientedBlock(SpawnerModule.SPAWNER.get(), frontBasedModel("spawner", modLoc("block/machinespawner")));

        orientedBlock(CrafterModule.CRAFTER1.get(), frontBasedModel("crafter1", modLoc("block/machinecrafter1")));
        orientedBlock(CrafterModule.CRAFTER2.get(), frontBasedModel("crafter2", modLoc("block/machinecrafter2")));
        orientedBlock(CrafterModule.CRAFTER3.get(), frontBasedModel("crafter3", modLoc("block/machinecrafter3")));

        orientedBlock(TeleporterModule.DESTINATION_ANALYZER.get(), frontBasedModel("destination_analyzer", modLoc("block/machinedestinationanalyzer")));
        orientedBlock(TeleporterModule.DIALING_DEVICE.get(), frontBasedModel("dialing_device", modLoc("block/machinedialingdevice")));
        orientedBlock(TeleporterModule.MATTER_BOOSTER.get(), frontBasedModel("matter_booster", modLoc("block/machinematterbooster")));
        simpleBlock(TeleporterModule.MATTER_RECEIVER.get(), topBasedModel("matter_receiver", modLoc("block/machinereceiver")));
        simpleBlock(TeleporterModule.MATTER_TRANSMITTER.get(), topBasedModel("matter_transmitter", modLoc("block/machinetransmitter")));
        logicSlabBlock(TeleporterModule.SIMPLE_DIALER.get(), "simple_dialer", modLoc("block/machinesimpledialer"));

        logicSlabBlock(LogicBlockModule.ANALOG.get(), "analog", modLoc("block/logic/machineanalogtop"));
        logicSlabBlock(LogicBlockModule.COUNTER.get(), "counter", modLoc("block/logic/machinecountertop"));
        logicSlabBlock(LogicBlockModule.DIGIT.get(), "digit", modLoc("block/logic/machineoutput"));
        logicSlabBlock(LogicBlockModule.INVCHECKER.get(), "invchecker", modLoc("block/logic/machineinvchecker"));
        logicSlabBlock(LogicBlockModule.SENSOR.get(), "sensor", modLoc("block/logic/machinesensor"));
        logicSlabBlock(LogicBlockModule.SEQUENCER.get(), "sequencer", modLoc("block/logic/machinesequencertop"));
        logicSlabBlock(LogicBlockModule.LOGIC.get(), "logic", modLoc("block/logic/machinelogictop"));
        logicSlabBlock(LogicBlockModule.TIMER.get(), "timer", modLoc("block/logic/machinetimertop"));
        logicSlabBlock(LogicBlockModule.WIRE.get(), "wire", modLoc("block/logic/machinewiretop"));
        logicSlabBlock(LogicBlockModule.REDSTONE_RECEIVER.get(), "redstone_receiver", modLoc("block/logic/machineredstonereceiver"));
        logicSlabBlock(LogicBlockModule.REDSTONE_TRANSMITTER.get(), "redstone_transmitter", modLoc("block/logic/machineredstonetransmitter"));

        ModelFile screen = screenModel("screen", modLoc("block/screenframe_icon"));
        orientedBlock(ScreenModule.SCREEN.get(), screen);
        orientedBlock(ScreenModule.SCREEN_HIT.get(), screen);
        orientedBlock(ScreenModule.CREATIVE_SCREEN.get(), screenModel("creative_screen", modLoc("block/creative_screenframe_icon")));
        orientedBlock(ScreenModule.SCREEN_CONTROLLER.get(), frontBasedModel("screen_controller", modLoc("block/machinescreencontroller")));

        frontBasedModel("tank_inventory", modLoc("block/tank0"));

//        singleTextureBlock(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get(), BLOCK_FOLDER + "/environmental_controller", "block/machineenvironmentalcontroller");
        createEnvController();
    }

    public ModelFile screenModel(String modelName, ResourceLocation texture) {
        BlockModelBuilder model = models().getBuilder(BLOCK_FOLDER + "/" + modelName)
                .parent(models().getExistingFile(mcLoc("block")));
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

    private void createEnvController() {
        BlockModelBuilder model = models().getBuilder("block/environmental_controller");
        model.element().from(0f, 0f, 0f).to(16f, 16f, 16f).allFaces((direction, faceBuilder) -> {
            if (direction == Direction.UP) {
                faceBuilder.texture("#top");
            } else if (direction == Direction.DOWN) {
                faceBuilder.texture("#bottom");
            } else {
                faceBuilder.texture("#side");
            }
        }).end();

        model.element().from(0f, 3f, 0f).to(16f, 3f, 16f).face(Direction.UP).texture("#bottom").end();
        model.element().from(0f, 16f, 0f).to(16f, 16f, 16f).face(Direction.DOWN).texture("#top").end();

        model.element().from(0f, 0, 16f).to(16f, 16f, 16f).face(Direction.NORTH).texture("#side").end();
        model.element().from(0f, 0, 0f).to(16f, 16f, 0f).face(Direction.SOUTH).texture("#side").end();
        model.element().from(16f, 0, 0f).to(16f, 16f, 16f).face(Direction.WEST).texture("#side").end();
        model.element().from(0f, 0, 0f).to(0f, 16f, 16f).face(Direction.EAST).texture("#side").end();

        model
                .texture("top", new ResourceLocation(RFToolsBase.MODID, "block/base/machinetop"))
                .texture("side", modLoc("block/machineenvironmentalcontroller"))
                .texture("bottom", new ResourceLocation(RFToolsBase.MODID, "block/base/machinebottom"));

        MultiPartBlockStateBuilder bld = getMultipartBuilder(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get());
        bld.part().modelFile(model).addModel();
    }

}
