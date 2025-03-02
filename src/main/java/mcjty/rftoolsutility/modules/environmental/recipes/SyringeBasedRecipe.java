package mcjty.rftoolsutility.modules.environmental.recipes;

import mcjty.lib.crafting.BaseShapedRecipe;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SyringeBasedRecipe extends BaseShapedRecipe {

    private final ResourceLocation mobId;
    private final int syringeIndex;

    public SyringeBasedRecipe(BaseShapedRecipe base, ResourceLocation mobId, int syringeIndex) {
        super(base.getGroup(), base.category(), base.pattern, base.getResultItem(null));
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    public SyringeBasedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, ResourceLocation mobId, int syringeIndex) {
        super(group, CraftingBookCategory.MISC, new ShapedRecipePattern(width, height, addMob(ingredients, mobId, syringeIndex), Optional.empty()), result);
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    public SyringeBasedRecipe(ShapedRecipe other, ResourceLocation mobId, int syringeIndex, ItemStack result) {
        super(other.getGroup(), other.category(), new ShapedRecipePattern(other.getWidth(), other.getHeight(), addMob(other.getIngredients(), mobId, syringeIndex), Optional.empty()), result);
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    private static NonNullList<Ingredient> addMob(NonNullList<Ingredient> input, ResourceLocation mobId, int syringeIndex) {
        NonNullList<Ingredient> output = NonNullList.withSize(input.size(), Ingredient.EMPTY);
        for (int i = 0 ; i < input.size() ; i++) {
            Ingredient ingredient = input.get(i);
            if (syringeIndex == i) {
                if (!ingredient.isEmpty() && ingredient.getItems().length > 0 && ingredient.getItems()[0].getItem() instanceof SyringeItem) {
                    ItemStack syringe = SyringeItem.createMobSyringe(mobId);
                    ingredient = Ingredient.of(syringe);
                } else {
                    throw new RuntimeException("Bad recipe. Index " + syringeIndex + " does not point to syringe!");
                }
            }
            output.set(i, ingredient);
        }
        return output;
    }

    @Override
    public boolean matches(@Nonnull CraftingInput inv, @Nonnull Level level) {
        boolean matches = super.matches(inv, level);
        if (matches) {
            for (int i = 0 ; i < inv.width() * inv.height() ; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof SyringeItem) {
                    ResourceLocation mob = SyringeItem.getMobId(stack);
                    if (mob == null || !mob.equals(mobId)) {
                        return false;
                    }
                    int amount = SyringeItem.getLevel(stack);
                    if (amount < 100) {
                        return false;
                    }
                }
            }
        }
        return matches;
    }

    public ResourceLocation getMobId() {
        return mobId;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnvironmentalModule.SYRINGE_SERIALIZER.get();
    }

    public int getSyringeIndex() {
        return syringeIndex;
    }
}
