package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.RedstoneChannelData;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RedstoneChannelTileEntity extends GenericTileEntity {

    protected final LogicSupport support = new LogicSupport();

    public int getChannel(boolean initialize) {
        RedstoneChannelData data = getData(LogicBlockModule.REDSTONECHANNEL_DATA);
        int channel = data.channel();
        if (initialize && channel == -1) {
            RedstoneChannels redstoneChannels = RedstoneChannels.getChannels(level);
            channel = redstoneChannels.newChannel();
            data = data.withChannel(channel);
            setData(LogicBlockModule.REDSTONECHANNEL_DATA, data);
            redstoneChannels.save();
        }
        return channel;
    }

    public RedstoneChannelTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public int getChannel() {
        return getData(LogicBlockModule.REDSTONECHANNEL_DATA).channel();
    }

    public void setChannel(int channel) {
        RedstoneChannelData data = getData(LogicBlockModule.REDSTONECHANNEL_DATA);
        data = data.withChannel(channel);
        setData(LogicBlockModule.REDSTONECHANNEL_DATA, data);
    }

    @ServerCommand
    public static final Command<?> CMD_RESET = Command.<RedstoneChannelTileEntity>create("reset", (te, player, params) -> te.setChannel(-1));
}
