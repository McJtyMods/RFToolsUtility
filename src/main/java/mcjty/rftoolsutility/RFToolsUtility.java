package mcjty.rftoolsutility;

import mcjty.lib.modules.Modules;
import mcjty.rftoolsbase.api.screens.IScreenModuleRegistry;
import mcjty.rftoolsbase.api.teleportation.ITeleportationManager;
import mcjty.rftoolsutility.apiimpl.teleportation.TeleportationManager;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.ScreenModuleRegistry;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.setup.ClientSetup;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.ModSetup;
import mcjty.rftoolsutility.setup.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;
import java.util.function.Supplier;

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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::initClient);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        event.getIMCStream().forEach(message -> {
            if ("getTeleportationManager".equals(message.getMethod())) {
                Supplier<Function<ITeleportationManager, Void>> supplier = message.getMessageSupplier();
                supplier.get().apply(new TeleportationManager());
            } else if ("getScreenModuleRegistry".equalsIgnoreCase(message.getMethod())) {
                Supplier<Function<IScreenModuleRegistry, Void>> supplier = message.getMessageSupplier();
                supplier.get().apply(screenModuleRegistry);
            }
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
