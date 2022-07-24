package mcjty.rftoolsutility.datagen;

import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsUtility.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(event.includeServer(), new Recipes(generator));
            generator.addProvider(event.includeServer(), new LootTables(generator));
            BlockTags blockTags = new BlockTags(generator, event.getExistingFileHelper());
            generator.addProvider(event.includeServer(), blockTags);
        }
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(), new BlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(event.includeClient(), new Items(generator, event.getExistingFileHelper()));
        }
    }
}
