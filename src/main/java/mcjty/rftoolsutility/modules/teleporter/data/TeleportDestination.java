package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.varia.LevelTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class TeleportDestination {
    private final BlockPos coordinate;
    private final ResourceKey<Level> dimension;
    private String name = "";
    private boolean privateAccess = false;
    private Set<String> allowedPlayers = null;      // null means unknown, needs updating from receiver

    public TeleportDestination(FriendlyByteBuf buf) {
        int cx = buf.readInt();
        int cy = buf.readInt();
        int cz = buf.readInt();
        if (cx == -1 && cy == -1 && cz == -1) {
            coordinate = null;
        } else {
            coordinate = new BlockPos(cx, cy, cz);
        }
        dimension = LevelTools.getId(buf.readResourceLocation());
        setName(buf.readUtf(32767));
        privateAccess = buf.readBoolean();
        int len = buf.readInt();
        if (len == -1) {
            // Unknown
            allowedPlayers = null;
        } else {
            allowedPlayers = new HashSet<>(len);
            for (int i = 0 ; i < len ; i++) {
                allowedPlayers.add(buf.readUtf(32767));
            }
        }
    }

    public TeleportDestination(BlockPos coordinate, ResourceKey<Level> dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public boolean isValid() {
        return coordinate != null;
    }

    public void toBytes(FriendlyByteBuf buf) {
        if (coordinate == null) {
            buf.writeInt(-1);
            buf.writeInt(-1);
            buf.writeInt(-1);
        } else {
            buf.writeInt(coordinate.getX());
            buf.writeInt(coordinate.getY());
            buf.writeInt(coordinate.getZ());
        }
        buf.writeResourceLocation(dimension.location());
        buf.writeUtf(getName());
        buf.writeBoolean(privateAccess);
        if (allowedPlayers == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(allowedPlayers.size());
            allowedPlayers.forEach(buf::writeUtf);
        }
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

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    @Nullable
    public Set<String> getAllowedPlayers() {
        return allowedPlayers;
    }

    public void setPrivateAccess(boolean privateAccess) {
        this.privateAccess = privateAccess;
    }

    public boolean isAccessKnown() {
        return allowedPlayers != null;
    }

    public boolean checkAccess(Level level, UUID player) {
        if (!privateAccess) {
            return true;
        }
        Player playerByUuid = level.getServer().getPlayerList().getPlayer(player);
        if (playerByUuid == null) {
            return true;
        }
        return allowedPlayers.contains(playerByUuid.getDisplayName().getString());  // @todo 1.16 getFormattedText
    }

    public void setAllowedPlayers(@Nullable Set<String> allowedPlayers) {
        this.allowedPlayers = allowedPlayers == null ? null : new HashSet<>(allowedPlayers);
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
