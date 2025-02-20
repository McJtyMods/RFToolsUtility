package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record RedstoneInformationData(Set<Integer> channels) {

    public static final Codec<RedstoneInformationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf().fieldOf("channels").forGetter(data -> new ArrayList<>(data.channels()))
    ).apply(instance, channels -> new RedstoneInformationData(Set.copyOf(channels))));

    public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneInformationData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.collection(HashSet::new)), RedstoneInformationData::channels,
            RedstoneInformationData::new);

    public static final RedstoneInformationData DEFAULT = new RedstoneInformationData(new HashSet<>());

    public boolean hasChannel(int channel) {
        return channels.contains(channel);
    }

    public RedstoneInformationData addChannel(int channel) {
        Set<Integer> newChannels = new HashSet<>(channels);
        newChannels.add(channel);
        return new RedstoneInformationData(newChannels);
    }

    public RedstoneInformationData removeChannel(int channel) {
        Set<Integer> newChannels = new HashSet<>(channels);
        newChannels.remove(channel);
        return new RedstoneInformationData(newChannels);
    }
}
