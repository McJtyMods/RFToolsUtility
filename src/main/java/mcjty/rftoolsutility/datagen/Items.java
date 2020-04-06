package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
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

        parentedBlock(LogicBlockSetup.ANALOG.get(), "block/analog");
        parentedBlock(LogicBlockSetup.COUNTER.get(), "block/counter");
        parentedBlock(LogicBlockSetup.DIGIT.get(), "block/digit");
    }

    @Override
    public String getName() {
        return "RFTools Utility Item Models";
    }
}
