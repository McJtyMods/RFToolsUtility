package mcjty.rftoolsutility.modules.logic.blocks;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
        if (level.isClientSide()) {
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
        if (level.isClientSide()) {
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
        channelName = tag.getStringOr("channelName", "");
    }

    @Override
    public void setChannel(int channel) {
        super.setChannel(channel);
        update();
    }

    public void update() {
        if (level.isClientSide()) {
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
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        HolderLookup.Provider provider = input.lookup();
        if(tag.contains("prevIn")) {
            prevIn = tag.getIntOr("prevIn", 0);
        } else {
            prevIn = tag.getBooleanOr("prevIn", false) ? 15 : 0; // backwards compatibility
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        tag.putInt("prevIn", prevIn);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }
}
