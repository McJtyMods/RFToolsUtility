package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class SaturationPlusEModule extends PotionEffectModule {

    public SaturationPlusEModule() {
        super("saturation", 2);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.SATURATIONPLUS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_SATURATIONPLUS;
    }
}
