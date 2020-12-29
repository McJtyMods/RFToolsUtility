package mcjty.rftoolsutility.modules.spawner;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public class SpawnerConfiguration {

    public static final String CATEGORY_SPAWNER = "spawner";

    public static final ResourceLocation LIVING = new ResourceLocation("rftoolsutility", "living/living");
    public static final ITag.INamedTag<Item> TAG_LIVING = tagItem(LIVING);

    public static final ResourceLocation LOWYIELD = new ResourceLocation("rftoolsutility", "living/lowyield");
    public static final ITag.INamedTag<Item> TAG_LOWYIELD = tagItem(LOWYIELD);

    public static final ResourceLocation HIGHYIELD = new ResourceLocation("rftoolsutility", "living/highyield");
    public static final ITag.INamedTag<Item> TAG_HIGHYIELD = tagItem(HIGHYIELD);

    public static final ResourceLocation AVERAGEYIELD = new ResourceLocation("rftoolsutility", "living/averageyield");
    public static final ITag.INamedTag<Item> TAG_AVERAGEYIELD = tagItem(AVERAGEYIELD);

    private static ITag.INamedTag<Item> tagItem(ResourceLocation id) {
        return ItemTags.makeWrapperTag(id.toString());
    }

    public static int SPAWNER_MAXENERGY = 200000;
    public static int SPAWNER_RECEIVEPERTICK = 2000;

    public static int BEAMER_MAXENERGY = 200000;
    public static int BEAMER_RECEIVEPERTICK = 1000;
    public static int beamRfPerObject = 2000;
    public static int beamBlocksPerSend = 1;
    public static int maxBeamDistance = 8;
    public static int maxMatterStorage = 64 * 100;

    public static ForgeConfigSpec.IntValue maxMobInjections;        // Maximum amount of injections we need to do a full mob extraction.

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the spawner system").push(CATEGORY_SPAWNER);
        CLIENT_BUILDER.comment("Settings for the spawner system").push(CATEGORY_SPAWNER);

        maxMobInjections = SERVER_BUILDER
                .comment("Maximum amount of injections we need to do a full mob extraction.")
                .defineInRange("maxMobInjections", 10, 1, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();
        SERVER_BUILDER.pop();
    }


}
