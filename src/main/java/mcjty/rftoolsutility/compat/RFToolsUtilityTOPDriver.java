package mcjty.rftoolsutility.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.ITooltipInfo;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.blocks.*;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.blocks.MatterBeamerTileEntity;
import mcjty.rftoolsutility.modules.spawner.blocks.SpawnerTileEntity;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.SimpleDialerTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.theoneprobe.api.TextStyleClass.*;

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
            } else if (block == TeleporterModule.MATTER_RECEIVER.get()) {
                drivers.put(id, new MatterReceiverDriver());
            } else if (block == TeleporterModule.MATTER_TRANSMITTER.get()) {
                drivers.put(id, new MatterTransmitterDriver());
            } else if (block == TeleporterModule.SIMPLE_DIALER.get()) {
                drivers.put(id, new SimpleDialerDriver());
            } else if (block == LogicBlockModule.COUNTER.get()) {
                drivers.put(id, new CounterDriver());
            } else if (block == LogicBlockModule.INVCHECKER.get()) {
                drivers.put(id, new InvCheckerDriver());
            } else if (block == LogicBlockModule.SENSOR.get()) {
                drivers.put(id, new SensorDriver());
            } else if (block == LogicBlockModule.SEQUENCER.get()) {
                drivers.put(id, new SequencerDriver());
            } else if (block == LogicBlockModule.TIMER.get()) {
                drivers.put(id, new TimerDriver());
            } else if (block == LogicBlockModule.DIGIT.get()) {
                drivers.put(id, new DigitDriver());
            } else if (block == SpawnerModule.MATTER_BEAMER.get()) {
                drivers.put(id, new MatterBeamerDriver());
            } else if (block == SpawnerModule.SPAWNER.get()) {
                drivers.put(id, new SpawnerDriver());
            } else if (block == EnvironmentalModule.ENVIRONENTAL_CONTROLLER.get()) {
                drivers.put(id, new EnvironmentalDriver());
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
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (ScreenTileEntity te) -> {
                if (!te.isConnected() && te.isControllerNeeded()) {
                    probeInfo.text(CompoundText.create().style(WARNING).text("[NOT CONNECTED]"));
                }
                if (!te.isCreative()) {
                    boolean power = te.isPowerOn();
                    if (!power) {
                        probeInfo.text(CompoundText.create().style(WARNING).text("[NO POWER]"));
                    }
                    if (mode == ProbeMode.EXTENDED) {
                        int rfPerTick = te.getTotalRfPerTick();
                        probeInfo.text(CompoundText.createLabelInfo(power ? "Consuming " : "Needs ", rfPerTick + " RF/tick"));
                    }
                }
                IScreenModule<?> module = te.getHoveringModule();
                if (module instanceof ITooltipInfo) {
                    List<String> info = ((ITooltipInfo) module).getInfo(world, te.getHoveringX(), te.getHoveringY());
                    for (String s : info) {
                        probeInfo.text(CompoundText.create().text(s));
                    }
                }
            });
        }
    }

    public static class MatterReceiverDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (MatterReceiverTileEntity te) -> {
                String name = te.getName();
                int id = te.getId();
                if (name == null || name.isEmpty()) {
                    probeInfo.text(CompoundText.create().style(INFO).text((id == -1 ? "" : ("Id: " + id))));
                } else {
                    probeInfo.text(CompoundText.create().style(INFO).text("Name: " + name + (id == -1 ? "" : (", Id: " + id))));
                }
            });
        }
    }

    public static class MatterTransmitterDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (MatterTransmitterTileEntity te) -> {
                probeInfo.text(CompoundText.createLabelInfo("Name: ", te.getName()));
                if (te.isDialed()) {
                    Integer teleportId = te.getTeleportId();
                    TeleportDestinations destinations = TeleportDestinations.get(world);
                    String name = "?";
                    if (teleportId != null) {
                        name = TeleportDestinations.getDestinationName(destinations, teleportId);
                    }
                    probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("[DIALED to " + name + "]"));
                }
                if (te.isOnce()) {
                    probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("[ONCE]"));
                }
            });
        }
    }

    public static class SimpleDialerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (SimpleDialerTileEntity te) -> {
                GlobalPos trans = te.getTransmitter();
                if (trans != null) {
                    probeInfo.text(CompoundText.createLabelInfo("Transmitter at: ", BlockPosTools.toString(trans.pos()) + " (dim " + trans.dimension().location().toString() + ")"));
                }
                Integer receiver = te.getReceiver();
                if (receiver != null) {
                    probeInfo.text(CompoundText.createLabelInfo("Receiver: ", receiver));
                }
                if (te.isOnceMode()) {
                    probeInfo.text(CompoundText.create().style(INFO).text("Dial Once mode enabled"));
                }
            });
        }
    }

    public static class CounterDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (CounterTileEntity te) -> {
                probeInfo.text(CompoundText.createLabelInfo("Current: ", te.getCurrent()));
            });
        }
    }

    public static class InvCheckerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (InvCheckerTileEntity te) -> {
                boolean rc = te.checkOutput();
                probeInfo.text(CompoundText.createLabelInfo("Output: ", (rc ? "on" : "off")));
            });
        }
    }

    public static class SensorDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (SensorTileEntity te) -> {
                SensorType sensorType = te.getSensorType();
                if (sensorType.isSupportsNumber()) {
                    probeInfo.text(CompoundText.createLabelInfo("Type: ", sensorType.getName() + " (" + te.getNumber() + ")"));
                } else {
                    probeInfo.text(CompoundText.createLabelInfo("Type: ", sensorType.getName()));
                }
                int blockCount = te.getAreaType().getBlockCount();
                if (blockCount == 1) {
                    probeInfo.text(CompoundText.createLabelInfo("Area: ", "1 block"));
                } else if (blockCount < 0) {
                    probeInfo.text(CompoundText.createLabelInfo("Area: ", (-blockCount) + "x" + (-blockCount) + " blocks"));
                } else {
                    probeInfo.text(CompoundText.createLabelInfo("Area: ", blockCount + " blocks"));
                }
                boolean rc = te.checkSensor();
                probeInfo.text(CompoundText.createLabelInfo("Output: ", (rc ? "on" : "off")));
            });
        }
    }

    public static class SequencerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (SequencerTileEntity te) -> {
                IProbeInfo horizontal = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                horizontal.text(CompoundText.createLabelInfo("Mode: ", te.getMode().getName()));
                TheOneProbeSupport.addSequenceElement(horizontal, te.getCycleBits(),
                        te.getCurrentStep(), mode == ProbeMode.EXTENDED);
                int currentStep = te.getCurrentStep();
                boolean rc = te.checkOutput();
                probeInfo.text(CompoundText.create().style(LABEL).text("Step: ").style(INFO).text(String.valueOf(currentStep))
                        .style(LABEL).text(" -> ").style(INFO).text((rc ? "on" : "off")));
            });
        }
    }

    public static class TimerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (TimerTileEntity te) -> {
                probeInfo.text(CompoundText.createLabelInfo("Time: ", te.getTimer()));
            });
        }
    }

    public static class DigitDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (DigitTileEntity te) -> {
                probeInfo.text(CompoundText.createLabelInfo("Power: ", te.getPowerLevel()));
            });
        }
    }

    public static class RedstoneChannelDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (RedstoneChannelTileEntity te) -> {
                int channel = te.getChannel(false);
                if (channel == -1) {
                    probeInfo.text(CompoundText.create().style(WARNING).text("No channel set! Right-click with another"));
                    probeInfo.text(CompoundText.create().style(WARNING).text("transmitter or receiver to pair"));
                } else {
                    RedstoneChannels.RedstoneChannel c = RedstoneChannels.getChannels(world).getChannel(channel);
                    if (c != null && !c.getName().isEmpty()) {
                        probeInfo.text(CompoundText.createLabelInfo("Channel: ", channel + " (" + c.getName() + ")"));
                    } else {
                        probeInfo.text(CompoundText.createLabelInfo("Channel: ", channel));
                    }
                }
                if (te instanceof RedstoneReceiverTileEntity) {
                    probeInfo.text(CompoundText.createLabelInfo("Analog mode: ", ((RedstoneReceiverTileEntity) te).getAnalog()));
                    probeInfo.text(CompoundText.createLabelInfo("Output: ", ((RedstoneReceiverTileEntity) te).checkOutput()));
                }
            });
        }
    }

    public static class MatterBeamerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (MatterBeamerTileEntity te) -> {
                BlockPos coordinate = te.getDestination();
                if (coordinate == null) {
                    probeInfo.text(CompoundText.create().style(ERROR).text("Not connected to a spawner!"));
                } else {
                    probeInfo.text(CompoundText.create().style(INFO).text("Connected!"));
                }
                probeInfo.text(CompoundText.createLabelInfo("Power: ", te.getPowerLevel()));
            });
        }
    }

    public static class SpawnerDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (SpawnerTileEntity te) -> {
                float[] matter = te.getMatter();
                DecimalFormat fmt = new DecimalFormat("#.##");
                fmt.setRoundingMode(RoundingMode.DOWN);
                probeInfo.text(CompoundText.createLabelInfo("Key Matter: ", fmt.format(matter[0])));
                probeInfo.text(CompoundText.createLabelInfo("Bulk Matter: ", fmt.format(matter[1])));
                probeInfo.text(CompoundText.createLabelInfo("Living Matter: ", fmt.format(matter[2])));
            });
        }
    }

    public static class EnvironmentalDriver extends DefaultDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (EnvironmentalControllerTileEntity te) -> {
                int rfPerTick = te.getTotalRfPerTick();
                int volume = te.getVolume();
                if (te.isActive()) {
                    probeInfo.text(TextFormatting.GREEN + "Active " + rfPerTick + " RF/tick (#" + volume + ")");
                } else {
                    probeInfo.text(TextFormatting.GREEN + "Inactive (#" + volume + ")");
                }
                int radius = te.getRadius();
                int miny = te.getMiny();
                int maxy = te.getMaxy();
                probeInfo.text(TextFormatting.GREEN + "Area: radius " + radius + " (" + miny + "/" + maxy + ")");
            });
        }
    }

}
