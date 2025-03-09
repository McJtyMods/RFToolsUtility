package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.typed.Type;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.RedstoneChannelData;
import mcjty.rftoolsutility.modules.logic.data.RedstoneReceiverData;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.*;

public class RedstoneReceiverTileEntity extends RedstoneChannelTileEntity {

    @GuiValue
    public static final Value<RedstoneReceiverTileEntity, Boolean> VALUE_ANALOG = Value.create("analog", Type.BOOLEAN, RedstoneReceiverTileEntity::getAnalog, RedstoneReceiverTileEntity::setAnalog);

    @Cap(type = CapType.CONTAINER)
    private static final Function<RedstoneReceiverTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Redstone Receiver")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_REDSTONE_RECEIVER, be))
            .setupSync(be);

    public RedstoneReceiverTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.REDSTONE_RECEIVER.be().get(), pos, state);
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
        RedstoneReceiverData data = getData(LogicBlockModule.REDSTONERECEIVER_DATA);
        return data.analog();
    }

    public void setAnalog(boolean analog) {
        RedstoneReceiverData data = getData(LogicBlockModule.REDSTONERECEIVER_DATA);
        data = data.withAnalog(analog);
        setData(LogicBlockModule.REDSTONERECEIVER_DATA, data);
    }

    public void tickServer() {
        support.setRedstoneState(this, checkOutput());
    }

    public int checkOutput() {
        RedstoneChannelData cdata = getData(LogicBlockModule.REDSTONECHANNEL_DATA);
        if (cdata.channel() != -1) {
            RedstoneReceiverData data = getData(LogicBlockModule.REDSTONERECEIVER_DATA);
            RedstoneChannels channels = RedstoneChannels.getChannels(level);
            RedstoneChannels.RedstoneChannel ch = channels.getChannel(cdata.channel());
            if (ch != null) {
                int newout = ch.getValue();
                if (!data.analog() && newout > 0) {
                    return 15;
                }
                return newout;
            }
        }
        return 0;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        support.setPowerOutput(tag.getInt("rs"));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("rs", support.getPowerOutput());
    }
}
