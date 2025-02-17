package mcjty.rftoolsutility.modules.logic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsutility.modules.logic.blocks.InvCheckerDamageMode;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Optional;

import static mcjty.rftoolsutility.modules.logic.blocks.InvCheckerDamageMode.DMG_IGNORE;

public record IncCheckerData(int amount, int slot, InvCheckerDamageMode useDamage, TagKey<Item> tag) {

    public static final Codec<IncCheckerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("amount").forGetter(IncCheckerData::amount),
            Codec.INT.fieldOf("slot").forGetter(IncCheckerData::slot),
            InvCheckerDamageMode.CODEC.fieldOf("useDamage").forGetter(IncCheckerData::useDamage),
            TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(data -> Optional.ofNullable(data.tag))
    ).apply(instance, (amount, slot, useDamage, tag) -> {
        return new IncCheckerData(amount, slot, useDamage, tag.orElse(null));
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, IncCheckerData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, d -> d.amount,
            ByteBufCodecs.INT, d -> d.slot,
            InvCheckerDamageMode.STREAM_CODEC, d -> d.useDamage,
            StreamCodec.of((buf, tag) -> buf.writeResourceLocation(tag.location()), buf -> TagKey.create(Registries.ITEM, buf.readResourceLocation())), d -> d.tag,
            IncCheckerData::new
    );

    public static IncCheckerData createDefault() {
        return new IncCheckerData(1, 0, DMG_IGNORE, null);
    }

    public IncCheckerData withAmount(int amount) {
        return new IncCheckerData(amount, slot, useDamage, tag);
    }

    public IncCheckerData withSlot(int slot) {
        return new IncCheckerData(amount, slot, useDamage, tag);
    }

    public IncCheckerData withUseDamage(InvCheckerDamageMode useDamage) {
        return new IncCheckerData(amount, slot, useDamage, tag);
    }

    public IncCheckerData withTag(TagKey<Item> tag) {
        return new IncCheckerData(amount, slot, useDamage, tag);
    }
}
