package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.varia.NamedEnum;

public enum InvCheckerDamageMode implements NamedEnum<InvCheckerDamageMode> {
    DMG_MATCH("Match"),
    DMG_IGNORE("Ignore");

    private final String name;

    InvCheckerDamageMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getDescription() {
        return new String[] { name };
    }
}
