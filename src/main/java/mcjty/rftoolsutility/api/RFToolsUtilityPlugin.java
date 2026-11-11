package mcjty.rftoolsutility.api;

import mcjty.rftoolsbase.api.screens.IScreenModuleRegistry;
import mcjty.rftoolsbase.api.teleportation.ITeleportationManager;

/** Declare a "rftoolsutility" Fabric entrypoint to register modules or obtain the teleportation API. */
@FunctionalInterface
public interface RFToolsUtilityPlugin {

    void register(ITeleportationManager teleportationManager, IScreenModuleRegistry screenModuleRegistry);
}
