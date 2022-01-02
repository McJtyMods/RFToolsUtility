package mcjty.rftoolsutility.playerprops;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerExtendedProperties {

    public static Capability<FavoriteDestinationsProperties> FAVORITE_DESTINATIONS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static Capability<BuffProperties> BUFF_CAPABILITY;

    public static LazyOptional<FavoriteDestinationsProperties> getFavoriteDestinations(Player player) {
        return player.getCapability(FAVORITE_DESTINATIONS_CAPABILITY);
    }

    public static LazyOptional<BuffProperties> getBuffProperties(Player player) {
        return player.getCapability(BUFF_CAPABILITY);
    }
}
