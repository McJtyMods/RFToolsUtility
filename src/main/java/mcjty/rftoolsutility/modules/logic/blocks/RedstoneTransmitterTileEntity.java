package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.network.PacketSetChannelName;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;

public class RedstoneTransmitterTileEntity extends RedstoneChannelTileEntity {

    private int prevIn = -1;

    // Only for client-side TE
    private String channelName;

    public static final Key<String> VALUE_NAME = new Key<>("name", Type.STRING);

    @Cap(type = CapType.CONTAINER)
    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Redstone Receiver")
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockModule.CONTAINER_REDSTONE_TRANSMITTER.get(), windowId, ContainerFactory.EMPTY.get(), getBlockPos(), RedstoneTransmitterTileEntity.this)));

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_NAME, this::getChannelName, this::setChannelName),
        };
    }

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

    public RedstoneTransmitterTileEntity() {
        super(LogicBlockModule.TYPE_REDSTONE_TRANSMITTER.get());
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
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
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        if(tagCompound.contains("prevIn", 3 /* int */)) {
            prevIn = tagCompound.getInt("prevIn");
        } else {
            prevIn = tagCompound.getBoolean("prevIn") ? 15 : 0; // backwards compatibility
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putInt("prevIn", prevIn);
        return tagCompound;
    }
}
