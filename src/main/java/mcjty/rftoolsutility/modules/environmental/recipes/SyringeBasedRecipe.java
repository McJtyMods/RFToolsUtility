package mcjty.rftoolsutility.modules.environmental.recipes;

import mcjty.lib.crafting.BaseShapedRecipe;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.data.SyringeData;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.setup.Config;
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

    public SyringeBasedRecipe(String group, ShapedRecipePattern pattern, ItemStack result, ResourceLocation mobId, int syringeIndex) {
        super(group, CraftingBookCategory.MISC, pattern, result);
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    public SyringeBasedRecipe(ShapedRecipe base, ResourceLocation mobId, int syringeIndex) {
        super(base.getGroup(), base.category(), base.pattern, base.getResultItem(null));
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    public SyringeBasedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, ResourceLocation mobId, int syringeIndex) {
        super(group, CraftingBookCategory.MISC, new ShapedRecipePattern(width, height, ingredients, Optional.empty()), result);
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    public SyringeBasedRecipe(ShapedRecipe other, ResourceLocation mobId, int syringeIndex, ItemStack result) {
        super(other.getGroup(), other.category(), new ShapedRecipePattern(other.getWidth(), other.getHeight(), other.getIngredients(), Optional.empty()), result);
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> pattern = super.getIngredients();
        if (syringeIndex < 0 || syringeIndex >= pattern.size()) {
            return pattern;
        }

        int fullLevel = Config.SERVER_CONFIG.isLoaded()
                ? SpawnerConfiguration.maxMobInjections.get() : SpawnerConfiguration.maxMobInjections.getDefault();
        ItemStack syringe = new ItemStack(SpawnerModule.SYRINGE.get());
        syringe.set(SpawnerModule.ITEM_SYRINGE_DATA, new SyringeData(mobId, fullLevel));
        if (!pattern.get(syringeIndex).test(syringe)) {
            return pattern;
        }

        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(pattern);
        ingredients.set(syringeIndex, Ingredient.of(syringe));
        return ingredients;
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
