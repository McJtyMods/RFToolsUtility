package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;
import mcjty.rftoolsutility.modules.environmental.modules.PeacefulEModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeacefulAreaManager {
    private static final Map<GlobalPos,PeacefulArea> areas = new HashMap<>();

    public static void markArea(GlobalPos coordinate, int radius, int miny, int maxy) {
        if (areas.containsKey(coordinate)) {
            areas.get(coordinate).touch().setArea(radius, miny, maxy);
        } else {
            PeacefulArea area = new PeacefulArea(radius, miny, maxy);
            areas.put(coordinate, area);
        }
    }

    public static boolean isPeaceful(GlobalPos coordinate) {
        if (areas.isEmpty()) {
            return false;
        }
        List<GlobalPos> toRemove = new ArrayList<>();
        boolean peaceful = false;
        long curtime = System.currentTimeMillis() - 10000;

        for (Map.Entry<GlobalPos, PeacefulArea> entry : areas.entrySet()) {
            PeacefulArea area = entry.getValue();
            GlobalPos entryCoordinate = entry.getKey();
            if (area.in(coordinate, entryCoordinate)) {
                peaceful = true;
            }
            if (area.getLastTouched() < curtime) {
                // Hasn't been touched for at least 10 seconds. Probably no longer valid.
                // To be sure we will first check this by testing if the environmental controller is still active and running.
                ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(entryCoordinate.dimension());
                if (world != null) {
                    BlockPos c = entryCoordinate.pos();
                    // If the world is not loaded we don't do anything and we also don't remove the area since we have no information about it.
                    if (LevelTools.isLoaded(world, c)) {
                        boolean removeArea = true;
                        BlockEntity te = world.getBlockEntity(c);
                        if (te instanceof EnvironmentalControllerTileEntity controller) {
                            for (EnvironmentModule module : controller.getEnvironmentModules()) {
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

        for (GlobalPos globalCoordinate : toRemove) {
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

        public boolean in(GlobalPos coordinate, GlobalPos thisCoordinate) {
            if (!coordinate.dimension().equals(thisCoordinate.dimension())) {
                return false;
            }
            double py = coordinate.pos().getY();
            if (py < miny || py > maxy) {
                return false;
            }

            double px = coordinate.pos().getX() - thisCoordinate.pos().getX();
            double pz = coordinate.pos().getZ() - thisCoordinate.pos().getZ();
            double sqdist = px * px + pz * pz;
            return sqdist < sqradius;
        }
    }
}
