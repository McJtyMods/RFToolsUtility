package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class WeaknessEModule extends PotionEffectModule {

    public WeaknessEModule() {
        super("weakness", 1);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.WEAKNESS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_WEAKNESS;
    }

    @Override
    protected boolean allowedForPlayers() {
        return EnvironmentalConfiguration.weaknessAvailable.get();
    }

}
