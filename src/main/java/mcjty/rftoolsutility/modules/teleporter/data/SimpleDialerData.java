package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public record SimpleDialerData(GlobalPos transmitter, int receiver, boolean onceMode) {

    public static final Codec<SimpleDialerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("transmitter").forGetter(SimpleDialerData::transmitter),
            Codec.INT.fieldOf("receiver").forGetter(SimpleDialerData::receiver),
            Codec.BOOL.fieldOf("once").forGetter(SimpleDialerData::onceMode)
    ).apply(instance, SimpleDialerData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleDialerData> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, d -> d.transmitter,
            ByteBufCodecs.INT, d -> d.receiver,
            ByteBufCodecs.BOOL, d -> d.onceMode,
            SimpleDialerData::new
    );

    public static SimpleDialerData createDefault() {
        return new SimpleDialerData(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), -1, false);
    }

    public SimpleDialerData withTransmitter(GlobalPos transmitter) {
        return new SimpleDialerData(transmitter, receiver, onceMode);
    }

    public SimpleDialerData withReceiver(int receiver) {
        return new SimpleDialerData(transmitter, receiver, onceMode);
    }

    public SimpleDialerData withOnceMode(boolean onceMode) {
        return new SimpleDialerData(transmitter, receiver, onceMode);
    }
}