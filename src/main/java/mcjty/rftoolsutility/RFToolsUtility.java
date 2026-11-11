package mcjty.rftoolsutility;

import mcjty.lib.modules.Modules;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.ScreenModuleRegistry;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.items.porter.ChargedPorterItem;
import mcjty.rftoolsutility.setup.Config;
import mcjty.rftoolsutility.setup.ModSetup;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import mcjty.rftoolsutility.setup.Registration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Item;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Function;

public class RFToolsUtility implements ModInitializer {

    public static final String MODID = "rftoolsutility";
    public static final ModSetup setup = new ModSetup();
    public static RFToolsUtility instance;
    public final Modules modules = new Modules();
    public static final ScreenModuleRegistry screenModuleRegistry = new ScreenModuleRegistry();

    @Override
    public void onInitialize() {
        instance = this;
        modules.register(new CrafterModule());
        modules.register(new LogicBlockModule());
        modules.register(new ScreenModule());
        modules.register(new SpawnerModule());
        modules.register(new TankModule());
        modules.register(new TeleporterModule());
        modules.register(new EnvironmentalModule());
        Config.register(modules);
        Registration.register();
        RFToolsUtilityMessages.registerMessages();
        setup.init();
        modules.init();
        setup.registerBlockCapabilities(Registration.RBLOCKS);
        Registration.ITEMS.getRegister().getEntries().forEach(entry -> {
            if (entry.get() instanceof ChargedPorterItem porter) {
                EnergyStorage.ITEM.registerForItems((stack, context) -> porter.createEnergyStorage(context), porter);
            }
        });
    }

    public static <T extends Item> Function<Item.Properties, T> tab(Function<Item.Properties, T> factory) {
        return properties -> setup.tab(() -> factory.apply(properties)).get();
    }
}
