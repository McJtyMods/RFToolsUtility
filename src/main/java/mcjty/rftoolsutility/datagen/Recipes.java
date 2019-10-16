package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', ModItems.MACHINE_FRAME);
        add('A', ModItems.MACHINE_BASE);
        add('s', ModItems.DIMENSIONALSHARD);
        add('Z', Tags.Items.DYES_BLACK);
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
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.SCREEN)
                        .addCriterion("base", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE)),
                "GGG", "GAG", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.SCREEN_CONTROLLER)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME)),
                "ror", "GFG", "rGr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.TEXT_MODULE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " p ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.BUTTON_MODULE)
                        .key('X', Items.STONE_BUTTON)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.CLOCK_MODULE)
                        .key('X', Items.CLOCK)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COMPUTER_MODULE)
//                        .key('X', Items.QUARTZ)
//                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COUNTERPLUS_MODULE)
//                        .key('z', Tags.Items.INGOTS_GOLD)
//                        .key('M', ScreenSetup.COUNTER_MODULE)
//                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
//                " o ", "zMz", " o ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COUNTER_MODULE)
//                        .key('X', Items.COMPARATOR)
//                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.DUMP_MODULE)
//                        .key('X', ItemTags.WOODEN_BUTTONS)
//                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ELEVATOR_MODULE)
//                        .key('X', Items.STONE_BUTTON)
//                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
//                "XXX", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ENERGY_MODULE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " r ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ENERGYPLUS_MODULE)
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenSetup.ENERGY_MODULE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.FLUID_MODULE)
                        .key('X', Items.BUCKET)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.FLUIDPLUS_MODULE)
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenSetup.FLUID_MODULE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.INVENTORY_MODULE)
                        .key('X', Tags.Items.CHESTS)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.INVENTORYPLUS_MODULE)
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenSetup.INVENTORY_MODULE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.MACHINEINFORMATION_MODULE)
                        .key('X', Items.FURNACE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.STORAGECONTROL_MODULE)
                        .key('X', Items.CRAFTING_TABLE)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.REDSTONE_MODULE)
                        .key('X', Items.REPEATER)
                        .addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)),
                " X ", "rir", " Z ");

    }
}
