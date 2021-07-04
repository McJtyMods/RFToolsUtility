package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class PoisonEModule extends PotionEffectModule {

    public PoisonEModule() {
        super("poison", 1);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.POISON_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_POISON;
    }

    @Override
    protected boolean allowedForPlayers() {
        return EnvironmentalConfiguration.poisonAvailable.get();
    }

}
