package mcjty.rftoolsutility.modules.logic.tools;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum AreaType implements NamedEnum<AreaType> {
    AREA_1("Area 1", 1, "1 block in front of sensor"),
    AREA_3("Area 3", 3, "3 blocks in front of sensor"),
    AREA_5("Area 5", 5, "5 blocks in front of sensor"),
    AREA_3X3("Area 3x3", -3, "3x3 blocks in front of sensor"),
    AREA_5X5("Area 5x5", -5, "5x5 blocks in front of sensor"),
    AREA_7X7("Area 7x7", -7, "7x7 blocks in front of sensor");

    public static final Codec<AreaType> CODEC = StringRepresentable.fromEnum(AreaType::values);
    public static final StreamCodec<FriendlyByteBuf, AreaType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(AreaType.class);

    private final String name;
    private final String[] description;
    private final int blockCount;

    AreaType(String name, int blockCount, String... description) {
        this.name = name;
        this.blockCount = blockCount;
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

    public int getBlockCount() {
        return blockCount;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
