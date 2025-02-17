package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Set;

public record AnalogData(float mulEqual, float mulLess, float mulGreater, int addEqual, int addLess, int addGreater) {

    public static final Codec<AnalogData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("mul_eq").forGetter(AnalogData::mulEqual),
            Codec.FLOAT.fieldOf("mul_less").forGetter(AnalogData::mulLess),
            Codec.FLOAT.fieldOf("mul_greater").forGetter(AnalogData::mulGreater),
            Codec.INT.fieldOf("add_eq").forGetter(AnalogData::addEqual),
            Codec.INT.fieldOf("add_less").forGetter(AnalogData::addLess),
            Codec.INT.fieldOf("add_greater").forGetter(AnalogData::addGreater)
    ).apply(instance, AnalogData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalogData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, d -> d.mulEqual,
            ByteBufCodecs.FLOAT, d -> d.mulLess,
            ByteBufCodecs.FLOAT, d -> d.mulGreater,
            ByteBufCodecs.INT, d -> d.addEqual,
            ByteBufCodecs.INT, d -> d.addLess,
            ByteBufCodecs.INT, d -> d.addGreater,
            AnalogData::new
    );

    public static AnalogData createDefault() {
        return new AnalogData(1.0f, 1.0f, 1.0f, 0, 0, 0);
    }

    public AnalogData withMulEqual(float mulEqual) {
        return new AnalogData(mulEqual, mulLess, mulGreater, addEqual, addLess, addGreater);
    }

    public AnalogData withMulLess(float mulLess) {
        return new AnalogData(mulEqual, mulLess, mulGreater, addEqual, addLess, addGreater);
    }

    public AnalogData withMulGreater(float mulGreater) {
        return new AnalogData(mulEqual, mulLess, mulGreater, addEqual, addLess, addGreater);
    }

    public AnalogData withAddEqual(int addEqual) {
        return new AnalogData(mulEqual, mulLess, mulGreater, addEqual, addLess, addGreater);
    }

    public AnalogData withAddLess(int addLess) {
        return new AnalogData(mulEqual, mulLess, mulGreater, addEqual, addLess, addGreater);
    }

    public AnalogData withAddGreater(int addGreater) {
        return new AnalogData(mulEqual, mulLess, mulGreater, addEqual, addLess, addGreater);
    }
}