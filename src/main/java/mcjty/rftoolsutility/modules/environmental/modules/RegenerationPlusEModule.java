package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class RegenerationPlusEModule extends PotionEffectModule {

    public RegenerationPlusEModule() {
        super("regeneration", 2);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.REGENERATIONPLUS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_REGENERATIONPLUS;
    }
}
