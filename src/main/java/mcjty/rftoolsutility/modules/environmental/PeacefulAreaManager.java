package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;
import mcjty.rftoolsutility.modules.environmental.modules.PeacefulEModule;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeacefulAreaManager {
    private static final Map<GlobalCoordinate,PeacefulArea> areas = new HashMap<>();

    public static void markArea(GlobalCoordinate coordinate, int radius, int miny, int maxy) {
        if (areas.containsKey(coordinate)) {
            areas.get(coordinate).touch().setArea(radius, miny, maxy);
        } else {
            PeacefulArea area = new PeacefulArea(radius, miny, maxy);
            areas.put(coordinate, area);
        }
    }

    public static boolean isPeaceful(GlobalCoordinate coordinate) {
        if (areas.isEmpty()) {
            return false;
        }
        List<GlobalCoordinate> toRemove = new ArrayList<>();
        boolean peaceful = false;
        long curtime = System.currentTimeMillis() - 10000;

        for (Map.Entry<GlobalCoordinate, PeacefulArea> entry : areas.entrySet()) {
            PeacefulArea area = entry.getValue();
            GlobalCoordinate entryCoordinate = entry.getKey();
            if (area.in(coordinate, entryCoordinate)) {
                peaceful = true;
            }
            if (area.getLastTouched() < curtime) {
                // Hasn't been touched for at least 10 seconds. Probably no longer valid.
                // To be sure we will first check this by testing if the environmental controller is still active and running.
                ServerWorld world = entryCoordinate.getDimension().loadWorld();
                if (world != null) {
                    BlockPos c = entryCoordinate.getCoordinate();
                    // If the world is not loaded we don't do anything and we also don't remove the area since we have no information about it.
                    if (WorldTools.isLoaded(world, c)) {
                        boolean removeArea = true;
                        TileEntity te = world.getBlockEntity(c);
                        if (te instanceof EnvironmentalControllerTileEntity) {
                            EnvironmentalControllerTileEntity controllerTileEntity = (EnvironmentalControllerTileEntity) te;
                            for (EnvironmentModule module : controllerTileEntity.getEnvironmentModules()) {
                                if (module instanceof PeacefulEModule) {
                                    if (((PeacefulEModule) module).isActive()) {
                                        removeArea = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (removeArea) {
                            toRemove.add(entryCoordinate);
                        }
                    }
                }
            }
        }

        for (GlobalCoordinate globalCoordinate : toRemove) {
            areas.remove(globalCoordinate);
        }

        return peaceful;
    }


    public static class PeacefulArea {
        private float sqradius;
        private int miny;
        private int maxy;
        private long lastTouched;

        public PeacefulArea(float radius, int miny, int maxy) {
            this.sqradius = radius * radius;
            this.miny = miny;
            this.maxy = maxy;
            touch();
        }

        public PeacefulArea setArea(float radius, int miny, int maxy) {
            this.sqradius = radius * radius;
            this.miny = miny;
            this.maxy = maxy;
            return this;
        }

        @Override
        public String toString() {
            return "PeacefulArea{" +
                    "sqradius=" + sqradius +
                    ", miny=" + miny +
                    ", maxy=" + maxy +
                    ", lastTouched=" + lastTouched +
                    '}';
        }

        public long getLastTouched() {
            return lastTouched;
        }

        public PeacefulArea touch() {
            lastTouched = System.currentTimeMillis();
            return this;
        }

        public boolean in(GlobalCoordinate coordinate, GlobalCoordinate thisCoordinate) {
            if (coordinate.getDimension() != thisCoordinate.getDimension()) {
                return false;
            }
            double py = coordinate.getCoordinate().getY();
            if (py < miny || py > maxy) {
                return false;
            }

            double px = coordinate.getCoordinate().getX() - thisCoordinate.getCoordinate().getX();
            double pz = coordinate.getCoordinate().getZ() - thisCoordinate.getCoordinate().getZ();
            double sqdist = px * px + pz * pz;
            return sqdist < sqradius;
        }
    }
}
