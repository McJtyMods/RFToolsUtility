package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class HastePlusEModule extends PotionEffectModule {

    public HastePlusEModule() {
        super("haste", 2);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.HASTEPLUS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_HASTEPLUS;
    }
}
