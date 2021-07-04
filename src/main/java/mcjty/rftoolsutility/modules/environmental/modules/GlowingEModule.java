package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class GlowingEModule extends PotionEffectModule {

    public GlowingEModule() {
        super("glowing", 0);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.HASTE_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_GLOWING;
    }
}
