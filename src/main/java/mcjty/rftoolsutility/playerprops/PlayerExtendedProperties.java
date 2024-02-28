package mcjty.rftoolsutility.playerprops;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.util.LazyOptional;

public class PlayerExtendedProperties {

    public static Capability<FavoriteDestinationsProperties> FAVORITE_DESTINATIONS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static Capability<BuffProperties> BUFF_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});;

    public static LazyOptional<FavoriteDestinationsProperties> getFavoriteDestinations(Player player) {
        return player.getCapability(FAVORITE_DESTINATIONS_CAPABILITY);
    }

    public static LazyOptional<BuffProperties> getBuffProperties(Player player) {
        return player.getCapability(BUFF_CAPABILITY);
    }
}
