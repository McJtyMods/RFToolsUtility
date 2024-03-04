package mcjty.rftoolsutility.modules.tank;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TankConfiguration {

    public static final String CATEGORY_TANK = "tank";

    public static ModConfigSpec.IntValue MAXCAPACITY;

    public static void init(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the tank").push(CATEGORY_TANK);
        CLIENT_BUILDER.comment("Settings for the tank").push(CATEGORY_TANK);

        MAXCAPACITY = SERVER_BUILDER
                .comment("Maximum tank capacity (in mb)")
                .defineInRange("maxCapacity", 32000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
