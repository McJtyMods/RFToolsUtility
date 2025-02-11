package mcjty.rftoolsutility.modules.crafter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CrafterData(List<ItemStack> ghostSlots) {

    public static final Codec<CrafterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("ghostSlots").forGetter(CrafterData::ghostSlots)
    ).apply(instance, CrafterData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrafterData> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, CrafterData::ghostSlots,
            CrafterData::new
    );

    public static final CrafterData createDefault() {
        return new CrafterData(ItemStackList.create(CrafterContainer.BUFFER_SIZE + CrafterContainer.BUFFEROUT_SIZE));
    }

    public CrafterData withGhostSlots(List<ItemStack> ghostSlots) {
        return new CrafterData(ghostSlots);
    }
}
