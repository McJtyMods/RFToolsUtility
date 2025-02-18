package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.modules.logic.tools.AreaType;
import mcjty.rftoolsutility.modules.logic.tools.GroupType;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SensorData(int number, SensorType sensorType, AreaType areaType, GroupType groupType) {

    public static final Codec<SensorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("number").forGetter(SensorData::number),
            SensorType.CODEC.fieldOf("sensorType").forGetter(SensorData::sensorType),
            AreaType.CODEC.fieldOf("areaType").forGetter(SensorData::areaType),
            GroupType.CODEC.fieldOf("groupType").forGetter(SensorData::groupType)
    ).apply(instance, SensorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SensorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SensorData::number,
            SensorType.STREAM_CODEC, SensorData::sensorType,
            AreaType.STREAM_CODEC, SensorData::areaType,
            GroupType.STREAM_CODEC, SensorData::groupType,
            SensorData::new);

    public static SensorData createDefault() {
        return new SensorData(0, SensorType.SENSOR_BLOCK, AreaType.AREA_1, GroupType.GROUP_ONE);
    }

    public SensorData withNumber(int number) {
        return new SensorData(number, sensorType, areaType, groupType);
    }

    public SensorData withSensorType(SensorType sensorType) {
        return new SensorData(number, sensorType, areaType, groupType);
    }

    public SensorData withAreaType(AreaType areaType) {
        return new SensorData(number, sensorType, areaType, groupType);
    }

    public SensorData withGroupType(GroupType groupType) {
        return new SensorData(number, sensorType, areaType, groupType);
    }
}
