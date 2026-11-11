package mcjty.rftoolsutility.modules.crafter.data;

import com.mojang.serialization.Codec;
import mcjty.lib.network.StreamCodecs;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum SpeedMode implements NamedEnum<SpeedMode> {
    SLOW("Slow"),
    FAST("Fast");

    private final String description;

    SpeedMode(String description) {
        this.description = description;
    }

    public static final Codec<SpeedMode> CODEC = StringRepresentable.fromEnum(SpeedMode::values);
    public static final StreamCodec<FriendlyByteBuf, SpeedMode> STREAM_CODEC = StreamCodecs.enumCodec(SpeedMode.class);

    @Override
    public String getName() {
        return description;
    }

    @Override
    public String[] getDescription() {
        return new String[]{description};
    }

    @Override
    public String getSerializedName() {
        return description;
    }
}
