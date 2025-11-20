package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public record MatterReceiverData(int id, String name, boolean privateAccess, Set<String> players) {

    public static final MatterReceiverData DEFAULT = new MatterReceiverData(-1, null, false, Collections.emptySet());

    public static final Codec<MatterReceiverData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(MatterReceiverData::id),
            Codec.STRING.optionalFieldOf("name").forGetter(d -> Optional.ofNullable(d.name)),
            Codec.BOOL.fieldOf("private").forGetter(MatterReceiverData::privateAccess),
            Codec.list(Codec.STRING).fieldOf("players").forGetter(d -> new ArrayList<>(d.players()))
    ).apply(instance, (id, name, priv, players) -> new MatterReceiverData(id, name.orElse(null), priv, new HashSet<>(players))));

    public static final StreamCodec<RegistryFriendlyByteBuf, MatterReceiverData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, d -> d.id,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), d -> Optional.ofNullable(d.name),
            ByteBufCodecs.BOOL, d -> d.privateAccess,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), d -> new ArrayList<>(d.players),
            (id, name, priv, players) -> new MatterReceiverData(id, name.orElse(null), priv, new HashSet<>(players)));

    public MatterReceiverData withId(int id) {
        return new MatterReceiverData(id, name, privateAccess, players);
    }

    public MatterReceiverData withShowFav(boolean showFav) {
        return new MatterReceiverData(id, name, showFav, players);
    }

    public MatterReceiverData withName(String name) {
        return new MatterReceiverData(id, name, privateAccess, players);
    }

    public MatterReceiverData withPlayers(Set<String> players) {
        return new MatterReceiverData(id, name, privateAccess, players);
    }

    public MatterReceiverData addPlayer(String player) {
        Set<String> newPlayers = new HashSet<>(players);
        newPlayers.add(player);
        return new MatterReceiverData(id, name, privateAccess, newPlayers);
    }

    public MatterReceiverData removePlayer(String player) {
        Set<String> newPlayers = new HashSet<>(players);
        newPlayers.remove(player);
        return new MatterReceiverData(id, name, privateAccess, newPlayers);
    }
}