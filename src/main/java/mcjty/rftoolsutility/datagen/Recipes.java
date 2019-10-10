package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', ModItems.MACHINE_FRAME);
        add('A', ModItems.MACHINE_BASE);
        add('s', ModItems.DIMENSIONALSHARD);
        group("rftools");
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER1)
                .key('C', Blocks.CRAFTING_TABLE)
                .addCriterion("machine_frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                " T ", "CFC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER2)
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterSetup.BLOCK_CRAFTER1)
                .addCriterion("crafter1", InventoryChangeTrigger.Instance.forItems(CrafterSetup.BLOCK_CRAFTER1)),
                " T ", "CMC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterSetup.BLOCK_CRAFTER3)
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterSetup.BLOCK_CRAFTER2)
                .addCriterion("crafter2", InventoryChangeTrigger.Instance.forItems(CrafterSetup.BLOCK_CRAFTER2)),
                " T ", "CMC", " T ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.DIALING_DEVICE)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                "rrr", "TFT", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_RECEIVER)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                "iii", "rFr", "ooo");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_TRANSMITTER)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                "ooo", "rFr", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_BOOSTER)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                " R ", "RFR", " R ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.SIMPLE_DIALER)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE)),
                "rRr", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.CHARGED_PORTER)
                .addCriterion("pearl", InventoryChangeTrigger.Instance.forItems(Items.ENDER_PEARL)),
                " o ", "oRo" , "ioi");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(TeleporterSetup.ADVANCED_CHARGED_PORTER)
                .key('M', TeleporterSetup.CHARGED_PORTER)
                .addCriterion("porter", InventoryChangeTrigger.Instance.forItems(TeleporterSetup.CHARGED_PORTER)),
                "RdR", "dMd", "RdR");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(TankSetup.BLOCK_TANK)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                "GGG", "bFb", "iii");
    }
}
