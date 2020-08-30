package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.network.PacketSetChannelName;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RedstoneTransmitterTileEntity extends RedstoneChannelTileEntity {

    private int prevIn = -1;

    // Only for client-side TE
    private String channelName;

    public static final Key<String> VALUE_NAME = new Key<>("name", Type.STRING);

    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Redstone Receiver")
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER.get(), windowId, EmptyContainer.CONTAINER_FACTORY.get(), getPos(), RedstoneTransmitterTileEntity.this)));

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_NAME, this::getChannelName, this::setChannelName),
        };
    }

    public void setChannelName(String v) {
        if (world.isRemote) {
            channelName = v;
            RFToolsUtilityMessages.INSTANCE.sendToServer(new PacketSetChannelName(pos, channelName));
        } else {
            if (channel == -1) {
                getChannel(true);
            }
            RedstoneChannels channels = RedstoneChannels.getChannels(world);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
            ch.setName(v);
            channels.markDirty();
            markDirtyClient();
        }
    }

    private String getChannelName() {
        if (world.isRemote) {
            return channelName;
        } else {
            if (channel == -1) {
                return "";
            } else {
                RedstoneChannels channels = RedstoneChannels.getChannels(world);
                RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
                return ch.getName();
            }
        }
    }

    public RedstoneTransmitterTileEntity() {
        super(LogicBlockModule.TYPE_REDSTONE_TRANSMITTER.get());
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        super.writeClientDataToNBT(tagCompound);
        tagCompound.putString("channelName", getChannelName());
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        channelName = tagCompound.getString("channelName");
    }

    @Override
    public void setChannel(int channel) {
        super.setChannel(channel);
        update();
    }

    public void update() {
        if (world.isRemote) {
            return;
        }

        if (channel == -1) {
            return;
        }

        if (powerLevel != prevIn) {
            prevIn = powerLevel;
            markDirty();
            RedstoneChannels channels = RedstoneChannels.getChannels(world);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
            ch.setValue(powerLevel);
            channels.save();
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        if(tagCompound.contains("prevIn", 3 /* int */)) {
            prevIn = tagCompound.getInt("prevIn");
        } else {
            prevIn = tagCompound.getBoolean("prevIn") ? 15 : 0; // backwards compatibility
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        tagCompound.putInt("prevIn", prevIn);
        return tagCompound;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

}
