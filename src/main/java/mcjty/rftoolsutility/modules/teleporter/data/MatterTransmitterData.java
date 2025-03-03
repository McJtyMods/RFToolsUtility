package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public record MatterTransmitterData(int id, String name, boolean privateAccess, Set<String> players) {

    public static final Codec<MatterTransmitterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(MatterTransmitterData::id),
            Codec.STRING.optionalFieldOf("name").forGetter(d -> Optional.ofNullable(d.name)),
            Codec.BOOL.fieldOf("private").forGetter(MatterTransmitterData::privateAccess),
            Codec.list(Codec.STRING).fieldOf("players").forGetter(d -> new ArrayList<>(d.players()))
    ).apply(instance, (id, name, priv, players) -> new MatterTransmitterData(id, name.orElse(null), priv, new HashSet<>(players))));

    public static final StreamCodec<RegistryFriendlyByteBuf, MatterTransmitterData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, d -> d.id,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), d -> Optional.ofNullable(d.name),
            ByteBufCodecs.BOOL, d -> d.privateAccess,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), d -> new ArrayList<>(d.players),
            (id, name, priv, players) -> new MatterTransmitterData(id, name.orElse(null), priv, new HashSet<>(players)));

    public static MatterTransmitterData createDefault() {
        return new MatterTransmitterData(-1, null, false, Collections.emptySet());
    }

    public MatterTransmitterData withId(int id) {
        return new MatterTransmitterData(id, name, privateAccess, players);
    }

    public MatterTransmitterData withShowFav(boolean showFav) {
        return new MatterTransmitterData(id, name, showFav, players);
    }

    public MatterTransmitterData withName(String name) {
        return new MatterTransmitterData(id, name, privateAccess, players);
    }

    public MatterTransmitterData withPlayers(Set<String> players) {
        return new MatterTransmitterData(id, name, privateAccess, players);
    }

    public MatterTransmitterData addPlayer(String player) {
        Set<String> newPlayers = new HashSet<>(players);
        newPlayers.add(player);
        return new MatterTransmitterData(id, name, privateAccess, newPlayers);
    }

    public MatterTransmitterData removePlayer(String player) {
        Set<String> newPlayers = new HashSet<>(players);
        newPlayers.remove(player);
        return new MatterTransmitterData(id, name, privateAccess, newPlayers);
    }
}