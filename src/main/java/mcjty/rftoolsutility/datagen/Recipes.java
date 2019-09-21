package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
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
        CopyNBTRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER2)
                .patternLine(" T ")
                .patternLine("cMc")
                .patternLine(" T ")
                .key('c', Blocks.CRAFTING_TABLE)
                .key('T', Blocks.REDSTONE_TORCH)
                .key('M', CrafterSetup.BLOCK_CRAFTER1)
                .setGroup("")
                .addCriterion("crafter1", InventoryChangeTrigger.Instance.forItems(CrafterSetup.BLOCK_CRAFTER1))
                .build(consumer);
        CopyNBTRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER3)
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
        ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.CHARGED_PORTER)
                .patternLine(" o ")
                .patternLine("oRo")
                .patternLine("ioi")
                .key('i', Items.IRON_INGOT)
                .key('o', Items.ENDER_PEARL)
                .key('R', Blocks.REDSTONE_BLOCK)
                .setGroup("")
                .addCriterion("pearl", InventoryChangeTrigger.Instance.forItems(Items.ENDER_PEARL))
                .build(consumer);
        CopyNBTRecipeBuilder.shapedRecipe(TeleporterSetup.ADVANCED_CHARGED_PORTER)
                .patternLine("RdR")
                .patternLine("dMd")
                .patternLine("RdR")
                .key('M', TeleporterSetup.CHARGED_PORTER)
                .key('d', Items.DIAMOND)
                .key('R', Blocks.REDSTONE_BLOCK)
                .setGroup("")
                .addCriterion("porter", InventoryChangeTrigger.Instance.forItems(TeleporterSetup.CHARGED_PORTER))
                .build(consumer);

    }
}
