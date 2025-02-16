package mcjty.rftoolsutility.modules.crafter.data;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum CraftMode implements NamedEnum<CraftMode> {
    EXT("Ext"),
    INT("Int"),
    EXTC("ExtC");

    private final String description;

    CraftMode(String description) {
        this.description = description;
    }

    public static final Codec<CraftMode> CODEC = StringRepresentable.fromEnum(CraftMode::values);
    public static final StreamCodec<FriendlyByteBuf, CraftMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(CraftMode.class);

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
