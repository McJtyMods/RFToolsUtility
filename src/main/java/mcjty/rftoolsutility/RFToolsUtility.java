package mcjty.rftoolsutility;

import mcjty.lib.base.ModBase;
import mcjty.rftoolsutility.config.Config;
import mcjty.rftoolsutility.setup.ModSetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(RFToolsUtility.MODID)
public class RFToolsUtility implements ModBase {

    public static final String MODID = "rftoolsutility";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public static RFToolsUtility instance;

    public RFToolsUtility() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> setup.init(event));

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("rftoolsutility-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("rftoolsutility-common.toml"));
    }

    @Override
    public String getModId() {
        return MODID;
    }

    public static final String SHIFT_MESSAGE = "<Press Shift>";


    @Override
    public void openManual(PlayerEntity player, int bookIndex, String page) {
    }
}
