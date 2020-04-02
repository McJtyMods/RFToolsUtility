package mcjty.rftoolsutility;

import mcjty.lib.base.ModBase;
import mcjty.rftoolsutility.modules.screen.ScreenModuleRegistry;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.ModSetup;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsUtility.MODID)
public class RFToolsUtility implements ModBase {

    public static final String MODID = "rftoolsutility";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public static RFToolsUtility instance;
    public static ScreenModuleRegistry screenModuleRegistry = new ScreenModuleRegistry();

    public RFToolsUtility() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> setup.init(event));
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent event) -> setup.initClient(event));
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(PlayerEntity player, int bookIndex, String page) {
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(setup.getTab());
    }

}
