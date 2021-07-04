package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class FeatherFallingPlusEModule extends BuffEModule {

    public FeatherFallingPlusEModule() {
        super(PlayerBuff.BUFF_FEATHERFALLINGPLUS);
    }

    @Override
    public float getRfPerTick() {
        return (float) (double) EnvironmentalConfiguration.FEATHERFALLINGPLUS_RFPERTICK.get();
    }
}
