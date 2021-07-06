package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.items.*;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolsutility.setup.Registration.ITEMS;

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
        parentedBlock(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get(), "block/environmental_controller");

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

        parentedBlock(TankModule.TANK.get(), "block/tank_inventory");

        itemGenerated(EnvironmentalModule.MODULE_TEMPLATE.get(), "item/envmodules/moduletemplate");
        itemGenerated(EnvironmentalModule.MODULEPLUS_TEMPLATE.get(), "item/envmodules/moduletemplateplus");

        itemGenerated(EnvironmentalModule.REGENERATION_MODULE.get(), "item/envmodules/regenerationmoduleitem");
        itemGenerated(EnvironmentalModule.REGENERATIONPLUS_MODULE.get(), "item/envmodules/regenerationplusmoduleitem");
        itemGenerated(EnvironmentalModule.SPEED_MODULE.get(), "item/envmodules/speedmoduleitem");
        itemGenerated(EnvironmentalModule.SPEEDPLUS_MODULE.get(), "item/envmodules/speedplusmoduleitem");
        itemGenerated(EnvironmentalModule.HASTE_MODULE.get(), "item/envmodules/hastemoduleitem");
        itemGenerated(EnvironmentalModule.HASTEPLUS_MODULE.get(), "item/envmodules/hasteplusmoduleitem");
        itemGenerated(EnvironmentalModule.SATURATION_MODULE.get(), "item/envmodules/saturationmoduleitem");
        itemGenerated(EnvironmentalModule.SATURATIONPLUS_MODULE.get(), "item/envmodules/saturationplusmoduleitem");
        itemGenerated(EnvironmentalModule.FEATHERFALLING_MODULE.get(), "item/envmodules/featherfallingmoduleitem");
        itemGenerated(EnvironmentalModule.FEATHERFALLINGPLUS_MODULE.get(), "item/envmodules/featherfallingplusmoduleitem");
        itemGenerated(EnvironmentalModule.FLIGHT_MODULE.get(), "item/envmodules/flightmoduleitem");
        itemGenerated(EnvironmentalModule.PEACEFUL_MODULE.get(), "item/envmodules/peacefulmoduleitem");
        itemGenerated(EnvironmentalModule.WATERBREATHING_MODULE.get(), "item/envmodules/waterbreathingmoduleitem");
        itemGenerated(EnvironmentalModule.NIGHTVISION_MODULE.get(), "item/envmodules/nightvisionmoduleitem");
        itemGenerated(EnvironmentalModule.GLOWING_MODULE.get(), "item/envmodules/glowingmoduleitem");
        itemGenerated(EnvironmentalModule.LUCK_MODULE.get(), "item/envmodules/luckmoduleitem");
        itemGenerated(EnvironmentalModule.NOTELEPORT_MODULE.get(), "item/envmodules/noteleportmoduleitem");
        itemGenerated(EnvironmentalModule.BLINDNESS_MODULE.get(), "item/envmodules/blindnessmoduleitem");
        itemGenerated(EnvironmentalModule.WEAKNESS_MODULE.get(), "item/envmodules/weaknessmoduleitem");
        itemGenerated(EnvironmentalModule.POISON_MODULE.get(), "item/envmodules/poisonmoduleitem");
        itemGenerated(EnvironmentalModule.SLOWNESS_MODULE.get(), "item/envmodules/slownessmoduleitem");
    }

    @Override
    public String getName() {
        return "RFTools Utility Item Models";
    }
}
