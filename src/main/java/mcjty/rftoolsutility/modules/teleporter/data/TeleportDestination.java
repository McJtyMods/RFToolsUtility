package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public record TeleportDestination(GlobalPos pos, String name, boolean privateAccess, Set<String> allowedPlayers) {
    public static final TeleportDestination INVALID = new TeleportDestination(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), "", false, null);

    public static final Codec<TeleportDestination> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(d -> d.pos),
            Codec.STRING.fieldOf("name").forGetter(d -> d.getName()),
            Codec.BOOL.fieldOf("privateAccess").forGetter(TeleportDestination::isPrivateAccess),
            Codec.list(Codec.STRING).optionalFieldOf("allowedPlayers").forGetter(d -> d.allowedPlayers == null ? Optional.empty() : Optional.of(new ArrayList<>(d.allowedPlayers)))
    ).apply(instance, (pos, name, priv, players) -> new TeleportDestination(pos, name, priv, players.map(HashSet::new).orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportDestination> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, d -> d.pos,
            ByteBufCodecs.STRING_UTF8, d -> d.name,
            ByteBufCodecs.BOOL, d -> d.privateAccess,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())), d -> d.allowedPlayers == null ? Optional.empty() : Optional.of(new ArrayList<>(d.allowedPlayers)),
            (pos, name, priv, players) -> new TeleportDestination(pos, name, priv, players.map(HashSet::new).orElse(null)));

    public TeleportDestination(BlockPos coordinate, ResourceKey<Level> dimension) {
        this(GlobalPos.of(dimension, coordinate), "", false, null);
    }

    public boolean isValid() {
        return pos.pos() != BlockPosTools.INVALID;
    }

    public String getName() {
        return name;
    }

    public TeleportDestination withName(String name) {
        return new TeleportDestination(pos, name == null ? "" : name, privateAccess, allowedPlayers);
    }

    public GlobalPos getPos() {
        return pos;
    }

    public BlockPos getCoordinate() {
        return pos.pos();
    }

    public ResourceKey<Level> getDimension() {
        return pos.dimension();
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    @Nullable
    public Set<String> getAllowedPlayers() {
        return allowedPlayers;
    }

    public TeleportDestination withPrivateAccess(boolean privateAccess) {
        return new TeleportDestination(pos, name, privateAccess, allowedPlayers);
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

    public TeleportDestination withAllowedPlayers(@Nullable Set<String> allowedPlayers) {
        return new TeleportDestination(pos, name, privateAccess, allowedPlayers == null ? null : new HashSet<>(allowedPlayers));
    }
}
