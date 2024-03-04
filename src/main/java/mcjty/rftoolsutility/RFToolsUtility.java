package mcjty.rftoolsutility;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import mcjty.rftoolsbase.api.screens.IScreenModuleRegistry;
import mcjty.rftoolsbase.api.teleportation.ITeleportationManager;
import mcjty.rftoolsutility.apiimpl.teleportation.TeleportationManager;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
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
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.neoforge.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.fml.loading.FMLEnvironment;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(RFToolsUtility.MODID)
public class RFToolsUtility {

    public static final String MODID = "rftoolsutility";

    @SuppressWarnings("PublicField")
    public static final ModSetup setup = new ModSetup();

    public static RFToolsUtility instance;
    private final Modules modules = new Modules();
    public static final ScreenModuleRegistry screenModuleRegistry = new ScreenModuleRegistry();

    public RFToolsUtility() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Dist dist = FMLEnvironment.dist;

        instance = this;
        setupModules(bus, dist);

        Config.register(bus, modules);
        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::processIMC);
        bus.addListener(setup::registerCapabilities);
        bus.addListener(this::onDataGen);

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
            MinecraftForge.EVENT_BUS.addListener(ClientSetup::renderGameOverlayEvent);
        }
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
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

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new CrafterModule());
        modules.register(new LogicBlockModule());
        modules.register(new ScreenModule());
        modules.register(new SpawnerModule());
        modules.register(new TankModule(bus, dist));
        modules.register(new TeleporterModule());
        modules.register(new EnvironmentalModule(bus, dist));
    }
}
