package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.typed.Type;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.network.PacketSetChannelName;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;

public class RedstoneTransmitterTileEntity extends RedstoneChannelTileEntity {

    private int prevIn = -1;

    // Only for client-side TE
    private String channelName;

    @GuiValue
    public static final Value<?, String> VALUE_NAME = Value.<RedstoneTransmitterTileEntity, String>create("name", Type.STRING, RedstoneTransmitterTileEntity::getChannelName, RedstoneTransmitterTileEntity::setChannelName);

    @Cap(type = CapType.CONTAINER)
    private LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Redstone Receiver")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER, this))
            .setupSync(this));

    public void setChannelName(String v) {
        if (level.isClientSide) {
            channelName = v;
            RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketSetChannelName(worldPosition, channelName));
        } else {
            if (channel == -1) {
                getChannel(true);
            }
            RedstoneChannels channels = RedstoneChannels.getChannels(level);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
            ch.setName(v);
            channels.setDirty();
            setChanged();
        }
    }

    private String getChannelName() {
        if (level.isClientSide) {
            return channelName;
        } else {
            if (channel == -1) {
                return "";
            } else {
                RedstoneChannels channels = RedstoneChannels.getChannels(level);
                RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
                return ch.getName();
            }
        }
    }

    public RedstoneTransmitterTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TYPE_REDSTONE_TRANSMITTER.get(), pos, state);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        tagCompound.putString("channelName", getChannelName());
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        channelName = tagCompound.getString("channelName");
    }

    @Override
    public void setChannel(int channel) {
        super.setChannel(channel);
        update();
    }

    public void update() {
        if (level.isClientSide) {
            return;
        }

        if (channel == -1) {
            return;
        }

        if (powerLevel != prevIn) {
            prevIn = powerLevel;
            setChanged();
            RedstoneChannels channels = RedstoneChannels.getChannels(level);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
            ch.setValue(powerLevel);
            channels.save();
        }
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        if(tagCompound.contains("prevIn", 3 /* int */)) {
            prevIn = tagCompound.getInt("prevIn");
        } else {
            prevIn = tagCompound.getBoolean("prevIn") ? 15 : 0; // backwards compatibility
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putInt("prevIn", prevIn);
    }
}
