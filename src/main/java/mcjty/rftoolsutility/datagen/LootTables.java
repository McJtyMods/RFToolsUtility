package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.mover.MoverModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;

import javax.annotation.Nonnull;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardTable(CrafterModule.CRAFTER1.get(), CrafterModule.TYPE_CRAFTER1.get());
        addStandardTable(CrafterModule.CRAFTER2.get(), CrafterModule.TYPE_CRAFTER2.get());
        addStandardTable(CrafterModule.CRAFTER3.get(), CrafterModule.TYPE_CRAFTER3.get());
        addStandardTable(TeleporterModule.MATTER_RECEIVER.get(), TeleporterModule.TYPE_MATTER_RECEIVER.get());
        addStandardTable(TeleporterModule.MATTER_TRANSMITTER.get(), TeleporterModule.TYPE_MATTER_TRANSMITTER.get());
        addSimpleTable(TeleporterModule.MATTER_BOOSTER.get());
        addStandardTable(TeleporterModule.DIALING_DEVICE.get(), TeleporterModule.TYPE_DIALING_DEVICE.get());
        addStandardTable(TeleporterModule.SIMPLE_DIALER.get(), TeleporterModule.TYPE_SIMPLE_DIALER.get());
        addSimpleTable(TeleporterModule.DESTINATION_ANALYZER.get());
        addStandardTable(TankModule.TANK.get(), TankModule.TYPE_TANK.get());
        addStandardTable(ScreenModule.SCREEN.get(), ScreenModule.TYPE_SCREEN.get());
        addStandardTable(ScreenModule.CREATIVE_SCREEN.get(), ScreenModule.TYPE_CREATIVE_SCREEN.get());
        addStandardTable(ScreenModule.SCREEN_CONTROLLER.get(), ScreenModule.TYPE_SCREEN_CONTROLLER.get());
        addStandardTable(SpawnerModule.MATTER_BEAMER.get(), SpawnerModule.TYPE_MATTER_BEAMER.get());
        addStandardTable(SpawnerModule.SPAWNER.get(), SpawnerModule.TYPE_SPAWNER.get());
        addStandardTable(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get(), EnvironmentalModule.TYPE_ENVIRONENTAL_CONTROLLER.get());
        addStandardTable(MoverModule.MOVER.get(), MoverModule.TYPE_MOVER.get());

        addStandardTable(LogicBlockModule.ANALOG.get(), LogicBlockModule.TYPE_ANALOG.get());
        addStandardTable(LogicBlockModule.COUNTER.get(), LogicBlockModule.TYPE_COUNTER.get());
        addSimpleTable(LogicBlockModule.DIGIT.get());
        addStandardTable(LogicBlockModule.INVCHECKER.get(), LogicBlockModule.TYPE_INVCHECKER.get());
        addStandardTable(LogicBlockModule.SENSOR.get(), LogicBlockModule.TYPE_SENSOR.get());
        addStandardTable(LogicBlockModule.SEQUENCER.get(), LogicBlockModule.TYPE_SEQUENCER.get());
        addStandardTable(LogicBlockModule.LOGIC.get(), LogicBlockModule.TYPE_LOGIC.get());
        addStandardTable(LogicBlockModule.TIMER.get(), LogicBlockModule.TYPE_TIMER.get());
        addSimpleTable(LogicBlockModule.WIRE.get());
        addStandardTable(LogicBlockModule.REDSTONE_RECEIVER.get(), LogicBlockModule.TYPE_REDSTONE_RECEIVER.get());
        addStandardTable(LogicBlockModule.REDSTONE_TRANSMITTER.get(), LogicBlockModule.TYPE_REDSTONE_TRANSMITTER.get());
    }

    @Nonnull
    @Override
    public String getName() {
        return "RFToolsUtility LootTables";
    }
}
