package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.rftoolsutility.modules.logic.tools.SequencerMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ThreeLogicData(int[] logicTable) {

    // 0 == off, 1 == on, -1 == keep
    public static final Codec<ThreeLogicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("l0").forGetter(o -> o.logicTable()[0]),
            Codec.INT.fieldOf("l1").forGetter(o -> o.logicTable()[1]),
            Codec.INT.fieldOf("l2").forGetter(o -> o.logicTable()[2]),
            Codec.INT.fieldOf("l3").forGetter(o -> o.logicTable()[3]),
            Codec.INT.fieldOf("l4").forGetter(o -> o.logicTable()[4]),
            Codec.INT.fieldOf("l5").forGetter(o -> o.logicTable()[5]),
            Codec.INT.fieldOf("l6").forGetter(o -> o.logicTable()[6]),
            Codec.INT.fieldOf("l7").forGetter(o -> o.logicTable()[7])
    ).apply(instance, (l0, l1, l2, l3, l4, l5, l6, l7) -> new ThreeLogicData(new int[] {l0, l1, l2, l3, l4, l5, l6, l7})));

    public static final StreamCodec<RegistryFriendlyByteBuf, ThreeLogicData> STREAM_CODEC = CompositeStreamCodec.composite(
            ByteBufCodecs.INT, o -> o.logicTable()[0],
            ByteBufCodecs.INT, o -> o.logicTable()[1],
            ByteBufCodecs.INT, o -> o.logicTable()[2],
            ByteBufCodecs.INT, o -> o.logicTable()[3],
            ByteBufCodecs.INT, o -> o.logicTable()[4],
            ByteBufCodecs.INT, o -> o.logicTable()[5],
            ByteBufCodecs.INT, o -> o.logicTable()[6],
            ByteBufCodecs.INT, o -> o.logicTable()[7],
            (l0, l1, l2, l3, l4, l5, l6, l7) -> new ThreeLogicData(new int[] {l0, l1, l2, l3, l4, l5, l6, l7}));

    public static ThreeLogicData createDefault() {
        return new ThreeLogicData(new int[] {0, 0, 0, 0, 0, 0, 0, 0});
    }

    public ThreeLogicData withLogicTable(int[] logicTable) {
        return new ThreeLogicData(logicTable);
    }
}
