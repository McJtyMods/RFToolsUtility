package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class FeatherFallingEModule extends BuffEModule {

    public FeatherFallingEModule() {
        super(PlayerBuff.BUFF_FEATHERFALLING);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.FEATHERFALLING_RFPERTICK.get();
    }
}
