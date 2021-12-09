package mcjty.rftoolsutility.modules.crafter.data;

import mcjty.lib.varia.NamedEnum;

public enum KeepMode implements NamedEnum<KeepMode> {
    ALL("All"),
    KEEP("Keep");

    private final String description;

    KeepMode(String description) {
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
