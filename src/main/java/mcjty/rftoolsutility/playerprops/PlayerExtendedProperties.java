package mcjty.rftoolsutility.playerprops;

import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.world.entity.player.Player;

public class PlayerExtendedProperties {

    public static FavoriteDestinationsProperties getFavoriteDestinations(Player player) {
        return player.getAttachedOrCreate(Registration.ATTACHMENT_TYPE_FAVORITE_DESTINATIONS_PROPERTIES);
    }

    public static void setFavoriteDestinations(Player player, FavoriteDestinationsProperties properties) {
        player.setAttached(Registration.ATTACHMENT_TYPE_FAVORITE_DESTINATIONS_PROPERTIES, properties);
    }

    public static BuffProperties getBuffProperties(Player player) {
        return player.getAttachedOrCreate(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES);
    }

    public static void setBuffProperties(Player player, BuffProperties properties) {
        player.setAttached(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES, properties);
    }
}
