package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record DialingDeviceData(boolean showFav) {

    public static final Codec<DialingDeviceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("showFav").forGetter(DialingDeviceData::showFav)
    ).apply(instance, DialingDeviceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialingDeviceData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, d -> d.showFav,
            DialingDeviceData::new
    );

    public static DialingDeviceData createDefault() {
        return new DialingDeviceData(false);
    }

    public DialingDeviceData withShowFav(boolean showFav) {
        return new DialingDeviceData(showFav);
    }
}