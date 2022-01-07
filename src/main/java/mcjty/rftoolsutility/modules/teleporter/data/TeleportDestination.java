package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.varia.LevelTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class TeleportDestination {
    private final BlockPos coordinate;
    private final RegistryKey<World> dimension;
    private String name = "";
    private boolean privateAccess = false;
    private Set<String> allowedPlayers = null;      // null means unknown, needs updating from receiver

    public TeleportDestination(PacketBuffer buf) {
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

    public TeleportDestination(CompoundNBT tc) {
        coordinate = new BlockPos(tc.getInt("x"), tc.getInt("y"), tc.getInt("z"));
        dimension = LevelTools.getId(tc.getString("dim"));
        name = tc.getString("name");
        privateAccess = tc.getBoolean("privateAccess");
        if (tc.contains("allowedPlayers")) {
            ListNBT players = tc.getList("allowedPlayers", Constants.NBT.TAG_STRING);
            allowedPlayers = new HashSet<>(players.size());
            players.forEach(player -> allowedPlayers.add(player.getAsString()));
        } else {
            allowedPlayers = null;  // Unknown
        }
    }

    public TeleportDestination(BlockPos coordinate, RegistryKey<World> dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public boolean isValid() {
        return coordinate != null;
    }

    public CompoundNBT writeToTag() {
        CompoundNBT tc = new CompoundNBT();
        BlockPos c = getCoordinate();
        tc.putInt("x", c.getX());
        tc.putInt("y", c.getY());
        tc.putInt("z", c.getZ());
        tc.putString("dim", getDimension().location().toString());
        tc.putString("name", getName());
        tc.putBoolean("privateAccess", privateAccess);
        if (allowedPlayers != null) {
            ListNBT list = new ListNBT();
            allowedPlayers.forEach(p -> list.add(StringNBT.valueOf(p)));
            tc.put("allowedPlayers", list);
        }
        return tc;
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

    public RegistryKey<World> getDimension() {
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

    public boolean checkAccess(World level, UUID player) {
        if (!privateAccess) {
            return true;
        }
        PlayerEntity playerByUuid = level.getServer().getPlayerList().getPlayer(player);
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
