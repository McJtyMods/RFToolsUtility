package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(CrafterModule.CRAFTER1.get());
        addStandardTable(CrafterModule.CRAFTER2.get());
        addStandardTable(CrafterModule.CRAFTER3.get());
        addStandardTable(TeleporterModule.MATTER_RECEIVER.get());
        addStandardTable(TeleporterModule.MATTER_TRANSMITTER.get());
        addStandardTable(TeleporterModule.MATTER_BOOSTER.get());
        addStandardTable(TeleporterModule.DIALING_DEVICE.get());
        addStandardTable(TeleporterModule.SIMPLE_DIALER.get());
        addStandardTable(TeleporterModule.DESTINATION_ANALYZER.get());
        addStandardTable(TankModule.TANK.get());
        addStandardTable(ScreenModule.SCREEN.get());
        addStandardTable(ScreenModule.CREATIVE_SCREEN.get());
        addStandardTable(ScreenModule.SCREEN_CONTROLLER.get());
        addStandardTable(SpawnerModule.MATTER_BEAMER.get());
        addStandardTable(SpawnerModule.SPAWNER.get());
        addStandardTable(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get());

        addStandardTable(LogicBlockModule.ANALOG.get());
        addStandardTable(LogicBlockModule.COUNTER.get());
        addSimpleTable(LogicBlockModule.DIGIT.get());
        addStandardTable(LogicBlockModule.INVCHECKER.get());
        addStandardTable(LogicBlockModule.SENSOR.get());
        addStandardTable(LogicBlockModule.SEQUENCER.get());
        addStandardTable(LogicBlockModule.LOGIC.get());
        addStandardTable(LogicBlockModule.TIMER.get());
        addSimpleTable(LogicBlockModule.WIRE.get());
        addStandardTable(LogicBlockModule.REDSTONE_RECEIVER.get());
        addStandardTable(LogicBlockModule.REDSTONE_TRANSMITTER.get());
    }

    @Override
    public String getName() {
        return "RFToolsUtility LootTables";
    }
}
