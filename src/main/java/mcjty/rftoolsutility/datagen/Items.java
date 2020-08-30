package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsUtility.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(CrafterModule.CRAFTER1.get(), "block/crafter1");
        parentedBlock(CrafterModule.CRAFTER2.get(), "block/crafter2");
        parentedBlock(CrafterModule.CRAFTER3.get(), "block/crafter3");
        parentedBlock(TeleporterModule.DESTINATION_ANALYZER.get(), "block/destination_analyzer");
        parentedBlock(TeleporterModule.DIALING_DEVICE.get(), "block/dialing_device");
        parentedBlock(TeleporterModule.MATTER_BOOSTER.get(), "block/matter_booster");
        parentedBlock(TeleporterModule.SIMPLE_DIALER.get(), "block/simple_dialer_0");
        parentedBlock(TeleporterModule.MATTER_RECEIVER.get(), "block/matter_receiver");
        parentedBlock(TeleporterModule.MATTER_TRANSMITTER.get(), "block/matter_transmitter");
        parentedBlock(ScreenModule.SCREEN.get(), "block/screen");
        parentedBlock(ScreenModule.CREATIVE_SCREEN.get(), "block/creative_screen");
        parentedBlock(ScreenModule.SCREEN_CONTROLLER.get(), "block/screen_controller");
        parentedBlock(SpawnerModule.MATTER_BEAMER.get(), "block/matter_beamer_on");
        parentedBlock(SpawnerModule.SPAWNER.get(), "block/spawner");

        parentedBlock(LogicBlockModule.ANALOG.get(), "block/analog_0");
        parentedBlock(LogicBlockModule.COUNTER.get(), "block/counter_0");
        parentedBlock(LogicBlockModule.DIGIT.get(), "block/digit_0");
        parentedBlock(LogicBlockModule.INVCHECKER.get(), "block/invchecker_0");
        parentedBlock(LogicBlockModule.SENSOR.get(), "block/sensor_0");
        parentedBlock(LogicBlockModule.SEQUENCER.get(), "block/sequencer_0");
        parentedBlock(LogicBlockModule.LOGIC.get(), "block/logic_0");
        parentedBlock(LogicBlockModule.TIMER.get(), "block/timer_0");
        parentedBlock(LogicBlockModule.WIRE.get(), "block/wire_0");
        parentedBlock(LogicBlockModule.REDSTONE_RECEIVER.get(), "block/redstone_receiver_0");
        parentedBlock(LogicBlockModule.REDSTONE_TRANSMITTER.get(), "block/redstone_transmitter_0");
    }

    @Override
    public String getName() {
        return "RFTools Utility Item Models";
    }
}
