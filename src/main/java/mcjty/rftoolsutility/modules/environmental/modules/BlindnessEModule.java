package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class BlindnessEModule extends PotionEffectModule {

    public BlindnessEModule() {
        super("blindness", 0);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.BLINDNESS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_BLINDNESS;
    }

    @Override
    protected boolean allowedForPlayers() {
        return EnvironmentalConfiguration.blindnessAvailable.get();
    }
}
