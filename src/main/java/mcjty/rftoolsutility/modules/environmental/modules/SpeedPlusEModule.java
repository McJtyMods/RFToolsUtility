package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class SpeedPlusEModule extends PotionEffectModule {

    public SpeedPlusEModule() {
        super("speed", 2);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.SPEEDPLUS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_SPEEDPLUS;
    }
}
