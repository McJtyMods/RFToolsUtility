package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.modules.logic.blocks.InvCheckerDamageMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CounterData(int counter, int current) {

    public static final Codec<CounterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("counter").forGetter(CounterData::counter),
            Codec.INT.fieldOf("current").forGetter(CounterData::current)
    ).apply(instance, CounterData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CounterData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, d -> d.counter,
            ByteBufCodecs.INT, d -> d.current,
            CounterData::new
    );

    public static CounterData createDefault() {
        return new CounterData(1, 0);
    }

    public CounterData withCounter(int counter) {
        return new CounterData(counter, current);
    }

    public CounterData withCurrent(int current) {
        return new CounterData(counter, current);
    }
}
