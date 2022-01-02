package mcjty.rftoolsutility.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.TheOneProbeSupport;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.playerprops.BuffProperties;
import mcjty.rftoolsutility.playerprops.FavoriteDestinationsProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("rftoolsutility", () -> new ItemStack(TeleporterModule.CHARGED_PORTER.get()));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        e.enqueueWork(() -> {
            CommandHandler.registerCommands();
        });
        RFToolsUtilityMessages.registerMessages("rftoolsutility");
        RFToolsUtility.screenModuleRegistry.registerBuiltins();
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeSupport::new);
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(BuffProperties.class);
        event.register(FavoriteDestinationsProperties.class);
    }
}
