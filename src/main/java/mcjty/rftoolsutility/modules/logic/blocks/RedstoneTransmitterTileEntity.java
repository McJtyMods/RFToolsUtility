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
import net.minecraft.core.HolderLookup;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;

public class RedstoneTransmitterTileEntity extends RedstoneChannelTileEntity {

    private int prevIn = -1;

    // Only for client-side TE
    private String channelName;

    @GuiValue
    public static final Value<?, String> VALUE_NAME = Value.<RedstoneTransmitterTileEntity, String>create("name", Type.STRING, RedstoneTransmitterTileEntity::getChannelName, RedstoneTransmitterTileEntity::setChannelName);

    @Cap(type = CapType.CONTAINER)
    private static final Function<RedstoneTransmitterTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Redstone Receiver")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER, be))
            .setupSync(be);

    public void setChannelName(String v) {
        if (level.isClientSide) {
            channelName = v;
            RFToolsUtilityMessages.sendToServer(PacketSetChannelName.create(worldPosition, channelName));
        } else {
            if (getChannel() == -1) {
                getChannel(true);
            }
            RedstoneChannels channels = RedstoneChannels.getChannels(level);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(getChannel());
            ch.setName(v);
            channels.setDirty();
            setChanged();
        }
    }

    private String getChannelName() {
        if (level.isClientSide) {
            return channelName;
        } else {
            if (getChannel() == -1) {
                return "";
            } else {
                RedstoneChannels channels = RedstoneChannels.getChannels(level);
                RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(getChannel());
                return ch.getName();
            }
        }
    }

    public RedstoneTransmitterTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.REDSTONE_TRANSMITTER.be().get(), pos, state);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putString("channelName", getChannelName());
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        channelName = tag.getString("channelName");
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

        if (getChannel() == -1) {
            return;
        }

        if (powerLevel != prevIn) {
            prevIn = powerLevel;
            setChanged();
            RedstoneChannels channels = RedstoneChannels.getChannels(level);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(getChannel());
            ch.setValue(powerLevel);
            channels.save();
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if(tag.contains("prevIn", 3 /* int */)) {
            prevIn = tag.getInt("prevIn");
        } else {
            prevIn = tag.getBoolean("prevIn") ? 15 : 0; // backwards compatibility
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("prevIn", prevIn);
    }
}
