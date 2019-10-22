package mcjty.rftoolsutility.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import mcjty.rftoolsutility.network.RFToolsUtilityMessages;
import mcjty.rftoolsutility.playerprops.BuffProperties;
import mcjty.rftoolsutility.playerprops.FavoriteDestinationsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("rftoolsutility", () -> new ItemStack(TeleporterSetup.CHARGED_PORTER));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        CommandHandler.registerCommands();
        setupCapabilities();
        RFToolsUtilityMessages.registerMessages("rftoolsutility");
        PacketHandler.registerMessageHandler(RFToolsUtility.MODID, RFToolsUtilityMessages.INSTANCE);
        RFToolsUtility.screenModuleRegistry.registerBuiltins();
    }

    public void initClient(FMLClientSetupEvent e) {
        TeleporterSetup.initClient();
        ScreenSetup.initClient();
        ClientCommandHandler.registerCommands();
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
//        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.compat.theoneprobe.TheOneProbeSupport");
    }

    private void setupCapabilities() {
        CapabilityManager.INSTANCE.register(BuffProperties.class, new Capability.IStorage<BuffProperties>() {
            @Override
            public INBT writeNBT(Capability<BuffProperties> capability, BuffProperties instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<BuffProperties> capability, BuffProperties instance, Direction side, INBT nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });

        CapabilityManager.INSTANCE.register(FavoriteDestinationsProperties.class, new Capability.IStorage<FavoriteDestinationsProperties>() {
            @Override
            public INBT writeNBT(Capability<FavoriteDestinationsProperties> capability, FavoriteDestinationsProperties instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<FavoriteDestinationsProperties> capability, FavoriteDestinationsProperties instance, Direction side, INBT nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

}
