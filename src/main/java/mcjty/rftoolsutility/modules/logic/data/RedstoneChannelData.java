package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RedstoneChannelData(int channel) {

    public static final Codec<RedstoneChannelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("channel").forGetter(RedstoneChannelData::channel)
    ).apply(instance, RedstoneChannelData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneChannelData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RedstoneChannelData::channel,
            RedstoneChannelData::new);

    public static RedstoneChannelData createDefault() {
        return new RedstoneChannelData(-1);
    }

    public RedstoneChannelData withChannel(int channel) {
        return new RedstoneChannelData(channel);
    }
}
