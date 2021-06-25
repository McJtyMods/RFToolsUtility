package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.varia.DimensionId;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class TeleportDestination {
    private final BlockPos coordinate;
    private final DimensionId dimension;
    private String name = "";

    public TeleportDestination(PacketBuffer buf) {
        int cx = buf.readInt();
        int cy = buf.readInt();
        int cz = buf.readInt();
        if (cx == -1 && cy == -1 && cz == -1) {
            coordinate = null;
        } else {
            coordinate = new BlockPos(cx, cy, cz);
        }
        dimension = DimensionId.fromPacket(buf);
        setName(buf.readUtf(32767));
    }

    public TeleportDestination(BlockPos coordinate, DimensionId dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public boolean isValid() {
        return coordinate != null;
    }

    public void toBytes(PacketBuffer buf) {
        if (coordinate == null) {
            buf.writeInt(-1);
            buf.writeInt(-1);
            buf.writeInt(-1);
        } else {
            buf.writeInt(coordinate.getX());
            buf.writeInt(coordinate.getY());
            buf.writeInt(coordinate.getZ());
        }
        dimension.toBytes(buf);
        buf.writeUtf(getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    public BlockPos getCoordinate() {
        return coordinate;
    }

    public DimensionId getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeleportDestination that = (TeleportDestination) o;
        return Objects.equals(coordinate, that.coordinate) &&
                Objects.equals(dimension, that.dimension) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, dimension, name);
    }
}
