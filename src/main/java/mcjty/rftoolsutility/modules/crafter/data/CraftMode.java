package mcjty.rftoolsutility.modules.crafter.data;

import mcjty.lib.varia.NamedEnum;

public enum CraftMode implements NamedEnum<CraftMode> {
    EXT("Ext"),
    INT("Int"),
    EXTC("ExtC");

    private final String description;

    CraftMode(String description) {
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
