package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.blockcommands.ISerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TeleportDestinationClientInfo extends TeleportDestination implements Comparable<TeleportDestinationClientInfo> {

    private String dimensionName = "";
    private boolean favorite = false;

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportDestinationClientInfo> STREAM_CODEC = StreamCodec.composite(
            TeleportDestination.STREAM_CODEC, d -> d,
            ByteBufCodecs.STRING_UTF8, d -> d.getDimensionName(),
            ByteBufCodecs.BOOL, d -> d.isFavorite(),
            TeleportDestinationClientInfo::new);

    public TeleportDestinationClientInfo(TeleportDestination dest, String dimensionName, boolean favorite) {
        super(dest.getCoordinate(), dest.getDimension());
        this.dimensionName = dimensionName;
        this.favorite = favorite;
    }

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
        super(destination.getCoordinate(), destination.getDimension());
        setName(destination.getName());
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public int compareTo(TeleportDestinationClientInfo o) {
        if (getDimension().compareTo(o.getDimension()) < 0) {
            return -1;
        } else if (getDimension().compareTo(o.getDimension()) > 0) {
            return 1;
        }
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        TeleportDestinationClientInfo that = (TeleportDestinationClientInfo) o;

        if (favorite != that.favorite) {
            return false;
        }
        if (dimensionName != null ? !dimensionName.equals(that.dimensionName) : that.dimensionName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dimensionName != null ? dimensionName.hashCode() : 0);
        result = 31 * result + (favorite ? 1 : 0);
        return result;
    }
}
