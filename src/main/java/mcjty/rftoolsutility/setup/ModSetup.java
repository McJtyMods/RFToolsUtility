package mcjty.rftoolsutility.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.TheOneProbeSupport;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TransmitterInfo;
import mcjty.rftoolsutility.playerprops.BuffProperties;
import mcjty.rftoolsutility.playerprops.FavoriteDestinationsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
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
            McJtyLib.registerCommandInfo(MatterTransmitterTileEntity.CMD_GETPLAYERS.getName(), String.class, buf -> buf.readUtf(32767), PacketBuffer::writeUtf);
            McJtyLib.registerCommandInfo(MatterReceiverTileEntity.CMD_GETPLAYERS.getName(), String.class, buf -> buf.readUtf(32767), PacketBuffer::writeUtf);
            McJtyLib.registerCommandInfo(EnvironmentalControllerTileEntity.CMD_GETPLAYERS.getName(), String.class, buf -> buf.readUtf(32767), PacketBuffer::writeUtf);
            McJtyLib.registerCommandInfo(DialingDeviceTileEntity.CMD_GETRECEIVERS.getName(), TeleportDestinationClientInfo.class, TeleportDestinationClientInfo::new, (buf, i) -> i.toBytes(buf));
            McJtyLib.registerCommandInfo(DialingDeviceTileEntity.CMD_GETTRANSMITTERS.getName(), TransmitterInfo.class, TransmitterInfo::new, (buf, i) -> i.toBytes(buf));
        });
        setupCapabilities();
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
