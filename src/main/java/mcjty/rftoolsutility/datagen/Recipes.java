package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousSetup;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
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
        add('F', VariousSetup.MACHINE_FRAME.get());
        add('A', VariousSetup.MACHINE_BASE.get());
        add('s', VariousSetup.DIMENSIONALSHARD.get());
        add('Z', Tags.Items.DYES_BLACK);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(CrafterSetup.CRAFTER1.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .addCriterion("machine_frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                " T ", "CFC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterSetup.CRAFTER2.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterSetup.CRAFTER1.get())
                .addCriterion("crafter1", hasItem(CrafterSetup.CRAFTER1.get())),
                " T ", "CMC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterSetup.CRAFTER3.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterSetup.CRAFTER2.get())
                .addCriterion("crafter2", hasItem(CrafterSetup.CRAFTER2.get())),
                " T ", "CMC", " T ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.DIALING_DEVICE.get())
                .addCriterion("frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                "rrr", "TFT", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_RECEIVER.get())
                .addCriterion("frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                "iii", "rFr", "ooo");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_TRANSMITTER.get())
                .addCriterion("frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                "ooo", "rFr", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.MATTER_BOOSTER.get())
                .addCriterion("frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                " R ", "RFR", " R ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.SIMPLE_DIALER.get())
                .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "rRr", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterSetup.CHARGED_PORTER.get())
                .addCriterion("pearl", hasItem(Items.ENDER_PEARL)),
                " o ", "oRo" , "ioi");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(TeleporterSetup.ADVANCED_CHARGED_PORTER.get())
                .key('M', TeleporterSetup.CHARGED_PORTER.get())
                .addCriterion("porter", hasItem(TeleporterSetup.CHARGED_PORTER.get())),
                "RdR", "dMd", "RdR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TankSetup.TANK.get())
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                "GGG", "bFb", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.SCREEN.get())
                        .addCriterion("base", hasItem(VariousSetup.MACHINE_BASE.get())),
                "GGG", "GAG", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.SCREEN_CONTROLLER.get())
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_FRAME.get())),
                "ror", "GFG", "rGr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.TEXT_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " p ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.BUTTON_MODULE.get())
                        .key('X', Items.STONE_BUTTON)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.CLOCK_MODULE.get())
                        .key('X', Items.CLOCK)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COMPUTER_MODULE)
//                        .key('X', Items.QUARTZ)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COUNTERPLUS_MODULE)
//                        .key('z', Tags.Items.INGOTS_GOLD)
//                        .key('M', ScreenSetup.COUNTER_MODULE)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                " o ", "zMz", " o ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.COUNTER_MODULE)
//                        .key('X', Items.COMPARATOR)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                " X ", "rir", " Z ");
//        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ELEVATOR_MODULE)
//                        .key('X', Items.STONE_BUTTON)
//                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
//                "XXX", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ENERGY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " r ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.ENERGYPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenSetup.ENERGY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.FLUID_MODULE.get())
                        .key('X', Items.BUCKET)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.FLUIDPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenSetup.FLUID_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.INVENTORY_MODULE.get())
                        .key('X', Tags.Items.CHESTS)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.INVENTORYPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenSetup.INVENTORY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.MACHINEINFORMATION_MODULE.get())
                        .key('X', Items.FURNACE)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.REDSTONE_MODULE.get())
                        .key('X', Items.REPEATER)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.ANALOG.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "rAC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.COUNTER.get())
                        .key('C', Items.CLOCK)
                        .key('g', Items.GOLD_NUGGET)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "gCg", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.DIGIT.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "PPP", "rAr", "PPP");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.INVCHECKER.get())
                        .key('P', Items.COMPARATOR)
                        .key('C', Tags.Items.CHESTS)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                " P ", "rAr", " C ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.SENSOR.get())
                        .key('C', Items.COMPARATOR)
                        .key('x', Tags.Items.GEMS_QUARTZ)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "xCx", "rAr", "xCx");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.SEQUENCER.get())
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "rTr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.LOGIC.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "rCr", "CAC", "rCr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.TIMER.get())
                        .key('C', Items.CLOCK)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "rCr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.WIRE.get())
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "rAr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.REDSTONE_RECEIVER.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "ror", "CAC", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.REDSTONE_TRANSMITTER.get())
                        .addCriterion("frame", hasItem(VariousSetup.MACHINE_BASE.get())),
                "ror", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockSetup.REDSTONE_INFORMATION.get())
                        .addCriterion("redstone", hasItem(Items.REDSTONE)),
                "ror", "rRr", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenSetup.SCREEN_LINK.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("redstone", hasItem(Items.REDSTONE)),
                "ror", "PPP", "rrr");
    }
}
