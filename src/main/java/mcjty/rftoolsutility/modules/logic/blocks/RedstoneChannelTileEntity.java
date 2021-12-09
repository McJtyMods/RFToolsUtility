package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class RedstoneChannelTileEntity extends GenericTileEntity {

    protected final LogicSupport support = new LogicSupport();
    protected int channel = -1;

    public int getChannel(boolean initialize) {
        if(initialize && channel == -1) {
            RedstoneChannels redstoneChannels = RedstoneChannels.getChannels(level);
            setChannel(redstoneChannels.newChannel());
            redstoneChannels.save();
        }
        return channel;
    }

    public RedstoneChannelTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public void setChannel(int channel) {
        this.channel = channel;
        setChanged();
    }

    @Override
    public void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        channel = info.getInt("channel");
    }

    @Override
    public void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("channel", channel);
    }
}
