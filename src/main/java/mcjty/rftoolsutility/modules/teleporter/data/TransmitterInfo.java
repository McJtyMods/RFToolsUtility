package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.blockcommands.ISerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TransmitterInfo {
    private final BlockPos coordinate;
    private final String name;
    private final TeleportDestination teleportDestination;

    public static class Serializer implements ISerializer<TransmitterInfo> {
        @Override
        public Function<FriendlyByteBuf, TransmitterInfo> getDeserializer() {
            return TransmitterInfo::new;
        }

        @Override
        public BiConsumer<FriendlyByteBuf, TransmitterInfo> getSerializer() {
            return (buf, s) -> s.toBytes(buf);
        }
    }

    public TransmitterInfo(FriendlyByteBuf buf) {
        coordinate = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        name = buf.readUtf(32767);
        teleportDestination = new TeleportDestination(buf);
    }

    public TransmitterInfo(BlockPos coordinate, String name, TeleportDestination destination) {
        this.coordinate = coordinate;
        this.name = name;
        if (destination == null) {
            this.teleportDestination = new TeleportDestination(null, Level.OVERWORLD);
        } else {
            this.teleportDestination = destination;
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coordinate.getX());
        buf.writeInt(coordinate.getY());
        buf.writeInt(coordinate.getZ());
        buf.writeUtf(getName());
        teleportDestination.toBytes(buf);
    }

    public BlockPos getCoordinate() {
        return coordinate;
    }

    public String getName() {
        return name;
    }

    public TeleportDestination getTeleportDestination() {
        return teleportDestination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransmitterInfo that = (TransmitterInfo) o;

        if (coordinate != null ? !coordinate.equals(that.coordinate) : that.coordinate != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (!teleportDestination.equals(that.teleportDestination)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = coordinate != null ? coordinate.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (teleportDestination.hashCode());
        return result;
    }
}
