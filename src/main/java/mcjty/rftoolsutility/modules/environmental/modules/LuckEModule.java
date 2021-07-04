package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class LuckEModule extends PotionEffectModule {

    public LuckEModule() {
        super("luck", 0);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.LUCK_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_LUCK;
    }
}
