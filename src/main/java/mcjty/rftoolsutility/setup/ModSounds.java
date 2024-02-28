package mcjty.rftoolsutility.setup;

import mcjty.lib.varia.SoundTools;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModSounds {

    public static final Supplier<SoundEvent> WHOOSH = Registration.SOUNDS.register("teleport_whoosh", () -> SoundTools.createSoundEvent(new ResourceLocation(RFToolsUtility.MODID, "teleport_whoosh")));
    public static final Supplier<SoundEvent> ERROR = Registration.SOUNDS.register("teleport_error", () -> SoundTools.createSoundEvent(new ResourceLocation(RFToolsUtility.MODID, "teleport_error")));

    public static void init() {
    }

}
