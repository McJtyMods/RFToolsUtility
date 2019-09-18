package mcjty.rftoolsutility.playerprops;

import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FavoriteDestinationsProperties {

    private Set<GlobalCoordinate> favoriteDestinations = new HashSet<>();

    public FavoriteDestinationsProperties() {
    }

    public void copyFrom(FavoriteDestinationsProperties source) {
        favoriteDestinations = new HashSet<>(source.favoriteDestinations);
    }

    public boolean isDestinationFavorite(GlobalCoordinate coordinate) {
        return favoriteDestinations.contains(coordinate);
    }

    public void setDestinationFavorite(GlobalCoordinate coordinate, boolean favorite) {
        if (favorite) {
            favoriteDestinations.add(coordinate);
        } else {
            favoriteDestinations.remove(coordinate);
        }
    }
    public void saveNBTData(CompoundNBT compound) {
        writeFavoritesToNBT(compound, favoriteDestinations);
    }

    private static void writeFavoritesToNBT(CompoundNBT tagCompound, Collection<GlobalCoordinate> destinations) {
        ListNBT lst = new ListNBT();
        for (GlobalCoordinate destination : destinations) {
            CompoundNBT tc = new CompoundNBT();
            BlockPos c = destination.getCoordinate();
            tc.putInt("x", c.getX());
            tc.putInt("y", c.getY());
            tc.putInt("z", c.getZ());
            tc.putString("dim", destination.getDimension().getRegistryName().toString());
            lst.add(tc);
        }
        tagCompound.put("destinations", lst);
    }

    public void loadNBTData(CompoundNBT compound) {
        favoriteDestinations.clear();
        readCoordinatesFromNBT(compound, favoriteDestinations);
    }

    private static void readCoordinatesFromNBT(CompoundNBT tagCompound, Set<GlobalCoordinate> destinations) {
        ListNBT lst = tagCompound.getList("destinations", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundNBT tc = lst.getCompound(i);
            BlockPos c = new BlockPos(tc.getInt("x"), tc.getInt("y"), tc.getInt("z"));
            String dim = tc.getString("dim");
            destinations.add(new GlobalCoordinate(c, DimensionType.byName(new ResourceLocation(dim))));
        }
    }

}
