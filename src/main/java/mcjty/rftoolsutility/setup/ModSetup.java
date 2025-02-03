package mcjty.rftoolsutility.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.lib.varia.SpawnCanceler;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.RFToolsDimCompat;
import mcjty.rftoolsutility.compat.TheOneProbeSupport;
import mcjty.rftoolsutility.playerprops.FavoriteDestinationsProperties;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        NeoForge.EVENT_BUS.register(new ForgeEventHandlers());
        SpawnCanceler.registerSpawnCanceler(ForgeEventHandlers::onEntitySpawnEvent);

        e.enqueueWork(() -> {
            CommandHandler.registerCommands();
        });
        RFToolsUtility.screenModuleRegistry.registerBuiltins();
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeSupport::new);
        }
        RFToolsDimCompat.register();
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(FavoriteDestinationsProperties.class);
    }
}
