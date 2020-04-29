package mcjty.rftoolsutility.modules.tank;

import net.minecraftforge.common.ForgeConfigSpec;

public class TankConfiguration {

    public static final String CATEGORY_TANK = "tank";

    public static ForgeConfigSpec.IntValue MAXCAPACITY;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the tank").push(CATEGORY_TANK);
        CLIENT_BUILDER.comment("Settings for the tank").push(CATEGORY_TANK);

        MAXCAPACITY = SERVER_BUILDER
                .comment("Maximum tank capacity (in mb)")
                .defineInRange("maxCapacity", 32000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
