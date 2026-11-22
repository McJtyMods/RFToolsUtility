package mcjty.rftoolsutility.modules.environmental.recipes;

import mcjty.lib.crafting.BaseShapedRecipe;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.setup.Config;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class SyringeBasedRecipe extends BaseShapedRecipe {

    private final ResourceLocation mobId;
    private final int syringeIndex;

    public SyringeBasedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, ResourceLocation mobId, int syringeIndex) {
        super(id, group, width, height, NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new)), result);
        if (syringeIndex >= 0 && syringeIndex < ingredients.size()) {
            ItemStack[] candidates = ingredients.get(syringeIndex).getItems();
            if (candidates.length == 0 || !(candidates[0].getItem() instanceof SyringeItem)) {
                throw new IllegalArgumentException("Bad recipe. Index " + syringeIndex + " does not point to syringe!");
            }
            super.getIngredients().set(syringeIndex, Ingredient.of(SpawnerModule.SYRINGE.get()));
        }
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    public SyringeBasedRecipe(ShapedRecipe other, ResourceLocation mobId, int syringeIndex, ItemStack result) {
        this(other.getId(), other.getGroup(), other.getWidth(), other.getHeight(), other.getIngredients(), result, mobId, syringeIndex);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> pattern = super.getIngredients();
        if (syringeIndex < 0 || syringeIndex >= pattern.size()) {
            return pattern;
        }

        ItemStack syringe = SyringeItem.createMobSyringe(mobId);
        int fullLevel = Config.SERVER_CONFIG.isLoaded()
                ? SpawnerConfiguration.maxMobInjections.get() : SpawnerConfiguration.maxMobInjections.getDefault();
        syringe.getOrCreateTag().putInt("level", fullLevel);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(pattern);
        ingredients.set(syringeIndex, Ingredient.of(syringe));
        return ingredients;
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level level) {
        boolean matches = super.matches(inv, level);
        if (matches) {
            for (int i = 0 ; i < inv.getWidth() * inv.getHeight() ; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof SyringeItem) {
                    String mob = SyringeItem.getMobId(stack);
                    if (mob == null || !mob.equals(mobId.toString())) {
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
