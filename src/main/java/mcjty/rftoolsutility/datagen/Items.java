package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsUtility.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(CrafterSetup.CRAFTER1.get(), "block/crafter1");
        parentedBlock(CrafterSetup.CRAFTER2.get(), "block/crafter2");
        parentedBlock(CrafterSetup.CRAFTER3.get(), "block/crafter3");
        parentedBlock(TeleporterSetup.DESTINATION_ANALYZER.get(), "block/destination_analyzer");
        parentedBlock(TeleporterSetup.DIALING_DEVICE.get(), "block/dialing_device");
        parentedBlock(TeleporterSetup.MATTER_BOOSTER.get(), "block/matter_booster");
        parentedBlock(TeleporterSetup.SIMPLE_DIALER.get(), "block/simple_dialer_0");
        parentedBlock(TeleporterSetup.MATTER_RECEIVER.get(), "block/matter_receiver");
        parentedBlock(TeleporterSetup.MATTER_TRANSMITTER.get(), "block/matter_transmitter");
        parentedBlock(ScreenSetup.SCREEN.get(), "block/screen");
        parentedBlock(ScreenSetup.CREATIVE_SCREEN.get(), "block/creative_screen");
        parentedBlock(ScreenSetup.SCREEN_CONTROLLER.get(), "block/screen_controller");
        parentedBlock(SpawnerSetup.MATTER_BEAMER.get(), "block/matter_beamer_on");
        parentedBlock(SpawnerSetup.SPAWNER.get(), "block/spawner");

        parentedBlock(LogicBlockSetup.ANALOG.get(), "block/analog_0");
        parentedBlock(LogicBlockSetup.COUNTER.get(), "block/counter_0");
        parentedBlock(LogicBlockSetup.DIGIT.get(), "block/digit_0");
        parentedBlock(LogicBlockSetup.INVCHECKER.get(), "block/invchecker_0");
        parentedBlock(LogicBlockSetup.SENSOR.get(), "block/sensor_0");
        parentedBlock(LogicBlockSetup.SEQUENCER.get(), "block/sequencer_0");
        parentedBlock(LogicBlockSetup.LOGIC.get(), "block/logic_0");
        parentedBlock(LogicBlockSetup.TIMER.get(), "block/timer_0");
        parentedBlock(LogicBlockSetup.WIRE.get(), "block/wire_0");
        parentedBlock(LogicBlockSetup.REDSTONE_RECEIVER.get(), "block/redstone_receiver_0");
        parentedBlock(LogicBlockSetup.REDSTONE_TRANSMITTER.get(), "block/redstone_transmitter_0");
    }

    @Override
    public String getName() {
        return "RFTools Utility Item Models";
    }
}
