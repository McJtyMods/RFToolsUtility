package mcjty.rftoolsutility.modules.environmental.blocks;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum EnvironmentalMode implements NamedEnum<EnvironmentalMode> {
    MODE_BLACKLIST("blacklist"),
    MODE_WHITELIST("whitelist"),
    MODE_HOSTILE("hostile"),
    MODE_PASSIVE("passive"),
    MODE_MOBS("mobs"),
    MODE_ALL("all");

    private final String name;

    public static final Codec<EnvironmentalMode> CODEC = StringRepresentable.fromEnum(EnvironmentalMode::values);
    public static final StreamCodec<FriendlyByteBuf, EnvironmentalMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(EnvironmentalMode.class);

    EnvironmentalMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getDescription() {
        return new String[]{name};
    }


    @Override
    public String getSerializedName() {
        return name;
    }
}
