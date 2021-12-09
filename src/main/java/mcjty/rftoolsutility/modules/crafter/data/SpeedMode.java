package mcjty.rftoolsutility.modules.crafter.data;

import mcjty.lib.varia.NamedEnum;

public enum SpeedMode implements NamedEnum<SpeedMode> {
    SLOW("Slow"),
    FAST("Fast");

    private final String description;

    SpeedMode(String description) {
        this.description = description;
    }


    @Override
    public String getName() {
        return description;
    }

    @Override
    public String[] getDescription() {
        return new String[]{description};
    }
}
