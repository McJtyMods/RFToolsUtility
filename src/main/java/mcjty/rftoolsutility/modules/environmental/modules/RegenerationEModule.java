package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class RegenerationEModule extends PotionEffectModule {

    public RegenerationEModule() {
        super("regeneration", 0);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.REGENERATION_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_REGENERATION;
    }
}
