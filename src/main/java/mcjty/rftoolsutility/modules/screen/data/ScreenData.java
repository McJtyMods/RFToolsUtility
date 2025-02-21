package mcjty.rftoolsutility.modules.screen.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// size: Size of screen (0 is normal, 1 is large, 2 is huge)
// trueTypeMode: 0 is default, -1 is disabled, 1 is truetype
public record ScreenData(int size, boolean transparent, int color, boolean bright, int trueTypeMode) {

    public static final Codec<ScreenData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("size").forGetter(ScreenData::size),
            Codec.BOOL.fieldOf("transparent").forGetter(ScreenData::transparent),
            Codec.INT.fieldOf("color").forGetter(ScreenData::color),
            Codec.BOOL.fieldOf("bright").forGetter(ScreenData::bright),
            Codec.INT.fieldOf("truetype").forGetter(ScreenData::trueTypeMode)
    ).apply(instance, ScreenData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ScreenData::size,
            ByteBufCodecs.BOOL, ScreenData::transparent,
            ByteBufCodecs.INT, ScreenData::color,
            ByteBufCodecs.BOOL, ScreenData::bright,
            ByteBufCodecs.INT, ScreenData::trueTypeMode,
            ScreenData::new);

    public static ScreenData createDefault() {
        return new ScreenData(0, false, 0, false, 0);
    }

    public ScreenData withSize(int size) {
        return new ScreenData(size, transparent, color, bright, trueTypeMode);
    }

    public ScreenData withTransparent(boolean transparent) {
        return new ScreenData(size, transparent, color, bright, trueTypeMode);
    }

    public ScreenData withColor(int color) {
        return new ScreenData(size, transparent, color, bright, trueTypeMode);
    }

    public ScreenData withBright(boolean bright) {
        return new ScreenData(size, transparent, color, bright, trueTypeMode);
    }

    public ScreenData withTrueTypeMode(int trueTypeMode) {
        return new ScreenData(size, transparent, color, bright, trueTypeMode);
    }
}
