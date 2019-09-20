package mcjty.rftoolsutility.datagen;

import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
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

        ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.DIALING_DEVICE)
                .patternLine("rrr")
                .patternLine("TMT")
                .patternLine("rrr")
                .key('r', Items.REDSTONE)
                .key('T', Blocks.REDSTONE_TORCH)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_RECEIVER)
                .patternLine("iii")
                .patternLine("rMr")
                .patternLine("ooo")
                .key('r', Items.REDSTONE)
                .key('i', Items.IRON_INGOT)
                .key('o', Items.ENDER_PEARL)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_TRANSMITTER)
                .patternLine("ooo")
                .patternLine("rMr")
                .patternLine("iii")
                .key('r', Items.REDSTONE)
                .key('i', Items.IRON_INGOT)
                .key('o', Items.ENDER_PEARL)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_BOOSTER)
                .patternLine(" B ")
                .patternLine("BMB")
                .patternLine(" B ")
                .key('B', Blocks.REDSTONE_BLOCK)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.SIMPLE_DIALER)
                .patternLine("rRr")
                .patternLine("TMT")
                .patternLine("rRr")
                .key('r', Items.REDSTONE)
                .key('R', Blocks.REDSTONE_BLOCK)
                .key('T', Blocks.REDSTONE_TORCH)
                .key('M', ModItems.MACHINE_BASE)
                .setGroup("")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE))
                .build(consumer);

    }
}
