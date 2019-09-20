package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
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
        lootTables.put(TeleporterSetup.MATTER_TRANSMITTER, createStandardTable("receiver", TeleporterSetup.MATTER_TRANSMITTER));
        lootTables.put(TeleporterSetup.MATTER_BOOSTER, createStandardTable("receiver", TeleporterSetup.MATTER_BOOSTER));
        lootTables.put(TeleporterSetup.DIALING_DEVICE, createStandardTable("receiver", TeleporterSetup.DIALING_DEVICE));
        lootTables.put(TeleporterSetup.SIMPLE_DIALER, createStandardTable("receiver", TeleporterSetup.SIMPLE_DIALER));
        lootTables.put(TeleporterSetup.DESTINATION_ANALYZER, createStandardTable("receiver", TeleporterSetup.DESTINATION_ANALYZER));
    }

    @Override
    public String getName() {
        return "RFToolsUtility LootTables";
    }
}
