package mcjty.rftoolsutility.playerprops;

import mcjty.rftoolsutility.setup.Registration;
import net.minecraft.world.entity.player.Player;

public class PlayerExtendedProperties {

    public static FavoriteDestinationsProperties getFavoriteDestinations(Player player) {
        return player.getData(Registration.ATTACHMENT_TYPE_FAVORITE_DESTINATIONS_PROPERTIES.get());
    }

    public static void setFavoriteDestinations(Player player, FavoriteDestinationsProperties properties) {
        player.setData(Registration.ATTACHMENT_TYPE_FAVORITE_DESTINATIONS_PROPERTIES.get(), properties);
    }

    public static BuffProperties getBuffProperties(Player player) {
        return player.getData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES.get());
    }

    public static void setBuffProperties(Player player, BuffProperties properties) {
        player.setData(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES.get(), properties);
    }
}
