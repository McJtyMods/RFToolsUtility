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
        lootTables.put(CrafterSetup.BLOCK_CRAFTER1, createStandardTable("crafter1", CrafterSetup.BLOCK_CRAFTER1));
        lootTables.put(CrafterSetup.BLOCK_CRAFTER2, createStandardTable("crafter2", CrafterSetup.BLOCK_CRAFTER2));
        lootTables.put(CrafterSetup.BLOCK_CRAFTER3, createStandardTable("crafter3", CrafterSetup.BLOCK_CRAFTER3));
        lootTables.put(TeleporterSetup.MATTER_RECEIVER, createStandardTable("receiver", TeleporterSetup.MATTER_RECEIVER));
        lootTables.put(TeleporterSetup.MATTER_TRANSMITTER, createStandardTable("transmitter", TeleporterSetup.MATTER_TRANSMITTER));
        lootTables.put(TeleporterSetup.MATTER_BOOSTER, createStandardTable("booster", TeleporterSetup.MATTER_BOOSTER));
        lootTables.put(TeleporterSetup.DIALING_DEVICE, createStandardTable("dialing", TeleporterSetup.DIALING_DEVICE));
        lootTables.put(TeleporterSetup.SIMPLE_DIALER, createStandardTable("dialing", TeleporterSetup.SIMPLE_DIALER));
        lootTables.put(TeleporterSetup.DESTINATION_ANALYZER, createStandardTable("analyzer", TeleporterSetup.DESTINATION_ANALYZER));
        lootTables.put(TankSetup.BLOCK_TANK, createStandardTable("tank", TankSetup.BLOCK_TANK));
        lootTables.put(ScreenSetup.SCREEN, createStandardTable("screen", ScreenSetup.SCREEN));
        lootTables.put(ScreenSetup.CREATIVE_SCREEN, createStandardTable("creative_screen", ScreenSetup.CREATIVE_SCREEN));
        lootTables.put(ScreenSetup.SCREEN_CONTROLLER, createStandardTable("screen_controller", ScreenSetup.SCREEN_CONTROLLER));
    }

    @Override
    public String getName() {
        return "RFToolsUtility LootTables";
    }
}
