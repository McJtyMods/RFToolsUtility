package mcjty.rftoolsutility.setup;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import mcjty.lib.modules.Modules;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public static final String CATEGORY_GENERAL = "general";

    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static void register(Modules modules) {
        setupGeneralConfig();
        modules.initConfig();

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        ModConfigEvents.reloading(RFToolsUtility.MODID)
                .register(config -> onConfigReload());

        ConfigRegistry.INSTANCE.register(RFToolsUtility.MODID, ModConfig.Type.CLIENT, CLIENT_CONFIG);
        ConfigRegistry.INSTANCE.register(RFToolsUtility.MODID, ModConfig.Type.SERVER, SERVER_CONFIG);
    }

    private static void setupGeneralConfig() {
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

//        PowerCellConfig.setup(COMMON_BUILDER);
//        CoalGeneratorConfig.setup(COMMON_BUILDER);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static void onConfigReload() {
        ScreenConfiguration.trueTypeFont = null;
    }
}
