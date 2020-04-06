package mcjty.rftoolsutility.setup;


import mcjty.lib.gui.GenericGuiContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.logic.analog.GuiAnalog;
import mcjty.rftoolsutility.modules.logic.counter.GuiCounter;
import mcjty.rftoolsutility.modules.logic.invchecker.GuiInvChecker;
import mcjty.rftoolsutility.modules.logic.sensor.GuiSensor;
import mcjty.rftoolsutility.modules.logic.sequencer.GuiSequencer;
import mcjty.rftoolsutility.modules.logic.threelogic.GuiThreeLogic;
import mcjty.rftoolsutility.modules.logic.timer.GuiTimer;
import mcjty.rftoolsutility.modules.logic.wireless.GuiRedstoneReceiver;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.screen.client.GuiScreen;
import mcjty.rftoolsutility.modules.screen.client.GuiScreenController;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.modules.tank.client.TankModelLoader;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import mcjty.rftoolsutility.modules.teleporter.client.GuiDialingDevice;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterReceiver;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterTransmitter;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static mcjty.rftoolsutility.modules.teleporter.client.BeamRenderer.*;

@Mod.EventBusSubscriber(modid = RFToolsUtility.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        GenericGuiContainer.register(CrafterSetup.CONTAINER_CRAFTER.get(), GuiCrafter::new);
        GenericGuiContainer.register(TeleporterSetup.CONTAINER_DIALING_DEVICE.get(), GuiDialingDevice::new);
        GenericGuiContainer.register(TeleporterSetup.CONTAINER_MATTER_TRANSMITTER.get(), GuiMatterTransmitter::new);
        GenericGuiContainer.register(TeleporterSetup.CONTAINER_MATTER_RECEIVER.get(), GuiMatterReceiver::new);
        GenericGuiContainer.register(TankSetup.CONTAINER_TANK.get(), GuiTank::new);
        GenericGuiContainer.register(ScreenSetup.CONTAINER_SCREEN.get(), GuiScreen::new);
        GenericGuiContainer.register(ScreenSetup.CONTAINER_SCREEN_CONTROLLER.get(), GuiScreenController::new);

        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_ANALOG.get(), GuiAnalog::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_COUNTER.get(), GuiCounter::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_INVCHECKER.get(), GuiInvChecker::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_SENSOR.get(), GuiSensor::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_SEQUENCER.get(), GuiSequencer::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_LOGIC.get(), GuiThreeLogic::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_TIMER.get(), GuiTimer::new);
        GenericGuiContainer.register(LogicBlockSetup.CONTAINER_REDSTONE_RECEIVER.get(), GuiRedstoneReceiver::new);

        ModelLoaderRegistry.registerLoader(new ResourceLocation(RFToolsUtility.MODID, "tankloader"), new TankModelLoader());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }
        event.addSprite(BEAM_OK);
        event.addSprite(BEAM_WARN);
        event.addSprite(BEAM_UNKNOWN);
    }

//    @SubscribeEvent
//    public static void onModelBake(ModelBakeEvent event) {
//        TankBakedModel model = new TankBakedModel();
//        event.getModelRegistry().put(new ModelResourceLocation(new ResourceLocation(RFToolsUtility.MODID, "tank"), ""), model);
//    }
}
