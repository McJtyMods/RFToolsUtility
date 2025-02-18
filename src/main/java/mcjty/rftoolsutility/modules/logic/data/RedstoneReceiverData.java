package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RedstoneReceiverData(boolean analog) {

    public static final Codec<RedstoneReceiverData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("analog").forGetter(RedstoneReceiverData::analog)
    ).apply(instance, RedstoneReceiverData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneReceiverData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RedstoneReceiverData::analog,
            RedstoneReceiverData::new);

    public static RedstoneReceiverData createDefault() {
        return new RedstoneReceiverData(false);
    }

    public RedstoneReceiverData withAnalog(boolean analog) {
        return new RedstoneReceiverData(analog);
    }
}
