package mcjty.rftoolsutility.compat;

import mcjty.rftoolsbase.api.dimension.IDimensionInformation;
import mcjty.rftoolsbase.api.dimension.IDimensionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

import java.util.function.Function;

public class RFToolsDimCompat {

    public static void register() {
        if (ModList.get().isLoaded("rftoolsdim")) {
            registerInternal();
        }
    }

    private static boolean registered = false;
    public static IDimensionManager dimensionManager = null;

    private static void registerInternal() {
        if (registered) {
            return;
        }
        registered = true;
        InterModComms.sendTo("rftoolsdim", "getDimensionManager", GetDimensionManager::new);
    }

    public static int getPowerPercentage(Level world, ResourceLocation id) {
        if (dimensionManager != null) {
            IDimensionInformation data = dimensionManager.getDimensionInformation(world, id);
            if (data != null) {
                long maxEnergy = data.getMaxEnergy(world);
                if (maxEnergy <= 0) {
                    return -1;
                }
                return (int) (data.getEnergy() * 100 / maxEnergy);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }


    public static class GetDimensionManager implements Function<IDimensionManager, Void> {

        @Override
        public Void apply(IDimensionManager tm) {
            RFToolsDimCompat.dimensionManager = tm;
            return null;
        }
    }
}
