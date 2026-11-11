package mcjty.rftoolsutility.setup;

import mcjty.lib.setup.DefaultModSetup;
import mcjty.lib.varia.SpawnCanceler;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.api.RFToolsUtilityPlugin;
import mcjty.rftoolsutility.apiimpl.teleportation.TeleportationManager;
import mcjty.rftoolsutility.compat.RFToolsDimCompat;
import net.fabricmc.loader.api.FabricLoader;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init() {
        super.init();
        ForgeEventHandlers.register();
        SpawnCanceler.registerSpawnCanceler(ForgeEventHandlers::onEntitySpawnEvent);
        CommandHandler.registerCommands();
        RFToolsUtility.screenModuleRegistry.registerBuiltins();
        FabricLoader.getInstance().getEntrypoints(RFToolsUtility.MODID, RFToolsUtilityPlugin.class)
                .forEach(plugin -> plugin.register(new TeleportationManager(), RFToolsUtility.screenModuleRegistry));
        RFToolsDimCompat.register();
    }

    @Override
    protected void setupModCompat() {
    }
}
