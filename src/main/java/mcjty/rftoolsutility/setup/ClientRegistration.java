package mcjty.rftoolsutility.setup;


import mcjty.lib.gui.GenericGuiContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.crafter.client.GuiCrafter;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.screen.client.GuiScreen;
import mcjty.rftoolsutility.modules.screen.client.GuiScreenController;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.modules.tank.client.TankBakedModel;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import mcjty.rftoolsutility.modules.teleporter.client.GuiDialingDevice;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterReceiver;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterTransmitter;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

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

    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getBasePath().equals("textures")) {
            return;
        }
        for (int i = 0 ; i <= 8 ; i++) {
            event.addSprite(new ResourceLocation(RFToolsUtility.MODID, "block/tank" + i));
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        TankBakedModel model = new TankBakedModel();
        event.getModelRegistry().put(new ModelResourceLocation(new ResourceLocation(RFToolsUtility.MODID, "tank"), ""), model);
    }
}
