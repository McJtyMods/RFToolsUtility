package mcjty.rftoolsutility.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.ITooltipInfo;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.SimpleDialerTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RFToolsUtilityTOPDriver implements TOPDriver {

    public static final RFToolsUtilityTOPDriver DRIVER = new RFToolsUtilityTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = blockState.getBlock().getRegistryName();
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() instanceof ScreenBlock) {
                drivers.put(id, new ScreenDriver());
            } else if (blockState.getBlock() == TeleporterSetup.MATTER_RECEIVER.get()) {
                drivers.put(id, new MatterReceiverDriver());
            } else if (blockState.getBlock() == TeleporterSetup.MATTER_TRANSMITTER.get()) {
                drivers.put(id, new MatterTransmitterDriver());
            } else if (blockState.getBlock() == TeleporterSetup.SIMPLE_DIALER.get()) {
                drivers.put(id, new SimpleDialerDriver());
            } else {
                drivers.put(id, new DefaultDriver());
            }
        }
        TOPDriver driver = drivers.get(id);
        if (driver != null) {
            driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class DefaultDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    public static class ScreenDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (ScreenTileEntity te) -> {
                if (!te.isConnected() && te.isControllerNeeded()) {
                    probeInfo.text(TextFormatting.YELLOW + "[NOT CONNECTED]");
                }
                if (!te.isCreative()) {
                    boolean power = te.isPowerOn();
                    if (!power) {
                        probeInfo.text(TextFormatting.YELLOW + "[NO POWER]");
                    }
                    if (mode == ProbeMode.EXTENDED) {
                        int rfPerTick = te.getTotalRfPerTick();
                        probeInfo.text(TextFormatting.GREEN + (power ? "Consuming " : "Needs ") + rfPerTick + " RF/tick");
                    }
                }
                IScreenModule<?> module = te.getHoveringModule();
                if (module instanceof ITooltipInfo) {
                    List<String> info = ((ITooltipInfo) module).getInfo(world, te.getHoveringX(), te.getHoveringY());
                    for (String s : info) {
                        probeInfo.text(s);
                    }
                }
            });
        }
    }

    public static class MatterReceiverDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (MatterReceiverTileEntity te) -> {
                String name = te.getName();
                int id = te.getId();
                if (name == null || name.isEmpty()) {
                    probeInfo.text(TextFormatting.GREEN + (id == -1 ? "" : ("Id: " + id)));
                } else {
                    probeInfo.text(TextFormatting.GREEN + "Name: " + name + (id == -1 ? "" : (", Id: " + id)));
                }
            });
        }
    }

    public static class MatterTransmitterDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (MatterTransmitterTileEntity te) -> {
                probeInfo.text(TextFormatting.GREEN + "Name: " + te.getName());
                if (te.isDialed()) {
                    Integer teleportId = te.getTeleportId();
                    TeleportDestinations destinations = TeleportDestinations.get(world);
                    String name = "?";
                    if (teleportId != null) {
                        name = TeleportDestinations.getDestinationName(destinations, teleportId);
                    }
                    probeInfo.text(TextFormatting.YELLOW + "[DIALED to " + name + "]");
                }
                if (te.isOnce()) {
                    probeInfo.text(TextFormatting.YELLOW + "[ONCE]");
                }
            });
        }
    }

    public static class SimpleDialerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (SimpleDialerTileEntity te) -> {
                GlobalCoordinate trans = te.getTransmitter();
                if (trans != null) {
                    probeInfo.text(TextFormatting.GREEN + "Transmitter at: " + BlockPosTools.toString(trans.getCoordinate()) + " (dim " + trans.getDimension().getRegistryName().toString() + ")");
                }
                Integer receiver = te.getReceiver();
                if (receiver != null) {
                    probeInfo.text(TextFormatting.GREEN + "Receiver: " + receiver);
                }
                if (te.isOnceMode()) {
                    probeInfo.text(TextFormatting.GREEN + "Dial Once mode enabled");
                }
            });
        }
    }
}
