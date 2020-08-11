package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(CrafterSetup.CRAFTER1.get());
        addStandardTable(CrafterSetup.CRAFTER2.get());
        addStandardTable(CrafterSetup.CRAFTER3.get());
        addStandardTable(TeleporterSetup.MATTER_RECEIVER.get());
        addStandardTable(TeleporterSetup.MATTER_TRANSMITTER.get());
        addStandardTable(TeleporterSetup.MATTER_BOOSTER.get());
        addStandardTable(TeleporterSetup.DIALING_DEVICE.get());
        addStandardTable(TeleporterSetup.SIMPLE_DIALER.get());
        addStandardTable(TeleporterSetup.DESTINATION_ANALYZER.get());
        addStandardTable(TankSetup.TANK.get());
        addStandardTable(ScreenSetup.SCREEN.get());
        addStandardTable(ScreenSetup.CREATIVE_SCREEN.get());
        addStandardTable(ScreenSetup.SCREEN_CONTROLLER.get());
        addStandardTable(SpawnerSetup.MATTER_BEAMER.get());
        addStandardTable(SpawnerSetup.SPAWNER.get());

        addStandardTable(LogicBlockSetup.ANALOG.get());
        addStandardTable(LogicBlockSetup.COUNTER.get());
        addSimpleTable(LogicBlockSetup.DIGIT.get());
        addStandardTable(LogicBlockSetup.INVCHECKER.get());
        addStandardTable(LogicBlockSetup.SENSOR.get());
        addStandardTable(LogicBlockSetup.SEQUENCER.get());
        addStandardTable(LogicBlockSetup.LOGIC.get());
        addStandardTable(LogicBlockSetup.TIMER.get());
        addSimpleTable(LogicBlockSetup.WIRE.get());
        addStandardTable(LogicBlockSetup.REDSTONE_RECEIVER.get());
        addStandardTable(LogicBlockSetup.REDSTONE_TRANSMITTER.get());
    }

    @Override
    public String getName() {
        return "RFToolsUtility LootTables";
    }
}
