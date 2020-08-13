package mcjty.rftoolsutility.setup;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import mcjty.rftoolsutility.modules.crafter.CrafterConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.tank.TankConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    static {
        setupGeneralConfig();
        CrafterConfiguration.init(SERVER_BUILDER, CLIENT_BUILDER);
        TeleportConfiguration.init(SERVER_BUILDER, CLIENT_BUILDER);
        TankConfiguration.init(SERVER_BUILDER, CLIENT_BUILDER);
        ScreenConfiguration.init(SERVER_BUILDER, CLIENT_BUILDER);
        SpawnerConfiguration.init(SERVER_BUILDER, CLIENT_BUILDER);

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneralConfig() {
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

//        PowerCellConfig.setup(COMMON_BUILDER);
//        CoalGeneratorConfig.setup(COMMON_BUILDER);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}