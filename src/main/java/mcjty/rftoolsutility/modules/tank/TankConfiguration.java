package mcjty.rftoolsutility.modules.tank;

import net.minecraftforge.common.ForgeConfigSpec;

public class TankConfiguration {

    public static final String CATEGORY_TANK = "tank";

    public static ForgeConfigSpec.IntValue MAXCAPACITY;

    public static void init(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Settings for the tank").push(CATEGORY_TANK);
        CLIENT_BUILDER.comment("Settings for the tank").push(CATEGORY_TANK);

        MAXCAPACITY = COMMON_BUILDER
                .comment("Maximum tank capacity (in mb)")
                .defineInRange("maxCapacity", 32000, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
