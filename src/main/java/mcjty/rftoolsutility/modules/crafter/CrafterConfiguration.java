package mcjty.rftoolsutility.modules.crafter;

import net.minecraftforge.common.ForgeConfigSpec;

public class CrafterConfiguration {

    public static final String CATEGORY_CRAFTER = "crafter";

    public static ForgeConfigSpec.IntValue MAXENERGY;
    public static ForgeConfigSpec.IntValue RECEIVEPERTICK;
    public static ForgeConfigSpec.IntValue rfPerOperation;
    public static ForgeConfigSpec.IntValue speedOperations;

    public static void init(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Settings for the crafter").push(CATEGORY_CRAFTER);
        CLIENT_BUILDER.comment("Settings for the crafter").push(CATEGORY_CRAFTER);

        rfPerOperation = COMMON_BUILDER
                .comment("Amount of RF used per crafting operation")
                .defineInRange("rfPerOperation", 100, 0, Integer.MAX_VALUE);
        speedOperations = COMMON_BUILDER
                .comment("How many operations to do at once in fast mode")
                .defineInRange("speedOperations", 5, 0, Integer.MAX_VALUE);
        MAXENERGY = COMMON_BUILDER
                .comment("Maximum RF storage that the crafter can hold")
                .defineInRange("crafterMaxRF", 50000, 0, Integer.MAX_VALUE);
        RECEIVEPERTICK = COMMON_BUILDER
                .comment("RF per tick that the crafter can receive")
                .defineInRange("crafterRFPerTick", 500, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
