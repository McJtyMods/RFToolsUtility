package mcjty.rftoolsutility.modules.logic.blocks;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum InvCheckerDamageMode implements NamedEnum<InvCheckerDamageMode> {
    DMG_MATCH("Match"),
    DMG_IGNORE("Ignore");

    private final String name;

    public static final Codec<InvCheckerDamageMode> CODEC = StringRepresentable.fromEnum(InvCheckerDamageMode::values);
    public static final StreamCodec<FriendlyByteBuf, InvCheckerDamageMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(InvCheckerDamageMode.class);

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
