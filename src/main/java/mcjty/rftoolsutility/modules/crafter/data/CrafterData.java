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
import java.util.Objects;

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

    public static CrafterData createDefault() {
        return new CrafterData(ItemStackList.create(CrafterContainer.BUFFER_SIZE + CrafterContainer.BUFFEROUT_SIZE), new ArrayList<>(), SpeedMode.SLOW);
    }

    public CrafterData withGhostSlots(List<ItemStack> ghostSlots) {
        return new CrafterData(ghostSlots, recipes, speedMode);
    }

    public CrafterData withRecipes(List<CraftingRecipe> recipes) {
        return new CrafterData(ghostSlots, recipes, speedMode);
    }

    public CraftingRecipe getRecipeSafe(int index) {
        if (index < 0 || index >= recipes.size()) {
            return new CraftingRecipe();
        }
        return recipes.get(index);
    }

    // This function will extend recipes if needed but it doesn't check for a max size
    public CrafterData setRecipeSafe(int index, CraftingRecipe recipe) {
        if (index < 0) {
            return this;
        }
        List<CraftingRecipe> newRecipes = new ArrayList<>(recipes);
        while (newRecipes.size() <= index) {
            newRecipes.add(new CraftingRecipe());
        }
        newRecipes.set(index, recipe);
        return new CrafterData(ghostSlots, newRecipes, speedMode);
    }

    public CrafterData withSpeedMode(SpeedMode speedMode) {
        return new CrafterData(ghostSlots, recipes, speedMode);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CrafterData that)) return false;
        return speedMode == that.speedMode
                && ItemStack.listMatches(ghostSlots, that.ghostSlots)
                && Objects.equals(recipes, that.recipes);
    }


    @Override
    public int hashCode() {
        return Objects.hash(ItemStack.hashStackList(ghostSlots), recipes, speedMode);
    }
}
