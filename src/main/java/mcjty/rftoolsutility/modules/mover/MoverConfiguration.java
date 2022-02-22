package mcjty.rftoolsutility.modules.mover;

import net.minecraftforge.common.ForgeConfigSpec;

public class MoverConfiguration {

    public static final String CATEGORY_MOVER = "mover";

    public static ForgeConfigSpec.IntValue MAXENERGY;
    public static ForgeConfigSpec.IntValue RECEIVEPERTICK;
    public static ForgeConfigSpec.IntValue rfPerOperation;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the crafter").push(CATEGORY_MOVER);
        CLIENT_BUILDER.comment("Settings for the crafter").push(CATEGORY_MOVER);

        rfPerOperation = SERVER_BUILDER
                .comment("Amount of RF used per tick while moving")
                .defineInRange("rfPerTickOperation", 100, 0, Integer.MAX_VALUE);
        MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the mover can hold")
                .defineInRange("moverMaxRF", 100000, 0, Integer.MAX_VALUE);
        RECEIVEPERTICK = SERVER_BUILDER
                .comment("RF per tick that the mover can receive")
                .defineInRange("moverRFPerTick", 1000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
