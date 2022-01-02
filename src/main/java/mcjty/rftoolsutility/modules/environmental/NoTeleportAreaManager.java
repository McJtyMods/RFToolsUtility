package mcjty.rftoolsutility.modules.environmental;

import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;
import mcjty.rftoolsutility.modules.environmental.modules.NoTeleportEModule;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class NoTeleportAreaManager {
    private static final Map<GlobalPos,NoTeleportArea> areas = new HashMap<>();

    public static void markArea(GlobalPos coordinate, int radius, int miny, int maxy) {
        if (areas.containsKey(coordinate)) {
            areas.get(coordinate).touch().setArea(radius, miny, maxy);
        } else {
            NoTeleportArea area = new NoTeleportArea(radius, miny, maxy);
            areas.put(coordinate, area);
        }
    }

    public static boolean isTeleportPrevented(Entity entity, GlobalPos coordinate) {
        if (areas.isEmpty()) {
            return false;
        }

        List<GlobalPos> toRemove = new ArrayList<>();
        boolean noTeleport = false;
        long curtime = System.currentTimeMillis() - 10000;

        for (Map.Entry<GlobalPos, NoTeleportArea> entry : areas.entrySet()) {
            NoTeleportArea area = entry.getValue();
            GlobalPos entryCoordinate = entry.getKey();
            if (area.in(coordinate, entryCoordinate)) {
                ServerLevel world = LevelTools.getLevel(entity.level, entryCoordinate.dimension());
                BlockEntity te = world.getBlockEntity(entryCoordinate.pos());
                if (te instanceof EnvironmentalControllerTileEntity) {
                    EnvironmentalControllerTileEntity controllerTileEntity = (EnvironmentalControllerTileEntity) te;
                    noTeleport = controllerTileEntity.isEntityAffected(entity);
                }
            }
            if (area.getLastTouched() < curtime) {
                // Hasn't been touched for at least 10 seconds. Probably no longer valid.
                // To be sure we will first check this by testing if the environmental controller is still active and running.
                ServerLevel world = LevelTools.getLevel(entity.level, entryCoordinate.dimension());
                if (world != null) {
                    BlockPos c = entryCoordinate.pos();
                    // If the world is not loaded we don't do anything and we also don't remove the area since we have no information about it.
                    if (LevelTools.isLoaded(world, c)) {
                        boolean removeArea = true;
                        BlockEntity te = world.getBlockEntity(c);
                        if (te instanceof EnvironmentalControllerTileEntity) {
                            EnvironmentalControllerTileEntity controllerTileEntity = (EnvironmentalControllerTileEntity) te;
                            for (EnvironmentModule module : controllerTileEntity.getEnvironmentModules()) {
                                if (module instanceof NoTeleportEModule) {
                                    if (((NoTeleportEModule) module).isActive()) {
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

        return noTeleport;
    }


    public static class NoTeleportArea {
        private float sqradius;
        private int miny;
        private int maxy;
        private long lastTouched;

        public NoTeleportArea(float radius, int miny, int maxy) {
            this.sqradius = radius * radius;
            this.miny = miny;
            this.maxy = maxy;
            touch();
        }

        public NoTeleportArea setArea(float radius, int miny, int maxy) {
            this.sqradius = radius * radius;
            this.miny = miny;
            this.maxy = maxy;
            return this;
        }

        @Override
        public String toString() {
            return "NoTeleportArea{" +
                    "sqradius=" + sqradius +
                    ", miny=" + miny +
                    ", maxy=" + maxy +
                    ", lastTouched=" + lastTouched +
                    '}';
        }

        public long getLastTouched() {
            return lastTouched;
        }

        public NoTeleportArea touch() {
            lastTouched = System.currentTimeMillis();
            return this;
        }

        public boolean in(GlobalPos coordinate, GlobalPos thisCoordinate) {
            if (!Objects.equals(coordinate.dimension(), thisCoordinate.dimension())) {
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
