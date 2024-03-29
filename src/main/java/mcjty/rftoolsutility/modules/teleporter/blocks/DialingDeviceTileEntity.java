package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ResultCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.client.GuiDialingDevice;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinationClientInfo;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.modules.teleporter.data.TransmitterInfo;
import mcjty.rftoolsutility.playerprops.PlayerExtendedProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_DIALING_DEVICE;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_DIALING_DEVICE;

public class DialingDeviceTileEntity extends GenericTileEntity {

    public static final Key<Integer> PARAM_STATUS = new Key<>("status", Type.INTEGER);

//    public static final String CMD_CHECKSTATUS = "checkStatus";
    public static final int DIAL_RECEIVER_BLOCKED_MASK = 0x1;       // One value for blocked or not on receiver side
    public static final int DIAL_TRANSMITTER_BLOCKED_MASK = 0x2;    // One value for blocked or not on transmitter side
    public static final int DIAL_INVALID_DESTINATION_MASK = 0x4;    // The destination is somehow invalid
    public static final int DIAL_DIALER_POWER_LOW_MASK = 0x8;       // The dialer itself is low on power
    public static final int DIAL_RECEIVER_POWER_LOW_MASK = 0x10;    // The receiver is low on power
    public static final int DIAL_TRANSMITTER_NOACCESS = 0x20;       // No access to transmitter
    public static final int DIAL_RECEIVER_NOACCESS = 0x40;          // No access to receiver
    public static final int DIAL_INTERRUPTED = 0x80;                // The dial was interrupted
    public static final int DIAL_INVALID_SOURCE_MASK = 0x100;       // The source is somehow invalid
    public static final int DIAL_DIMENSION_POWER_LOW_MASK = 0x200;  // The destination dimension is low on power
    public static final int DIAL_INVALID_TRANSMITTER = 0x400;       // The transmitter is gone!
    public static final int DIAL_OK = 0;                            // All is ok
    public static final String COMPONENT_NAME = "dialing_device";

