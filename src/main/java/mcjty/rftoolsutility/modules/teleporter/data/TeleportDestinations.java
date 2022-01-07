package mcjty.rftoolsutility.modules.teleporter.data;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.playerprops.FavoriteDestinationsProperties;
import mcjty.rftoolsutility.playerprops.PlayerExtendedProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.*;

public class TeleportDestinations extends AbstractWorldData<TeleportDestinations> {

    private static final String TPDESTINATIONS_NAME = "TPDestinations";

    private final Map<GlobalPos,TeleportDestination> destinations = new HashMap<>();
    private final Map<Integer,GlobalPos> destinationById = new HashMap<>();
    private final Map<GlobalPos,Integer> destinationIdByCoordinate = new HashMap<>();
    private int lastId = 0;

    public TeleportDestinations() {
    }

    public TeleportDestinations(CompoundTag tag) {
        lastId = tag.getInt("lastId");
        readDestinationsFromNBT(tag);
    }

//    @Override
//    public void clear() {
//        destinationById.clear();
//        destinationIdByCoordinate.clear();
//        destinations.clear();
//        lastId = 0;
//    }

    public static String getDestinationName(TeleportDestinations destinations, int receiverId) {
        GlobalPos coordinate = destinations.getCoordinateForId(receiverId);
        String name;
        if (coordinate == null) {
            name = "?";
        } else {
            TeleportDestination destination = destinations.getDestination(coordinate);
            if (destination == null) {
                name = "?";
            } else {
                name = destination.getName();
                if (name == null || name.isEmpty()) {
                    name = BlockPosTools.toString(destination.getCoordinate()) + " (" + destination.getDimension().location().getPath() + ")";
                }
            }
        }
        return name;
    }

    public void cleanupInvalid() {
        Set<GlobalPos> keys = new HashSet<>(destinations.keySet());
        for (GlobalPos key : keys) {
            Level transWorld = LevelTools.getLevel(key.dimension());
            boolean removed = false;
            if (transWorld == null) {
                Logging.log("Receiver on dimension " + key.dimension().location().getPath() + " removed because world can't be loaded!");
                removed = true;
            } else {
                BlockPos c = key.pos();
                BlockEntity te;
                try {
                    te = transWorld.getBlockEntity(c);
                } catch (Exception e) {
                    te = null;
                }
                if (!(te instanceof MatterReceiverTileEntity)) {
                    Logging.log("Receiver at " + c + " on dimension " + key.dimension().location().getPath() + " removed because there is no receiver there!");
                    removed = true;
                }
            }
            if (removed) {
                destinations.remove(key);
            }
        }
    }

    public static TeleportDestinations get(Level world) {
        return getData(world, TeleportDestinations::new, TeleportDestinations::new, TPDESTINATIONS_NAME);
    }


    // Server side only
    public Collection<TeleportDestinationClientInfo> getValidDestinations(Level level, UUID player) {
        FavoriteDestinationsProperties properties = null;
        if (player != null) {
            MinecraftServer server = level.getServer();
            List<ServerPlayer> list = server.getPlayerList().getPlayers();
            for (ServerPlayer entity : list) {
                if (player.equals(entity.getUUID())) {
                    properties = PlayerExtendedProperties.getFavoriteDestinations(entity).map(h -> h).orElse(null);
                    break;
                }
            }
        }

        List<TeleportDestinationClientInfo> result = new ArrayList<>();
        for (TeleportDestination destination : destinations.values()) {
            TeleportDestinationClientInfo destinationClientInfo = new TeleportDestinationClientInfo(destination);
            BlockPos c = destination.getCoordinate();
            Level world = LevelTools.getLevel(destination.getDimension());
            String dimName = "<Unknown>";
            if (world != null) {
                dimName = world.dimension().location().getPath();
            }

            // @todo
//            DimensionInformation information = RfToolsDimensionManager.getDimensionManager(getWorld()).getDimensionInformation(destination.getDimension());
//            if (information != null) {
//                dimName = information.getModuleName();
//            }
            // @todo 1.16 is there a better name?
//            dimName = dimName + " (" + destination.getDimension().getId() + ")";
            destinationClientInfo.setDimensionName(dimName);

            if (world != null) {

                if (!destination.isAccessKnown()) {
                    // Update access
                    BlockEntity te = world.getBlockEntity(c);
                    if (te instanceof MatterReceiverTileEntity receiver) {
                        destination = receiver.updateDestination();
                    }
                }

                if (destination.isAccessKnown() && player != null && !destination.checkAccess(world, player)) {
                    // No access
                    continue;
                }
            }
            if (properties != null) {
                destinationClientInfo.setFavorite(properties.isDestinationFavorite(GlobalPos.of(destination.getDimension(), c)));
            }
            result.add(destinationClientInfo);
        }

        Collections.sort(result);

        return result;
    }

