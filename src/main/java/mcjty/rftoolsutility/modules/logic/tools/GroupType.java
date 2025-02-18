package mcjty.rftoolsutility.modules.logic.tools;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum GroupType implements NamedEnum<GroupType> {
    GROUP_ONE("One", "At least one detected", "to match the sensor"),
    GROUP_ALL("All", "All blocks in area", "must match the sensor");

    public static final Codec<GroupType> CODEC = StringRepresentable.fromEnum(GroupType::values);
    public static final StreamCodec<FriendlyByteBuf, GroupType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(GroupType.class);

    private final String name;
    private final String[] description;

    GroupType(String name, String... description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String[] getDescription() {
        return description;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
