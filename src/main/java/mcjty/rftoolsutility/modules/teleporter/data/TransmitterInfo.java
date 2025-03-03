package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.blockcommands.ISerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record TransmitterInfo(BlockPos coordinate, String name, TeleportDestination teleportDestination) {

    public static final Codec<TransmitterInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("coordinate").forGetter(TransmitterInfo::getCoordinate),
            Codec.STRING.fieldOf("name").forGetter(TransmitterInfo::getName),
            TeleportDestination.CODEC.fieldOf("destination").forGetter(TransmitterInfo::getTeleportDestination)
    ).apply(instance, TransmitterInfo::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransmitterInfo> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, d -> d.coordinate,
            ByteBufCodecs.STRING_UTF8, d -> d.name,
            TeleportDestination.STREAM_CODEC, d -> d.teleportDestination,
            TransmitterInfo::new);

    public static class Serializer implements ISerializer<TransmitterInfo> {
        @Override
        public Function<RegistryFriendlyByteBuf, TransmitterInfo> getDeserializer() {
            return buf -> STREAM_CODEC.decode(buf);
        }

        @Override
        public BiConsumer<RegistryFriendlyByteBuf, TransmitterInfo> getSerializer() {
            return (buf, s) -> STREAM_CODEC.encode(buf, s);
        }
    }

    public TransmitterInfo(BlockPos coordinate, String name, TeleportDestination teleportDestination) {
        this.coordinate = coordinate;
        this.name = name;
        if (teleportDestination == null) {
            this.teleportDestination = new TeleportDestination(null, Level.OVERWORLD);
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
