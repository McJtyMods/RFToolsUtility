package mcjty.rftoolsutility.modules.logic.blocks;

import com.mojang.serialization.Codec;
import mcjty.lib.network.StreamCodecs;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum InvCheckerDamageMode implements NamedEnum<InvCheckerDamageMode> {
    DMG_MATCH("Match"),
    DMG_IGNORE("Ignore");

    private final String name;

    public static final Codec<InvCheckerDamageMode> CODEC = StringRepresentable.fromEnum(InvCheckerDamageMode::values);
    public static final StreamCodec<FriendlyByteBuf, InvCheckerDamageMode> STREAM_CODEC = StreamCodecs.enumCodec(InvCheckerDamageMode.class);

    InvCheckerDamageMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getDescription() {
        return new String[] { name };
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
