package mcjty.rftoolsutility.compat;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TheOneProbeSupport implements Function<ITheOneProbe, Void> {

    public static ITheOneProbe probe;

    public static ResourceLocation ELEMENT_SEQUENCER = new ResourceLocation(RFToolsUtility.MODID, "elementseq");

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        probe = theOneProbe;
        Logging.log("Enabled support for The One Probe");
        probe.registerElementFactory(new IElementFactory() {
            @Override
            public IElement createElement(FriendlyByteBuf buf) {
                return new ElementSequencer(buf);
            }

            @Override
            public ResourceLocation getId() {
                return ELEMENT_SEQUENCER;
            }
        });
        return null;
    }

    public static IProbeInfo addSequenceElement(IProbeInfo probeInfo, long bits, int current, boolean large) {
        return probeInfo.element(new ElementSequencer(bits, current, large));
    }
}