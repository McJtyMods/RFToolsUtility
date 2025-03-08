package mcjty.rftoolsutility.modules.teleporter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;

public record ChargedPorterData(int energy, int currentTarget, List<Integer>targets, int tpTimer) {

    public static final Codec<ChargedPorterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("energy").forGetter(ChargedPorterData::energy),
            Codec.INT.fieldOf("current").forGetter(ChargedPorterData::currentTarget),
            Codec.list(Codec.INT).fieldOf("targets").forGetter(ChargedPorterData::targets),
            Codec.INT.fieldOf("tpTimer").forGetter(ChargedPorterData::tpTimer)
    ).apply(instance, ChargedPorterData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargedPorterData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, d -> d.energy,
            ByteBufCodecs.INT, d -> d.currentTarget,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), d -> d.targets,
            ByteBufCodecs.INT, d -> d.tpTimer,
            ChargedPorterData::new
    );

    public static ChargedPorterData createDefault() {
        return new ChargedPorterData(0, -1, Collections.emptyList(), -1);
    }

    public ChargedPorterData withEnergy(int energy) {
        return new ChargedPorterData(energy, currentTarget, targets, tpTimer);
    }

    public ChargedPorterData withCurrentTarget(int target) {
        return new ChargedPorterData(energy, currentTarget, targets, tpTimer);
    }

    public ChargedPorterData withTargets(List<Integer> targets) {
        return new ChargedPorterData(energy, currentTarget, targets, tpTimer);
    }

    // For usage with the advanced teleporter
    public ChargedPorterData withTarget(int index, int target) {
        List<Integer> newTarget = List.copyOf(this.targets);
        // Make sure the list is large enough first
        while (newTarget.size() <= index) {
            newTarget.add(-1);
        }
        newTarget.set(index, target);
        return new ChargedPorterData(energy, currentTarget, newTarget, tpTimer);
    }

    public int getTarget(int index) {
        return targets.size() > index ? targets.get(index) : -1;
    }

    public ChargedPorterData withTpTimer(int tpTimer) {
        return new ChargedPorterData(energy, currentTarget, targets, tpTimer);
    }
}