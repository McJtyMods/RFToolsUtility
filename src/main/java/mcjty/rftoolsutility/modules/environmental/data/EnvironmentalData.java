package mcjty.rftoolsutility.modules.environmental.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Set;

public record EnvironmentalData(int radius, int miny, int maxy, EnvironmentalMode mode, Set<String> players /* @todo convert to UUID */) {

    public static final Codec<EnvironmentalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(EnvironmentalData::radius),
            Codec.INT.fieldOf("miny").forGetter(EnvironmentalData::miny),
            Codec.INT.fieldOf("maxy").forGetter(EnvironmentalData::maxy),
            EnvironmentalMode.CODEC.fieldOf("mode").forGetter(EnvironmentalData::mode),
            Codec.STRING.listOf().fieldOf("players").forGetter(data -> new ArrayList<>(data.players()))
    ).apply(instance, (radius, minY, maxY, mode, playerList) -> {
        Set<String> players = Set.copyOf(playerList);
        return new EnvironmentalData(radius, minY, maxY, mode, players);
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnvironmentalData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EnvironmentalData::radius,
            ByteBufCodecs.INT, EnvironmentalData::miny,
            ByteBufCodecs.INT, EnvironmentalData::maxy,
            EnvironmentalMode.STREAM_CODEC, EnvironmentalData::mode,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), data -> new ArrayList<>(data.players()),
            (radius, minY, maxY, mode, playerList) -> {
                Set<String> players = Set.copyOf(playerList);
                return new EnvironmentalData(radius, minY, maxY, mode, players);
            }
    );

    public static final EnvironmentalData createDefault() {
        return new EnvironmentalData(50, 30, 70, EnvironmentalMode.MODE_BLACKLIST, Set.of());
    }

    public EnvironmentalData withRadius(int radius) {
        return new EnvironmentalData(radius, miny, maxy, mode, players);
    }

    public EnvironmentalData withMiny(int miny) {
        return new EnvironmentalData(radius, miny, maxy, mode, players);
    }

    public EnvironmentalData withMaxy(int maxy) {
        return new EnvironmentalData(radius, miny, maxy, mode, players);
    }

    public EnvironmentalData withMode(EnvironmentalMode mode) {
        return new EnvironmentalData(radius, miny, maxy, mode, players);
    }

    public EnvironmentalData withPlayers(Set<String> players) {
        return new EnvironmentalData(radius, miny, maxy, mode, players);
    }
}
