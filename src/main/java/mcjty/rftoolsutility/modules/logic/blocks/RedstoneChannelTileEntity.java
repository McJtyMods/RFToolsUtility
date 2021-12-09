package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class RedstoneChannelTileEntity extends LogicTileEntity {

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
