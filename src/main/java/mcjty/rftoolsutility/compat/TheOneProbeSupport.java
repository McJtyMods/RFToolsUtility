package mcjty.rftoolsutility.compat;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.TheOneProbePlugin;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.function.Function;
import javax.annotation.Nullable;

public class TheOneProbeSupport implements Function<ITheOneProbe, Void>, TheOneProbePlugin {

    public static ITheOneProbe probe;

    public static Identifier ELEMENT_SEQUENCER = Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "elementseq");

    @Override
    public void register(ITheOneProbe probe) {
        apply(probe);
    }

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        Logging.log("Enabled support for The One Probe");
        probe.registerElementFactory(new IElementFactory() {

            @Override
            public IElement createElement(RegistryFriendlyByteBuf buf) {
                return new ElementSequencer(buf);
            }

            @Override
            public Identifier getId() {
                return ELEMENT_SEQUENCER;
            }
        });
        return null;
    }

    public static IProbeInfo addSequenceElement(IProbeInfo probeInfo, long bits, int current, boolean large) {
        return probeInfo.element(new ElementSequencer(bits, current, large));
    }
}
