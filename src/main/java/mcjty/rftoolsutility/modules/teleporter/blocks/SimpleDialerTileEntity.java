package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.data.SimpleDialerData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleDialerTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    private boolean prevIn = false;

    public SimpleDialerTileEntity(BlockPos pos, BlockState state) {
        super(TeleporterModule.SIMPLE_DIALER.be().get(), pos, state);
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
        SimpleDialerData data = getData(TeleporterModule.SIMPLEDIALER_DATA);
        GlobalPos transmitter = data.transmitter();
        if (transmitter.pos() == BlockPosTools.INVALID) {
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
            if (data.receiver() != -1) {
                GlobalPos gc = destinations.getCoordinateForId(data.receiver());
                if (gc != null) {
                    coordinate = gc.pos();
                    dim = gc.dimension();
                }
            }

            int dial = TeleportationTools.dial(getLevel(), null, null, transmitter.pos(), transmitter.dimension(), coordinate, dim, data.onceMode());
            if (dial != DialingDeviceTileEntity.DIAL_OK) {
                // @todo some way to report error
            }
        }
    }

    public boolean isOnceMode() {
        SimpleDialerData data = getData(TeleporterModule.SIMPLEDIALER_DATA);
        return data.onceMode();
    }

    public void setOnceMode(boolean onceMode) {
        SimpleDialerData data = getData(TeleporterModule.SIMPLEDIALER_DATA);
        data = data.withOnceMode(onceMode);
        setData(TeleporterModule.SIMPLEDIALER_DATA, data);
    }

    public GlobalPos getTransmitter() {
        SimpleDialerData data = getData(TeleporterModule.SIMPLEDIALER_DATA);
        return data.transmitter();
    }

    public int getReceiver() {
        SimpleDialerData data = getData(TeleporterModule.SIMPLEDIALER_DATA);
        return data.receiver();
    }
}
