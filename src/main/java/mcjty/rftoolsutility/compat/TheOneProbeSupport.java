package mcjty.rftoolsutility.compat;

import mcjty.lib.varia.Logging;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TheOneProbeSupport implements Function<ITheOneProbe, Void> {

    public static ITheOneProbe probe;

    public static int ELEMENT_SEQUENCER;

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        Logging.log("Enabled support for The One Probe");
        ELEMENT_SEQUENCER = probe.registerElementFactory(ElementSequencer::new);
        return null;
    }

    public static IProbeInfo addSequenceElement(IProbeInfo probeInfo, long bits, int current, boolean large) {
        return probeInfo.element(new ElementSequencer(bits, current, large));
    }
}