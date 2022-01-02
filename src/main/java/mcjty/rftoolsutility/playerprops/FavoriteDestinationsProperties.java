package mcjty.rftoolsutility.playerprops;

import mcjty.lib.varia.LevelTools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FavoriteDestinationsProperties {

    private Set<GlobalPos> favoriteDestinations = new HashSet<>();

    public FavoriteDestinationsProperties() {
    }

    public void copyFrom(FavoriteDestinationsProperties source) {
        favoriteDestinations = new HashSet<>(source.favoriteDestinations);
    }

    public boolean isDestinationFavorite(GlobalPos coordinate) {
        return favoriteDestinations.contains(coordinate);
    }

    public void setDestinationFavorite(GlobalPos coordinate, boolean favorite) {
        if (favorite) {
            favoriteDestinations.add(coordinate);
        } else {
            favoriteDestinations.remove(coordinate);
        }
    }
    public void saveNBTData(CompoundTag compound) {
        writeFavoritesToNBT(compound, favoriteDestinations);
    }

    private static void writeFavoritesToNBT(CompoundTag tagCompound, Collection<GlobalPos> destinations) {
        ListTag lst = new ListTag();
        for (GlobalPos destination : destinations) {
            CompoundTag tc = new CompoundTag();
            BlockPos c = destination.pos();
            tc.putInt("x", c.getX());
            tc.putInt("y", c.getY());
            tc.putInt("z", c.getZ());
            tc.putString("dim", destination.dimension().location().toString());
            lst.add(tc);
        }
        tagCompound.put("destinations", lst);
    }

    public void loadNBTData(CompoundTag compound) {
        favoriteDestinations.clear();
        readCoordinatesFromNBT(compound, favoriteDestinations);
    }

    private static void readCoordinatesFromNBT(CompoundTag tagCompound, Set<GlobalPos> destinations) {
        ListTag lst = tagCompound.getList("destinations", net.minecraftforge.common.util.Tag.TAG_COMPOUND);
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundTag tc = lst.getCompound(i);
            BlockPos c = new BlockPos(tc.getInt("x"), tc.getInt("y"), tc.getInt("z"));
            destinations.add(GlobalPos.of(LevelTools.getId(tc.getString("dim")), c));
        }
    }

}
