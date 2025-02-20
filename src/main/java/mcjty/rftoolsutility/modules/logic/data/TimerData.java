package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.modules.logic.tools.AreaType;
import mcjty.rftoolsutility.modules.logic.tools.GroupType;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TimerData(int delay, boolean redstonePauses) {

    public static final Codec<TimerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("delay").forGetter(TimerData::delay),
            Codec.BOOL.fieldOf("pauses").forGetter(TimerData::redstonePauses)
    ).apply(instance, TimerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TimerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TimerData::delay,
            ByteBufCodecs.BOOL, TimerData::redstonePauses,
            TimerData::new);

    public static TimerData createDefault() {
        return new TimerData(20, false);
    }

    public TimerData withDelay(int delay) {
        return new TimerData(delay, redstonePauses);
    }

    public TimerData withRedstonePauses(boolean redstonePauses) {
        return new TimerData(delay, redstonePauses);
    }
}
