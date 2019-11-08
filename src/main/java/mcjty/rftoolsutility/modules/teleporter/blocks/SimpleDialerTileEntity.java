package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterSetup.TYPE_SIMPLE_DIALER;

public class SimpleDialerTileEntity extends LogicTileEntity {

    private GlobalCoordinate transmitter;
    private Integer receiver;
    private boolean onceMode = false;

    private boolean prevIn = false;

    public SimpleDialerTileEntity() {
        super(TYPE_SIMPLE_DIALER);
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
            DimensionType dim = DimensionType.OVERWORLD;
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

    // @todo 1.14 loot tables
    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        if (tagCompound.contains("transX")) {
            String transDim = tagCompound.getString("transDim");
            transmitter = new GlobalCoordinate(new BlockPos(tagCompound.getInt("transX"), tagCompound.getInt("transY"), tagCompound.getInt("transZ")), DimensionType.byName(new ResourceLocation(transDim)));
        } else {
            transmitter = null;
        }
        if (tagCompound.contains("receiver")) {
            receiver = tagCompound.getInt("receiver");
        } else {
            receiver = null;
        }
        onceMode = tagCompound.getBoolean("once");
    }

    // @todo 1.14 loot tables
    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        if (transmitter != null) {
            tagCompound.putInt("transX", transmitter.getCoordinate().getX());
            tagCompound.putInt("transY", transmitter.getCoordinate().getY());
            tagCompound.putInt("transZ", transmitter.getCoordinate().getZ());
            tagCompound.putString("transDim", transmitter.getDimension().getRegistryName().toString());
        }
        if (receiver != null) {
            tagCompound.putInt("receiver", receiver);
        }
        tagCompound.putBoolean("once", onceMode);
        return tagCompound;
    }
}
