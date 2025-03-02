package mcjty.rftoolsutility.modules.spawner.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record SyringeData(ResourceLocation mob, int level) {

    public static final Codec<SyringeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("mob").forGetter(d -> Optional.ofNullable(d.mob)),
            Codec.INT.fieldOf("level").forGetter(d -> d.level)
    ).apply(instance, (mob, level) -> new SyringeData(mob.orElse(null), level)));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyringeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), d -> Optional.ofNullable(d.mob),
            ByteBufCodecs.INT, d -> d.level,
            (mob, level) -> new SyringeData(mob.orElse(null), level)
    );

    public static SyringeData createDefault() {
        return new SyringeData(null, 0);
    }

    public SyringeData withMob(ResourceLocation mob) {
        return new SyringeData(mob, level);
    }

    public SyringeData withLevel(int level) {
        return new SyringeData(mob, level);
    }
}