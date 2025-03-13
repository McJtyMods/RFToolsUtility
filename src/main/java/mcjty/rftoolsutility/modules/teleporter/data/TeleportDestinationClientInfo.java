package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.blockcommands.ISerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record TeleportDestinationClientInfo(TeleportDestination destination, String dimensionName, boolean favorite) implements Comparable<TeleportDestinationClientInfo> {

    public static final TeleportDestinationClientInfo DEFAULT = new TeleportDestinationClientInfo(TeleportDestination.INVALID, "", false);

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportDestinationClientInfo> STREAM_CODEC = StreamCodec.composite(
            TeleportDestination.STREAM_CODEC, d -> d.destination,
            ByteBufCodecs.STRING_UTF8, TeleportDestinationClientInfo::getDimensionName,
            ByteBufCodecs.BOOL, TeleportDestinationClientInfo::isFavorite,
            TeleportDestinationClientInfo::new);

    public static class Serializer implements ISerializer<TeleportDestinationClientInfo> {
        @Override
        public Function<RegistryFriendlyByteBuf, TeleportDestinationClientInfo> getDeserializer() {
            return buf -> STREAM_CODEC.decode(buf);
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, TeleportDestinationClientInfo> getSerializer() {
            return (buf, s) -> STREAM_CODEC.encode(buf, s);
        }
    }

    public TeleportDestinationClientInfo(TeleportDestination destination) {
        this(destination, "", false);
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public TeleportDestinationClientInfo withDimensionName(String dimensionName) {
        return new TeleportDestinationClientInfo(destination, dimensionName, favorite);
    }

    public TeleportDestinationClientInfo withFavorite(boolean favorite) {
        return new TeleportDestinationClientInfo(destination, dimensionName, favorite);
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public int compareTo(TeleportDestinationClientInfo o) {
        if (destination.getDimension().compareTo(o.destination().getDimension()) < 0) {
            return -1;
        } else if (destination.getDimension().compareTo(o.destination.getDimension()) > 0) {
            return 1;
        }
        return destination.getName().compareTo(o.destination.getName());
    }
}
