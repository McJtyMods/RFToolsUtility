package mcjty.rftoolsutility;

import mcjty.lib.modules.Modules;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.ScreenModuleRegistry;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.setup.ClientSetup;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.ModSetup;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsUtility.MODID)
public class RFToolsUtility {

    public static final String MODID = "rftoolsutility";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public static RFToolsUtility instance;
    private Modules modules = new Modules();
    public static ScreenModuleRegistry screenModuleRegistry = new ScreenModuleRegistry();

    public RFToolsUtility() {
        instance = this;
        setupModules();

        Config.register(modules);
        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SpawnerConfiguration::onLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SpawnerConfiguration::onReload);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::initClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
    }

    private void setupModules() {
        modules.register(new CrafterModule());
        modules.register(new LogicBlockModule());
        modules.register(new ScreenModule());
        modules.register(new SpawnerModule());
        modules.register(new TankModule());
        modules.register(new TeleporterModule());
    }
}