    private boolean showOnlyFavorites = false;

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, TeleportConfiguration.DIALER_MAXENERGY.get(), TeleportConfiguration.DIALER_RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Dialing Device")
            .containerSupplier(empty(CONTAINER_DIALING_DEVICE, this))
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(DialingDeviceTileEntity.this));

    public DialingDeviceTileEntity() {
        super(TYPE_DIALING_DEVICE.get());
    }

    /**
     * Calculate the distance (in string form) between a transmitter and receiver.
     *
     * @param world
     * @param transmitterInfo
     * @param teleportDestination
     * @return the distance or else 'dimension warp' in case it is another dimension.
     */
    public static String calculateDistance(World world, TransmitterInfo transmitterInfo, TeleportDestination teleportDestination) {
        if (!world.dimension().equals(teleportDestination.getDimension())) {
            return "dimension warp";
        } else {
            BlockPos c1 = transmitterInfo.getCoordinate();
            BlockPos c2 = teleportDestination.getCoordinate();
            double dist = new Vector3d(c1.getX(), c1.getY(), c1.getZ()).distanceTo(new Vector3d(c2.getX(), c2.getY(), c2.getZ()));
            return Integer.toString((int) dist);
        }
    }

    public static boolean isMatterBoosterAvailable(World world, BlockPos pos) {
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            if (TeleporterModule.MATTER_BOOSTER.get().equals(world.getBlockState(pos.relative(facing)).getBlock())) {
                return true;
            }
        }
        return false;
    }


    public static boolean isDestinationAnalyzerAvailable(World world, BlockPos pos) {
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            if (TeleporterModule.DESTINATION_ANALYZER.get().equals(world.getBlockState(pos.relative(facing)).getBlock())) {
                return true;
            }
        }
        return false;
    }

    public boolean isShowOnlyFavorites() {
        return showOnlyFavorites;
    }

    public void setShowOnlyFavorites(boolean showOnlyFavorites) {
        this.showOnlyFavorites = showOnlyFavorites;
        setChanged();
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        energyStorage.setEnergy(tagCompound.getLong("Energy"));
        CompoundNBT info = tagCompound.getCompound("Info");
        showOnlyFavorites = info.getBoolean("showFav");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        getOrCreateInfo(tagCompound).putBoolean("showFav", showOnlyFavorites);
        tagCompound.putLong("Energy", energyStorage.getEnergy());
    }

    private List<TeleportDestinationClientInfo> searchReceivers(UUID player) {
        TeleportDestinations destinations = TeleportDestinations.get(level);
        return new ArrayList<>(destinations.getValidDestinations(level, player));
    }

    public List<TransmitterInfo> searchTransmitters() {
        int x = getBlockPos().getX();
        int y = getBlockPos().getY();
        int z = getBlockPos().getZ();

        int hrange = TeleportConfiguration.horizontalDialerRange.get();
        int vrange = TeleportConfiguration.verticalDialerRange.get();

        List<TransmitterInfo> transmitters = new ArrayList<>();
        for (int dy = -vrange; dy <= vrange; dy++) {
            int yy = y + dy;
            if (yy >= 0 && yy < level.getMaxBuildHeight()) {
                for (int dz = -hrange; dz <= hrange; dz++) {
                    int zz = z + dz;
                    for (int dx = -hrange; dx <= hrange; dx++) {
                        int xx = x + dx;
                        if (dx != 0 || dy != 0 || dz != 0) {
                            BlockPos c = new BlockPos(xx, yy, zz);
                            BlockState state = level.getBlockState(c);
                            TileEntity tileEntity = level.getBlockEntity(c);
                            if (tileEntity instanceof MatterTransmitterTileEntity) {
                                MatterTransmitterTileEntity matterTransmitterTileEntity = (MatterTransmitterTileEntity) tileEntity;
                                transmitters.add(new TransmitterInfo(c, matterTransmitterTileEntity.getName(), matterTransmitterTileEntity.getTeleportDestination()));
                            }
                        }
                    }
                }
            }
        }
        return transmitters;
    }

    // Server side only.
    private void changeFavorite(String playerName, BlockPos receiver, RegistryKey<World> dimension, boolean favorite) {
        List<ServerPlayerEntity> list = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity entity : list) {
            if (playerName.equals(entity.getName().getString())) {
                PlayerExtendedProperties.getFavoriteDestinations(entity).ifPresent(h -> {
                    h.setDestinationFavorite(GlobalPos.of(dimension, receiver), favorite);
                });
                return;
            }
        }
    }


    // Server side only
    private int dial(UUID player, BlockPos transmitter, RegistryKey<World> transDim, BlockPos coordinate, RegistryKey<World> dimension, boolean once) {
        return TeleportationTools.dial(level, this, player, transmitter, transDim, coordinate, dimension, once);
    }

    // Server side only
    private int checkStatus(BlockPos c, RegistryKey<World> dim) {
        int s;
        int defaultCost = TeleportConfiguration.rfPerCheck.get();
        int cost = infusableHandler.map(inf -> (int) (defaultCost * (2.0f - inf.getInfusedFactor()) / 2.0f)).orElse(defaultCost);
        if (energyStorage.getEnergy() < cost) {
            s = DialingDeviceTileEntity.DIAL_DIALER_POWER_LOW_MASK;
        } else {
            energyStorage.consumeEnergy(cost);
            s = 0;
        }
        if (s != 0) {
            return s;
        }

        World w = LevelTools.getLevel(dim);
        if (w == null) {
            TeleportDestinations destinations = TeleportDestinations.get(level);
            destinations.cleanupInvalid();
            return DialingDeviceTileEntity.DIAL_INVALID_DESTINATION_MASK;
        }

        TileEntity tileEntity = w.getBlockEntity(c);
        if (!(tileEntity instanceof MatterReceiverTileEntity)) {
            TeleportDestinations destinations = TeleportDestinations.get(level);
            destinations.cleanupInvalid();
            return DialingDeviceTileEntity.DIAL_INVALID_DESTINATION_MASK;
        }

        // @todo with plugin system
//        if (dimensionManager.getDimensionInformation(dim) != null) {
//            // This is an RFTools dimension. Check power.
//            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(w);
//            int energyLevel = dimensionStorage.getEnergyLevel(dim);
//            if (energyLevel < DimletConfiguration.DIMPOWER_WARN_TP) {
//                return DialingDeviceTileEntity.DIAL_DIMENSION_POWER_LOW_MASK;
//            }
//        }

        MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) tileEntity;

        return matterReceiverTileEntity.checkStatus();
    }

    public static final Key<UUID> PARAM_PLAYER_UUID = new Key<>("playerUuid", Type.UUID);

    @ServerCommand(type = TeleportDestinationClientInfo.class, serializer = TeleportDestinationClientInfo.Serializer.class)
    public static final ListCommand<?, ?> CMD_GETRECEIVERS = ListCommand.<DialingDeviceTileEntity, TeleportDestinationClientInfo>create("rftoolsutility.dialer.getReceivers",
            (te, player, params) -> te.searchReceivers(params.get(PARAM_PLAYER_UUID)),
            (te, player, params, list) -> GuiDialingDevice.fromServer_receivers = list);

    @ServerCommand(type = TransmitterInfo.class, serializer = TransmitterInfo.Serializer.class)
    public static final ListCommand<?, ?> CMD_GETTRANSMITTERS = ListCommand.<DialingDeviceTileEntity, TransmitterInfo>create("rftoolsutility.dialer.getTransmitters",
            (te, player, params) -> te.searchTransmitters(),
            (te, player, params, list) -> GuiDialingDevice.fromServer_transmitters = list);


    public static final Key<String> PARAM_PLAYER = new Key<>("player", Type.STRING);
    public static final Key<BlockPos> PARAM_POS = new Key<>("pos", Type.BLOCKPOS);
    public static final Key<String> PARAM_DIMENSION = new Key<>("dimension", Type.STRING);
    public static final Key<BlockPos> PARAM_TRANSMITTER = new Key<>("transmitter", Type.BLOCKPOS);
    public static final Key<String> PARAM_TRANS_DIMENSION = new Key<>("transDimension", Type.STRING);
    public static final Key<Boolean> PARAM_FAVORITE = new Key<>("favorite", Type.BOOLEAN);

    @ServerCommand
    public static final Command<?> CMD_FAVORITE = Command.<DialingDeviceTileEntity>create("dialer.favorite",
            (te, p, params) -> {
                String player = params.get(PARAM_PLAYER);
                BlockPos receiver = params.get(PARAM_POS);
                String dimension = params.get(PARAM_DIMENSION);
                boolean favorite = params.get(PARAM_FAVORITE);
                te.changeFavorite(player, receiver, LevelTools.getId(dimension), favorite);
            });

    @ServerCommand
    public static final Command<?> CMD_SHOWFAVORITE = Command.<DialingDeviceTileEntity>create("dialer.showFavorite",
            (te, player, params) -> te.setShowOnlyFavorites(params.get(PARAM_FAVORITE)));

    @ServerCommand
    public static final ResultCommand<?> CMD_CHECKSTATUS = ResultCommand.<DialingDeviceTileEntity>create("checkStatus",
            (te, player, params) -> {
                BlockPos c = params.get(PARAM_POS);
                String dim = params.get(PARAM_DIMENSION);
                return TypedMap.builder().put(PARAM_STATUS, te.checkStatus(c, LevelTools.getId(dim))).build();
            },
            (te, player, params) -> {
                GuiDialingDevice.setReceiverStatus(params.get(PARAM_STATUS));
            });

    @ServerCommand
    public static final ResultCommand<?> CMD_DIAL = ResultCommand.<DialingDeviceTileEntity>create("dial",
            (te, player, params) -> {
                UUID playerUUID = params.get(PARAM_PLAYER_UUID);
                BlockPos transmitter = params.get(PARAM_TRANSMITTER);
                String transDim = params.get(PARAM_TRANS_DIMENSION);
                BlockPos c = params.get(PARAM_POS);
                String dim = params.get(PARAM_DIMENSION);
                return TypedMap.builder().put(PARAM_STATUS, te.dial(playerUUID, transmitter,
                        LevelTools.getId(transDim), c,
                        LevelTools.getId(dim), false)).build();
            },
            (te, player, params) -> {
                GuiDialingDevice.setDialResult(params.get(PARAM_STATUS));
            });

    @ServerCommand
    public static final ResultCommand<?> CMD_DIALONCE = ResultCommand.<DialingDeviceTileEntity>create("dialOnce",
            (te, player, params) -> {
                UUID playerUUID = params.get(PARAM_PLAYER_UUID);
                BlockPos transmitter = params.get(PARAM_TRANSMITTER);
                String transDim = params.get(PARAM_TRANS_DIMENSION);
                BlockPos c = params.get(PARAM_POS);
                String dim = params.get(PARAM_DIMENSION);
                return TypedMap.builder().put(PARAM_STATUS, te.dial(playerUUID, transmitter,
                        LevelTools.getId(transDim), c,
                        LevelTools.getId(dim), true)).build();
            },
            (te, player, params) -> {
                GuiDialingDevice.setDialResult(params.get(PARAM_STATUS));
            });
}
