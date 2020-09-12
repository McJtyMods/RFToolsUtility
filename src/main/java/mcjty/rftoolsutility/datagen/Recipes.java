package mcjty.rftoolsutility.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
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
        add('F', VariousModule.MACHINE_FRAME.get());
        add('A', VariousModule.MACHINE_BASE.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
        add('Z', Tags.Items.DYES_BLACK);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER1.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                " T ", "CFC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER2.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterModule.CRAFTER1.get())
                .addCriterion("crafter1", hasItem(CrafterModule.CRAFTER1.get())),
                " T ", "CMC", " T ");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(CrafterModule.CRAFTER3.get())
                .key('C', Blocks.CRAFTING_TABLE)
                .key('M', CrafterModule.CRAFTER2.get())
                .addCriterion("crafter2", hasItem(CrafterModule.CRAFTER2.get())),
                " T ", "CMC", " T ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.DIALING_DEVICE.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "rrr", "TFT", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.MATTER_RECEIVER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "iii", "rFr", "ooo");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.MATTER_TRANSMITTER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "ooo", "rFr", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.MATTER_BOOSTER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                " R ", "RFR", " R ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.SIMPLE_DIALER.get())
                .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rRr", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TeleporterModule.CHARGED_PORTER.get())
                .addCriterion("pearl", hasItem(Items.ENDER_PEARL)),
                " o ", "oRo" , "ioi");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(TeleporterModule.ADVANCED_CHARGED_PORTER.get())
                .key('M', TeleporterModule.CHARGED_PORTER.get())
                .addCriterion("porter", hasItem(TeleporterModule.CHARGED_PORTER.get())),
                "RdR", "dMd", "RdR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(TankModule.TANK.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "GGG", "bFb", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.SCREEN.get())
                        .addCriterion("base", hasItem(VariousModule.MACHINE_BASE.get())),
                "GGG", "GAG", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.SCREEN_CONTROLLER.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "ror", "GFG", "rGr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.TEXT_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " p ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.BUTTON_MODULE.get())
                        .key('X', Items.STONE_BUTTON)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.CLOCK_MODULE.get())
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
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.ENERGY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " r ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.ENERGYPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenModule.ENERGY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.FLUID_MODULE.get())
                        .key('X', Items.BUCKET)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.FLUIDPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenModule.FLUID_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.INVENTORY_MODULE.get())
                        .key('X', Tags.Items.CHESTS)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.INVENTORYPLUS_MODULE.get())
                        .key('z', Tags.Items.INGOTS_GOLD)
                        .key('M', ScreenModule.INVENTORY_MODULE.get())
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " o ", "zMz", " o ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.MACHINEINFORMATION_MODULE.get())
                        .key('X', Items.FURNACE)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.REDSTONE_MODULE.get())
                        .key('X', Items.REPEATER)
                        .addCriterion("ingot", hasItem(Items.IRON_INGOT)),
                " X ", "rir", " Z ");

        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.ANALOG.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rAC");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.COUNTER.get())
                        .key('C', Items.CLOCK)
                        .key('g', Items.GOLD_NUGGET)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "gCg", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.DIGIT.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "PPP", "rAr", "PPP");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.INVCHECKER.get())
                        .key('P', Items.COMPARATOR)
                        .key('C', Tags.Items.CHESTS)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " C ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.SENSOR.get())
                        .key('C', Items.COMPARATOR)
                        .key('x', Tags.Items.GEMS_QUARTZ)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "xCx", "rAr", "xCx");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.SEQUENCER.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rTr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.LOGIC.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rCr", "CAC", "rCr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.TIMER.get())
                        .key('C', Items.CLOCK)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rCr", "TAT", "rTr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.WIRE.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "rAr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.REDSTONE_RECEIVER.get())
                        .key('C', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "ror", "CAC", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.REDSTONE_TRANSMITTER.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                "ror", "TAT", "rRr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(LogicBlockModule.REDSTONE_INFORMATION.get())
                        .addCriterion("redstone", hasItem(Items.REDSTONE)),
                "ror", "rRr", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ScreenModule.SCREEN_LINK.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("redstone", hasItem(Items.REDSTONE)),
                "ror", "PPP", "rrr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(SpawnerModule.SPAWNER.get())
                        .key('z', Items.ROTTEN_FLESH)
                        .key('P', Tags.Items.BONES)
                        .key('X', Tags.Items.RODS_BLAZE)
                        .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "rzr", "oFX", "rPr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(SpawnerModule.MATTER_BEAMER.get())
                        .key('z', Blocks.GLOWSTONE)
                        .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "RzR", "zFz", "RzR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(SpawnerModule.SYRINGE.get())
                        .key('z', Items.GLASS_BOTTLE)
                        .addCriterion("machine_frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "i  ", " i ", "  z");
    }
}
