package mcjty.rftoolsutility.datagen;

import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER1)
                .patternLine(" T ")
                .patternLine("cMc")
                .patternLine(" T ")
                .key('c', Blocks.CRAFTING_TABLE)
                .key('T', Blocks.REDSTONE_TORCH)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("")
                .addCriterion("machine_frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER2)
                .patternLine(" T ")
                .patternLine("cMc")
                .patternLine(" T ")
                .key('c', Blocks.CRAFTING_TABLE)
                .key('T', Blocks.REDSTONE_TORCH)
                .key('M', CrafterSetup.BLOCK_CRAFTER1)
                .setGroup("")
                .addCriterion("crafter1", InventoryChangeTrigger.Instance.forItems(CrafterSetup.BLOCK_CRAFTER1))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER3)
                .patternLine(" T ")
                .patternLine("cMc")
                .patternLine(" T ")
                .key('c', Blocks.CRAFTING_TABLE)
                .key('T', Blocks.REDSTONE_TORCH)
                .key('M', CrafterSetup.BLOCK_CRAFTER2)
                .setGroup("")
                .addCriterion("crafter2", InventoryChangeTrigger.Instance.forItems(CrafterSetup.BLOCK_CRAFTER2))
                .build(consumer);

    }
}
