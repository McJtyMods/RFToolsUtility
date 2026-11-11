package mcjty.rftoolsutility.modules.environmental.recipes;

import mcjty.lib.crafting.AbstractRecipeAdaptor;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class SyringeBasedRecipe extends AbstractRecipeAdaptor {

    private final Identifier mobId;
    private final int syringeIndex;

    public SyringeBasedRecipe(ShapedRecipe recipe, Identifier mobId, int syringeIndex) {
        super(recipe);
        this.mobId = mobId;
        this.syringeIndex = syringeIndex;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!getRecipe().matches(input, level)) {
            return false;
        }
        for (int index = 0; index < input.size(); index++) {
            ItemStack stack = input.getItem(index);
            if (stack.getItem() instanceof SyringeItem
                    && (!mobId.equals(SyringeItem.getMobId(stack)) || SyringeItem.getLevel(stack) < 100)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return getRecipe().assemble(input);
    }

    public Identifier getMobId() {
        return mobId;
    }

    public int getSyringeIndex() {
        return syringeIndex;
    }

    @Override
    public RecipeSerializer<SyringeBasedRecipe> getSerializer() {
        return EnvironmentalModule.SYRINGE_SERIALIZER.get();
    }
}
