package mcjty.rftoolsutility.modules.logic.tools;

import mcjty.lib.varia.NamedEnum;

public enum GroupType implements NamedEnum<GroupType> {
    GROUP_ONE("One", "At least one detected", "to match the sensor"),
    GROUP_ALL("All", "All blocks in area", "must match the sensor");

    private final String name;
    private final String[] description;

    GroupType(String name, String... description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getDescription() {
        return description;
    }
}
