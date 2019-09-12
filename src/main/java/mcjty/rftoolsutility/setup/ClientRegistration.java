package mcjty.rftoolsutility.setup;


import mcjty.lib.gui.GenericGuiContainer;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.blocks.crafter.CrafterSetup;
import mcjty.rftoolsutility.blocks.crafter.GuiCrafter;
import mcjty.rftoolsutility.blocks.teleporter.GuiDialingDevice;
import mcjty.rftoolsutility.blocks.teleporter.GuiMatterReceiver;
import mcjty.rftoolsutility.blocks.teleporter.GuiMatterTransmitter;
import mcjty.rftoolsutility.blocks.teleporter.TeleporterSetup;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RFToolsUtility.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        GenericGuiContainer.register(CrafterSetup.CONTAINER_CRAFTER, GuiCrafter::new);
        GenericGuiContainer.register(TeleporterSetup.CONTAINER_DIALING_DEVICE, GuiDialingDevice::new);
        GenericGuiContainer.register(TeleporterSetup.CONTAINER_MATTER_TRANSMITTER, GuiMatterTransmitter::new);
        GenericGuiContainer.register(TeleporterSetup.CONTAINER_MATTER_RECEIER, GuiMatterReceiver::new);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

//    @SubscribeEvent
//    public static void onTextureStitch(TextureStitchEvent.Pre event) {
//        Minecraft mc = Minecraft.getInstance();
//        for (SideType value : SideType.VALUES) {
//            if (value.getSideTexture() != null) {
//                for (Tier tier : Tier.VALUES) {
//                    ResourceLocation location = new ResourceLocation(value.getSideTexture() + tier.getSuffix());
//                    System.out.println("location = " + location);
//                    event.getMap().func_215244_a(mc.textureManager, mc.getResourceManager(),
//                            location, mc);
//                }
//            }
//            if (value.getUpDownTexture() != null) {
//                event.getMap().func_215244_a(mc.textureManager, mc.getResourceManager(),
//                        new ResourceLocation(value.getUpDownTexture()), mc);
//            }
//        }
//    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
//        PowerCellBakedModel model = new PowerCellBakedModel(DefaultVertexFormats.BLOCK);
//        Lists.newArrayList(ModBlocks.CELL1, ModBlocks.CELL2, ModBlocks.CELL3).stream()
//                .forEach(block -> {
//                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), ""), model);
//                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=false,upper=false"), model);
//                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=false,upper=true"), model);
//                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=true,upper=false"), model);
//                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=true,upper=true"), model);
//                });
    }
}
