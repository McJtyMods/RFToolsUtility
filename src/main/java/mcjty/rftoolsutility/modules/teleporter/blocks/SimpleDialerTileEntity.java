package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterSetup.TYPE_SIMPLE_DIALER;

public class SimpleDialerTileEntity extends LogicTileEntity {

    private GlobalCoordinate transmitter;
    private Integer receiver;
    private boolean onceMode = false;

    private boolean prevIn = false;

    public SimpleDialerTileEntity() {
        super(TYPE_SIMPLE_DIALER.get());
    }

    public void update() {
        if (transmitter == null) {
            return;
        }

        if ((powerLevel > 0) == prevIn) {
            return;
        }

        prevIn = powerLevel > 0;
        markDirty();

        if (powerLevel > 0) {
            TeleportDestinations destinations = TeleportDestinations.get(world);
            BlockPos coordinate = null;
            DimensionId dim = DimensionId.overworld();
            if (receiver != null) {
                GlobalCoordinate gc = destinations.getCoordinateForId(receiver);
                if (gc != null) {
                    coordinate = gc.getCoordinate();
                    dim = gc.getDimension();
                }
            }

            int dial = TeleportationTools.dial(getWorld(), null, null, transmitter.getCoordinate(), transmitter.getDimension(), coordinate, dim, onceMode);
            if (dial != DialingDeviceTileEntity.DIAL_OK) {
                // @todo some way to report error
            }
        }
    }

    public boolean isOnceMode() {
        return onceMode;
    }

    public void setOnceMode(boolean onceMode) {
        this.onceMode = onceMode;
        markDirtyClient();
    }

    public GlobalCoordinate getTransmitter() {
        return transmitter;
    }

    public Integer getReceiver() {
        return receiver;
    }

    @Override
    protected void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        if (info.contains("transX")) {
            String transDim = info.getString("transDim");
            transmitter = new GlobalCoordinate(new BlockPos(info.getInt("transX"), info.getInt("transY"), info.getInt("transZ")),
                    DimensionId.fromResourceLocation(new ResourceLocation(transDim)));
        } else {
            transmitter = null;
        }
        if (info.contains("receiver")) {
            receiver = info.getInt("receiver");
        } else {
            receiver = null;
        }
        onceMode = info.getBoolean("once");
    }

    @Override
    protected void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        if (transmitter != null) {
            info.putInt("transX", transmitter.getCoordinate().getX());
            info.putInt("transY", transmitter.getCoordinate().getY());
            info.putInt("transZ", transmitter.getCoordinate().getZ());
            info.putString("transDim", transmitter.getDimension().getRegistryName().toString());
        }
        if (receiver != null) {
            info.putInt("receiver", receiver);
        }
        info.putBoolean("once", onceMode);
    }
}