    /**
     * Check if the teleport destination is still valid.
     */
    public boolean isDestinationValid(TeleportDestination destination) {
        GlobalPos key = GlobalPos.of(destination.getDimension(), destination.getCoordinate());
        return destinations.containsKey(key);
    }

    // Set an old id to a new position (after moving a receiver).
    public void assignId(GlobalPos key, int id) {
        destinationById.put(id, key);
        destinationIdByCoordinate.put(key, id);
    }

    public int getNewId(GlobalPos key) {
        if (destinationIdByCoordinate.containsKey(key)) {
            return destinationIdByCoordinate.get(key);
        }
        lastId++;
        destinationById.put(lastId, key);
        destinationIdByCoordinate.put(key, lastId);
        return lastId;
    }

    // Get the id from a coordinate.
    public Integer getIdForCoordinate(GlobalPos key) {
        return destinationIdByCoordinate.get(key);
    }

    public GlobalPos getCoordinateForId(int id) {
        return destinationById.get(id);
    }

    public TeleportDestination addDestination(GlobalPos key) {
        if (!destinations.containsKey(key)) {
            TeleportDestination teleportDestination = new TeleportDestination(key.pos(), key.dimension());
            destinations.put(key, teleportDestination);
        }
        return destinations.get(key);
    }

    public void removeDestinationsInDimension(ResourceKey<Level> dimension) {
        Set<GlobalPos> keysToRemove = new HashSet<>();
        for (Map.Entry<GlobalPos, TeleportDestination> entry : destinations.entrySet()) {
            if (entry.getKey().dimension().equals(dimension)) {
                keysToRemove.add(entry.getKey());
            }
        }
        for (GlobalPos key : keysToRemove) {
            removeDestination(key.pos(), key.dimension());
        }
    }

    public void removeDestination(BlockPos coordinate, ResourceKey<Level> dimension) {
        if (coordinate == null) {
            return;
        }
        GlobalPos key = GlobalPos.of(dimension, coordinate);
        destinations.remove(key);
        Integer id = destinationIdByCoordinate.get(key);
        if (id != null) {
            destinationById.remove(id);
            destinationIdByCoordinate.remove(key);
        }
    }

    public TeleportDestination getDestination(GlobalPos coordinate) {
        return destinations.get(coordinate);
    }

    public TeleportDestination getDestination(BlockPos coordinate, ResourceKey<Level> dimension) {
        return destinations.get(GlobalPos.of(dimension, coordinate));
    }

    private void readDestinationsFromNBT(CompoundTag tagCompound) {
        ListTag lst = tagCompound.getList("destinations", Tag.TAG_COMPOUND);
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundTag tc = lst.getCompound(i);
            BlockPos c = new BlockPos(tc.getInt("x"), tc.getInt("y"), tc.getInt("z"));
            String dims = tc.getString("dim");
            ResourceKey<Level> dim = LevelTools.getId(dims);
            String name = tc.getString("name");

            TeleportDestination destination = new TeleportDestination(c, dim);
            destination.setName(name);
            GlobalPos gc = GlobalPos.of(dim, c);
            destinations.put(gc, destination);

            int id;
            if (tc.contains("id")) {
                id = tc.getInt("id");
                destinationById.put(id, gc);
                destinationIdByCoordinate.put(gc, id);
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tagCompound) {
        writeDestinationsToNBT(tagCompound, destinations.values(), destinationIdByCoordinate);
        tagCompound.putInt("lastId", lastId);
        return tagCompound;
    }

    private static void writeDestinationsToNBT(CompoundTag tagCompound, Collection<TeleportDestination> destinations,
                                              Map<GlobalPos, Integer> coordinateToInteger) {
        ListTag lst = new ListTag();
        for (TeleportDestination destination : destinations) {
            CompoundTag tc = new CompoundTag();
            BlockPos c = destination.getCoordinate();
            tc.putInt("x", c.getX());
            tc.putInt("y", c.getY());
            tc.putInt("z", c.getZ());
            tc.putString("dim", destination.getDimension().location().toString());
            tc.putString("name", destination.getName());
            if (coordinateToInteger != null) {
                Integer id = coordinateToInteger.get(GlobalPos.of(destination.getDimension(), c));
                if (id != null) {
                    tc.putInt("id", id);
                }
            }
            lst.add(tc);
        }
        tagCompound.put("destinations", lst);
    }
}
