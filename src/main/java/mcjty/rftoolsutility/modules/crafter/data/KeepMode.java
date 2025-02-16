package mcjty.rftoolsutility.modules.crafter.data;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum KeepMode implements NamedEnum<KeepMode> {
    ALL("All"),
    KEEP("Keep");

    private final String description;

    KeepMode(String description) {
        this.description = description;
    }

    public static final Codec<KeepMode> CODEC = StringRepresentable.fromEnum(KeepMode::values);
    public static final StreamCodec<FriendlyByteBuf, KeepMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(KeepMode.class);

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
