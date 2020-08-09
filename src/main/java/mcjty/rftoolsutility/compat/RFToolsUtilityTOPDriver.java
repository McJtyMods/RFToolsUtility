package mcjty.rftoolsutility.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.ITooltipInfo;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.spawner.MatterBeamerTileEntity;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.SimpleDialerTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
        Block block = blockState.getBlock();
        ResourceLocation id = block.getRegistryName();
        if (!drivers.containsKey(id)) {
            if (block instanceof ScreenBlock) {
                drivers.put(id, new ScreenDriver());
            } else if (block == TeleporterSetup.MATTER_RECEIVER.get()) {
                drivers.put(id, new MatterReceiverDriver());
            } else if (block == TeleporterSetup.MATTER_TRANSMITTER.get()) {
                drivers.put(id, new MatterTransmitterDriver());
            } else if (block == TeleporterSetup.SIMPLE_DIALER.get()) {
                drivers.put(id, new SimpleDialerDriver());
            } else if (block == LogicBlockSetup.COUNTER.get()) {
                drivers.put(id, new CounterDriver());
            } else if (block == LogicBlockSetup.INVCHECKER.get()) {
                drivers.put(id, new InvCheckerDriver());
            } else if (block == LogicBlockSetup.SENSOR.get()) {
                drivers.put(id, new SensorDriver());
            } else if (block == LogicBlockSetup.SEQUENCER.get()) {
                drivers.put(id, new SequencerDriver());
            } else if (block == LogicBlockSetup.TIMER.get()) {
                drivers.put(id, new TimerDriver());
            } else if (block == LogicBlockSetup.DIGIT.get()) {
                drivers.put(id, new DigitDriver());
            } else if (block == SpawnerSetup.MATTER_BEAMER.get()) {
                drivers.put(id, new MatterBeamerDriver());
            } else if (block instanceof RedstoneChannelBlock) {
                drivers.put(id, new RedstoneChannelDriver());
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

    public static class CounterDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (CounterTileEntity te) -> {
                probeInfo.text(TextFormatting.GREEN + "Current: " + te.getCurrent());
            });
        }
    }

    public static class InvCheckerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (InvCheckerTileEntity te) -> {
                boolean rc = te.checkOutput();
                probeInfo.text(TextFormatting.GREEN + "Output: " + TextFormatting.WHITE + (rc ? "on" : "off"));
            });
        }
    }

    public static class SensorDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (SensorTileEntity te) -> {
                SensorType sensorType = te.getSensorType();
                if (sensorType.isSupportsNumber()) {
                    probeInfo.text("Type: " + sensorType.getName() + " (" + te.getNumber() + ")");
                } else {
                    probeInfo.text("Type: " + sensorType.getName());
                }
                int blockCount = te.getAreaType().getBlockCount();
                if (blockCount == 1) {
                    probeInfo.text("Area: 1 block");
                } else if (blockCount < 0) {
                    probeInfo.text("Area: " + (-blockCount) + "x" + (-blockCount) + " blocks");
                } else {
                    probeInfo.text("Area: " + blockCount + " blocks");
                }
                boolean rc = te.checkSensor();
                probeInfo.text(TextFormatting.GREEN + "Output: " + TextFormatting.WHITE + (rc ? "on" : "off"));
            });
        }
    }

    public static class SequencerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (SequencerTileEntity te) -> {
                IProbeInfo horizontal = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                horizontal.text(TextFormatting.GREEN + "Mode: " + te.getMode().getDescription());
                TheOneProbeSupport.addSequenceElement(horizontal, te.getCycleBits(),
                        te.getCurrentStep(), mode == ProbeMode.EXTENDED);
                int currentStep = te.getCurrentStep();
                boolean rc = te.checkOutput();
                probeInfo.text(TextFormatting.GREEN + "Step: " + TextFormatting.WHITE + currentStep +
                        TextFormatting.GREEN + " -> " + TextFormatting.WHITE + (rc ? "on" : "off"));
            });
        }
    }

    public static class TimerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (TimerTileEntity te) -> {
                probeInfo.text(TextFormatting.GREEN + "Time: " + TextFormatting.WHITE + te.getTimer());
            });
        }
    }

    public static class DigitDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (DigitTileEntity te) -> {
                probeInfo.text(TextFormatting.GREEN + "Power: " + TextFormatting.WHITE + te.getPowerLevel());
            });
        }
    }

    public static class RedstoneChannelDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (RedstoneChannelTileEntity te) -> {
                int channel = te.getChannel(false);
                if (channel == -1) {
                    probeInfo.text(TextFormatting.YELLOW + "No channel set! Right-click with another");
                    probeInfo.text(TextFormatting.YELLOW + "transmitter or receiver to pair");
                } else {
                    RedstoneChannels.RedstoneChannel c = RedstoneChannels.getChannels(world).getChannel(channel);
                    if (c != null && !c.getName().isEmpty()) {
                        probeInfo.text(TextFormatting.GREEN + "Channel: " + channel + " (" + c.getName() + ")");
                    } else {
                        probeInfo.text(TextFormatting.GREEN + "Channel: " + channel);
                    }
                }
                if (te instanceof RedstoneReceiverTileEntity) {
                    probeInfo.text(TextFormatting.GREEN + "Analog mode: " + ((RedstoneReceiverTileEntity) te).getAnalog());
                    probeInfo.text(TextFormatting.GREEN + "Output: " + TextFormatting.WHITE + ((RedstoneReceiverTileEntity) te).checkOutput());
                }
            });
        }
    }

    public static class MatterBeamerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (MatterBeamerTileEntity te) -> {
                BlockPos coordinate = te.getDestination();
                if (coordinate == null) {
                    probeInfo.text(TextFormatting.RED + "Not connected to a spawner!");
                } else {
                    probeInfo.text(TextFormatting.GREEN + "Connected!");
                }
                probeInfo.text(TextFormatting.GREEN + "Power: " + TextFormatting.WHITE + te.getPowerLevel());
            });
        }
    }

}
