package mcjty.rftoolsutility.modules.crafter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsutility.modules.crafter.blocks.CrafterContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record CrafterData(List<ItemStack> ghostSlots, List<CraftingRecipe> recipes, SpeedMode speedMode) {

    public static final Codec<CrafterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("ghostSlots").forGetter(CrafterData::ghostSlots),
            CraftingRecipe.CODEC.listOf().fieldOf("recipes").forGetter(CrafterData::recipes),
            SpeedMode.CODEC.fieldOf("speedMode").forGetter(CrafterData::speedMode)
    ).apply(instance, CrafterData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrafterData> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, CrafterData::ghostSlots,
            CraftingRecipe.STREAM_CODEC.apply(ByteBufCodecs.list()), CrafterData::recipes,
            SpeedMode.STREAM_CODEC, CrafterData::speedMode,
            CrafterData::new
    );

    public static final CrafterData createDefault() {
        return new CrafterData(ItemStackList.create(CrafterContainer.BUFFER_SIZE + CrafterContainer.BUFFEROUT_SIZE), new ArrayList<>(), SpeedMode.SLOW);
    }

    public CrafterData withGhostSlots(List<ItemStack> ghostSlots) {
        return new CrafterData(ghostSlots, recipes, speedMode);
    }

    public CrafterData withRecipes(List<CraftingRecipe> recipes) {
        return new CrafterData(ghostSlots, recipes, speedMode);
    }

    public CrafterData withSpeedMode(SpeedMode speedMode) {
        return new CrafterData(ghostSlots, recipes, speedMode);
    }
}
