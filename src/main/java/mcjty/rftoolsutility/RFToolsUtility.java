package mcjty.rftoolsutility;

import mcjty.rftoolsutility.modules.screen.ScreenModuleRegistry;
import mcjty.rftoolsutility.setup.ClientSetup;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.ModSetup;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.item.Item;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsUtility.MODID)
public class RFToolsUtility {

    public static final String MODID = "rftoolsutility";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public static RFToolsUtility instance;
    public static ScreenModuleRegistry screenModuleRegistry = new ScreenModuleRegistry();

    public RFToolsUtility() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::modelInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(setup.getTab());
    }
}
