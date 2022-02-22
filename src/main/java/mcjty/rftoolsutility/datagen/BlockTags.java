package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseBlockTagsProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BlockTags extends BaseBlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RFToolsUtility.MODID, helper);
    }

    @Override
    protected void addTags() {
        ironPickaxe(
                CrafterModule.CRAFTER1, CrafterModule.CRAFTER2, CrafterModule.CRAFTER3,
                EnvironmentalModule.ENVIRONENTAL_CONTROLLER,
                LogicBlockModule.COUNTER, LogicBlockModule.LOGIC, LogicBlockModule.INVCHECKER, LogicBlockModule.SENSOR, LogicBlockModule.SEQUENCER,
                LogicBlockModule.TIMER, LogicBlockModule.ANALOG, LogicBlockModule.DIGIT, LogicBlockModule.REDSTONE_RECEIVER, LogicBlockModule.REDSTONE_TRANSMITTER,
                LogicBlockModule.WIRE,
                ScreenModule.SCREEN, ScreenModule.CREATIVE_SCREEN,
                SpawnerModule.SPAWNER, SpawnerModule.MATTER_BEAMER,
                TankModule.TANK,
                TeleporterModule.MATTER_RECEIVER, TeleporterModule.MATTER_TRANSMITTER, TeleporterModule.MATTER_BOOSTER,
                TeleporterModule.DIALING_DEVICE, TeleporterModule.SIMPLE_DIALER
        );
    }

    @Override
    @Nonnull
    public String getName() {
        return "RFToolsUtility Tags";
    }
}
