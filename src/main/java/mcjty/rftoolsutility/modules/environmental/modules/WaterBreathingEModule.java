package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class WaterBreathingEModule extends PotionEffectModule {

    public WaterBreathingEModule() {
        super("water_breathing", 0);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.WATERBREATHING_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_WATERBREATHING;
    }
}
