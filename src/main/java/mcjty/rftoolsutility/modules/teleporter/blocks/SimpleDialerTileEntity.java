package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_SIMPLE_DIALER;

public class SimpleDialerTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    private GlobalPos transmitter;
    private Integer receiver;
    private boolean onceMode = false;

    private boolean prevIn = false;

    public SimpleDialerTileEntity(BlockPos pos, BlockState state) {
        super(TYPE_SIMPLE_DIALER.get(), pos, state);
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
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
            ResourceKey<Level> dim = Level.OVERWORLD;
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
        setChanged();
    }

    public GlobalPos getTransmitter() {
        return transmitter;
    }

    public Integer getReceiver() {
        return receiver;
    }

    @Override
    protected void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        if (info.contains("transX")) {
            String transDim = info.getString("transDim");
            transmitter = GlobalPos.of(LevelTools.getId(transDim), new BlockPos(info.getInt("transX"), info.getInt("transY"), info.getInt("transZ")));
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
    protected void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
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
