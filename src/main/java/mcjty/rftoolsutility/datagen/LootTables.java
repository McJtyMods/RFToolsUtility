package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(CrafterSetup.CRAFTER1.get(), createStandardTable("crafter1", CrafterSetup.CRAFTER1.get()));
        lootTables.put(CrafterSetup.CRAFTER2.get(), createStandardTable("crafter2", CrafterSetup.CRAFTER2.get()));
        lootTables.put(CrafterSetup.CRAFTER3.get(), createStandardTable("crafter3", CrafterSetup.CRAFTER3.get()));
        lootTables.put(TeleporterSetup.MATTER_RECEIVER.get(), createStandardTable("receiver", TeleporterSetup.MATTER_RECEIVER.get()));
        lootTables.put(TeleporterSetup.MATTER_TRANSMITTER.get(), createStandardTable("transmitter", TeleporterSetup.MATTER_TRANSMITTER.get()));
        lootTables.put(TeleporterSetup.MATTER_BOOSTER.get(), createStandardTable("booster", TeleporterSetup.MATTER_BOOSTER.get()));
        lootTables.put(TeleporterSetup.DIALING_DEVICE.get(), createStandardTable("dialing", TeleporterSetup.DIALING_DEVICE.get()));
        lootTables.put(TeleporterSetup.SIMPLE_DIALER.get(), createStandardTable("dialing", TeleporterSetup.SIMPLE_DIALER.get()));
        lootTables.put(TeleporterSetup.DESTINATION_ANALYZER.get(), createStandardTable("analyzer", TeleporterSetup.DESTINATION_ANALYZER.get()));
        lootTables.put(TankSetup.TANK.get(), createStandardTable("tank", TankSetup.TANK.get()));
        lootTables.put(ScreenSetup.SCREEN.get(), createStandardTable("screen", ScreenSetup.SCREEN.get()));
        lootTables.put(ScreenSetup.CREATIVE_SCREEN.get(), createStandardTable("creative_screen", ScreenSetup.CREATIVE_SCREEN.get()));
        lootTables.put(ScreenSetup.SCREEN_CONTROLLER.get(), createStandardTable("screen_controller", ScreenSetup.SCREEN_CONTROLLER.get()));
    }

    @Override
    public String getName() {
        return "RFToolsUtility LootTables";
    }
}
