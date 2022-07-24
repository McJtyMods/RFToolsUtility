package mcjty.rftoolsutility.setup;

import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final RegistryObject<SoundEvent> WHOOSH = Registration.SOUNDS.register("teleport_whoosh", () -> new SoundEvent(new ResourceLocation(RFToolsUtility.MODID, "teleport_whoosh")));
    public static final RegistryObject<SoundEvent> ERROR = Registration.SOUNDS.register("teleport_error", () -> new SoundEvent(new ResourceLocation(RFToolsUtility.MODID, "teleport_error")));

    public static void init() {
    }

}
