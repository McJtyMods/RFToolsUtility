package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.*;

public class RedstoneReceiverTileEntity extends RedstoneChannelTileEntity {

    @GuiValue
    private boolean analog = false;

    @Cap(type = CapType.CONTAINER)
    private LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Redstone Receiver")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_REDSTONE_RECEIVER, this))
            .setupSync(this));

    public RedstoneReceiverTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TYPE_REDSTONE_RECEIVER.get(), pos, state);
    }

    public static RedstoneChannelBlock createBlock() {
        return new RedstoneChannelBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/redstone_receiver"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("channel", RedstoneChannelBlock::getChannelString))
                .tileEntitySupplier(RedstoneReceiverTileEntity::new));
    }

    public boolean getAnalog() {
        return analog;
    }

    public void setAnalog(boolean analog) {
        this.analog = analog;
        setChanged();
    }

    public void tickServer() {
        support.setRedstoneState(this, checkOutput());
    }

    public int checkOutput() {
        if (channel != -1) {
            RedstoneChannels channels = RedstoneChannels.getChannels(level);
            RedstoneChannels.RedstoneChannel ch = channels.getChannel(channel);
            if (ch != null) {
                int newout = ch.getValue();
                if(!analog && newout > 0) {
                    return 15;
                }
                return newout;
            }
        }
        return 0;
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        support.setPowerOutput(tagCompound.getInt("rs"));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putInt("rs", support.getPowerOutput());
    }

    @Override
    public void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        analog = info.getBoolean("analog");
    }

    @Override
    public void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        info.putBoolean("analog", analog);
    }
}
