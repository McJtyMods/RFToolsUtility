package mcjty.rftoolsutility.modules.spawner.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record SpawnerData(float matter0, float matter1, float matter2, ResourceLocation mob) {

    public static final Codec<SpawnerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("matter0").forGetter(d -> d.matter0),
            Codec.FLOAT.fieldOf("matter1").forGetter(d -> d.matter1),
            Codec.FLOAT.fieldOf("matter2").forGetter(d -> d.matter2),
            ResourceLocation.CODEC.optionalFieldOf("mob").forGetter(d -> Optional.ofNullable(d.mob))
    ).apply(instance, (m0, m1, m2, mob) -> new SpawnerData(m0, m1, m2, mob.orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, d -> d.matter0,
            ByteBufCodecs.FLOAT, d -> d.matter1,
            ByteBufCodecs.FLOAT, d -> d.matter2,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), d -> Optional.ofNullable(d.mob),
            (m0, m1, m2, mob) -> new SpawnerData(m0, m1, m2, mob.orElse(null))
    );

    public static SpawnerData createDefault() {
        return new SpawnerData(0, 0, 0, null);
    }

    public SpawnerData withMatter0(float matter0) {
        return new SpawnerData(matter0, matter1, matter2, mob);
    }

    public SpawnerData withMatter1(float matter1) {
        return new SpawnerData(matter0, matter1, matter2, mob);
    }

    public SpawnerData withMatter2(float matter2) {
        return new SpawnerData(matter0, matter1, matter2, mob);
    }

    public SpawnerData withMob(ResourceLocation mob) {
        return new SpawnerData(matter0, matter1, matter2, mob);
    }

    public float getMatter(int index) {
        return switch (index) {
            case 0 -> matter0;
            case 1 -> matter1;
            case 2 -> matter2;
            default -> 0;
        };
    }

    public SpawnerData withMatter(int index, float matter) {
        return switch (index) {
            case 0 -> new SpawnerData(matter, matter1, matter2, mob);
            case 1 -> new SpawnerData(matter0, matter, matter2, mob);
            case 2 -> new SpawnerData(matter0, matter1, matter, mob);
            default -> this;
        };
    }
}