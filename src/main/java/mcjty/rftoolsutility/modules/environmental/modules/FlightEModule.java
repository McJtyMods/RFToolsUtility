package mcjty.rftoolsutility.modules.environmental.modules;


import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.playerprops.PlayerBuff;

public class FlightEModule extends BuffEModule {

    public FlightEModule() {
        super(PlayerBuff.BUFF_FLIGHT);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.FLIGHT_RFPERTICK.get();
    }
}
