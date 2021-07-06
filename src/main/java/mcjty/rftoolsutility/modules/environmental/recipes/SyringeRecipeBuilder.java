package mcjty.rftoolsutility.modules.environmental.recipes;

import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class SyringeRecipeBuilder extends ShapedRecipeBuilder {

    public SyringeRecipeBuilder(IItemProvider itemProvider, int count) {
        super(itemProvider, count);
    }

    public static ShapedRecipeBuilder shaped(IItemProvider itemProvider, ResourceLocation mobId) {
        return shaped(itemProvider, 1);
    }

}
