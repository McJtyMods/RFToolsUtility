package mcjty.rftoolsutility.playerprops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteDestinationsProperties {

    private Set<GlobalPos> favoriteDestinations = new HashSet<>();

    public FavoriteDestinationsProperties() {
    }

    public void copyFrom(FavoriteDestinationsProperties source) {
        favoriteDestinations = new HashSet<>(source.favoriteDestinations);
    }

    public static final Codec<FavoriteDestinationsProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.listOf().fieldOf("destinations").forGetter(FavoriteDestinationsProperties::getFavoriteDestinations)
    ).apply(instance, FavoriteDestinationsProperties::new));

    public boolean isDestinationFavorite(GlobalPos coordinate) {
        return favoriteDestinations.contains(coordinate);
    }

    public FavoriteDestinationsProperties(List<GlobalPos> favoriteDestinations) {
        this.favoriteDestinations = new HashSet<>(favoriteDestinations);
    }

    public List<GlobalPos> getFavoriteDestinations() {
        return List.copyOf(favoriteDestinations);
    }

    public void setDestinationFavorite(GlobalPos coordinate, boolean favorite) {
        if (favorite) {
            favoriteDestinations.add(coordinate);
        } else {
            favoriteDestinations.remove(coordinate);
        }
    }
}
