package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.blockcommands.ISerializer;
import mcjty.lib.varia.BlockPosTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record TransmitterInfo(BlockPos coordinate, String name, TeleportDestination teleportDestination) {

    public static final Codec<TransmitterInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("coordinate").forGetter(info -> Optional.ofNullable(info.coordinate)),
            Codec.STRING.fieldOf("name").forGetter(TransmitterInfo::getName),
            TeleportDestination.CODEC.fieldOf("destination").forGetter(TransmitterInfo::getTeleportDestination)
    ).apply(instance, (pos, name, dest) -> new TransmitterInfo(pos.orElse(null), name, dest)));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransmitterInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), d -> Optional.ofNullable(d.coordinate),
            ByteBufCodecs.STRING_UTF8, d -> d.name,
            TeleportDestination.STREAM_CODEC, d -> d.teleportDestination,
            (pos, name, dest) -> new TransmitterInfo(pos.orElse(null), name, dest));

    public static class Serializer implements ISerializer<TransmitterInfo> {
        @Override
        public Function<RegistryFriendlyByteBuf, TransmitterInfo> getDeserializer() {
            return STREAM_CODEC::decode;
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, TransmitterInfo> getSerializer() {
            return STREAM_CODEC::encode;
        }
    }

    public TransmitterInfo(BlockPos coordinate, String name, TeleportDestination teleportDestination) {
        this.coordinate = coordinate;
        this.name = name;
        if (teleportDestination == null) {
            this.teleportDestination = new TeleportDestination(BlockPosTools.INVALID, Level.OVERWORLD);
        } else {
            this.teleportDestination = teleportDestination;
        }
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
}
