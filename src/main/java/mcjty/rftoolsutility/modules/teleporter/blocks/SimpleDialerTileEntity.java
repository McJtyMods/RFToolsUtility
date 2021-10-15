package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_SIMPLE_DIALER;

public class SimpleDialerTileEntity extends LogicTileEntity {

    private GlobalPos transmitter;
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
        setChanged();

        if (powerLevel > 0) {
            TeleportDestinations destinations = TeleportDestinations.get(level);
            BlockPos coordinate = null;
            RegistryKey<World> dim = World.OVERWORLD;
            if (receiver != null) {
                GlobalPos gc = destinations.getCoordinateForId(receiver);
                if (gc != null) {
                    coordinate = gc.pos();
                    dim = gc.dimension();
                }
            }

            int dial = TeleportationTools.dial(getLevel(), null, null, transmitter.pos(), transmitter.dimension(), coordinate, dim, onceMode);
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

    public GlobalPos getTransmitter() {
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
            transmitter = GlobalPos.of(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(transDim)), new BlockPos(info.getInt("transX"), info.getInt("transY"), info.getInt("transZ")));
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
            info.putInt("transX", transmitter.pos().getX());
            info.putInt("transY", transmitter.pos().getY());
            info.putInt("transZ", transmitter.pos().getZ());
            info.putString("transDim", transmitter.dimension().location().toString());
        }
        if (receiver != null) {
            info.putInt("receiver", receiver);
        }
        info.putBoolean("once", onceMode);
    }
}
