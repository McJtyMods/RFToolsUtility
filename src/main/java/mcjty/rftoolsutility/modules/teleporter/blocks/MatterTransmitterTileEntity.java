package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Cached;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsutility.compat.RFToolsDimCompat;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterTransmitter;
import mcjty.rftoolsutility.modules.teleporter.data.MatterTransmitterData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_MATTER_TRANSMITTER;

public class MatterTransmitterTileEntity extends TickingTileEntity {

    // Server side: the player we're currently teleporting.
    private UUID teleportingPlayer = null;
    private int teleportTimer = 0;
    private int cooldownTimer = 0;
    private int totalTicks;
    private int goodTicks;
    private int badTicks;
    private int rfPerTick = 0;

    private int status = TeleportationTools.STATUS_OK;

    private int checkReceiverStatusCounter = 20;

    private final Cached<AABB> beamBox = Cached.of(this::createBeamBox);

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, TeleportConfiguration.TRANSMITTER_MAXENERGY.get(), TeleportConfiguration.TRANSMITTER_RECEIVEPERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<MatterTransmitterTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<MatterTransmitterTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Matter Transmitter")
            .containerSupplier(empty(CONTAINER_MATTER_TRANSMITTER, be))
            .energyHandler(() -> be.energyStorage)
            .setupSync(be);

    private final DefaultInfusable infusable = new DefaultInfusable(MatterTransmitterTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<MatterTransmitterTileEntity, IInfusable> INFUSABLE_CAP = tile -> tile.infusable;


    // @todo 1.21 cap
    private final IMachineInformation infoHandler = createMachineInfo();

    @GuiValue
    public static final Value<?, String> VALUE_NAME = Value.create("name", Type.STRING, MatterTransmitterTileEntity::getName, MatterTransmitterTileEntity::setName);

    @GuiValue(name = "private")
    public static final Value<?, Boolean> VALUE_PRIVATE = Value.create("private", Type.BOOLEAN, MatterTransmitterTileEntity::isPrivateAccess, MatterTransmitterTileEntity::setPrivateAccess);

    @GuiValue(name = "beam")
    public static final Value<?, Boolean> VALUE_BEAMHIDDEN = Value.create("beam", Type.BOOLEAN, MatterTransmitterTileEntity::isBeamHidden, MatterTransmitterTileEntity::setBeamHidden);

    public MatterTransmitterTileEntity(BlockPos pos, BlockState state) {
        super(TeleporterModule.MATTER_TRANSMITTER.be().get(), pos, state);
    }

    public String getName() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return data.name() == null ? "" : data.name();
    }

    public void setName(String name) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        data = data.withName(name);
        setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
    }

    public boolean isPrivateAccess() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return data.privateAccess();
    }

    public void setPrivateAccess(boolean privateAccess) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        data = data.withPrivateAccess(privateAccess);
        setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
    }

    public boolean isBeamHidden() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return data.beamHidden();
    }

    public void setBeamHidden(boolean b) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        data = data.withBeamHidden(b);
        setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
    }

    public boolean isOnce() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return data.once();
    }

    public boolean checkAccess(String player) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (!data.privateAccess()) {
            return true;
        }
        return data.players().contains(player);
    }

    public boolean checkAccess(UUID player) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (!data.privateAccess()) {
            return true;
        }
        ServerPlayer entity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player);
        if (entity == null) {
            return false;
        }
        return data.players().contains(entity.getDisplayName().getString());    // @todo 1.16 getFormattedText()
    }

    public int getStatus() {
        return status;
    }

    public List<String> getAllowedPlayers() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return new ArrayList<>(data.players());
    }

    public void addPlayer(String player) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (!data.players().contains(player)) {
            data = data.addPlayer(player);
            setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
        }
    }

    public void delPlayer(String player) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (data.players().contains(player)) {
            data = data.removePlayer(player);
            setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        MatterTransmitterData.CODEC.encodeStart(NbtOps.INSTANCE, getData(TeleporterModule.MATTERTRANSMITTER_DATA)).result().ifPresent(data -> tag.put("data", data));
        tag.putInt("status", status);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        MatterTransmitterData.CODEC.decode(NbtOps.INSTANCE, tag.get("data")).result().ifPresent(data -> setData(TeleporterModule.MATTERTRANSMITTER_DATA, data.getFirst()));
        status = tag.getInt("status");
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        teleportTimer = tag.getInt("tpTimer");
        cooldownTimer = tag.getInt("cooldownTimer");
        totalTicks = tag.getInt("totalTicks");
        goodTicks = tag.getInt("goodTicks");
        badTicks = tag.getInt("badTicks");
        if (tag.hasUUID("tpPlayer")) {
            teleportingPlayer = tag.getUUID("tpPlayer");
        } else {
            teleportingPlayer = null;
        }
        status = tag.getInt("status");
        rfPerTick = tag.getInt("rfPerTick");
        energyStorage.load(tag, "energy", provider);
        infusable.load(tag, "infusable");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("tpTimer", teleportTimer);
        tag.putInt("cooldownTimer", cooldownTimer);
        tag.putInt("totalTicks", totalTicks);
        tag.putInt("goodTicks", goodTicks);
        tag.putInt("badTicks", badTicks);
        if (teleportingPlayer != null) {
            tag.putUUID("tpPlayer", teleportingPlayer);
        }
        tag.putInt("status", status);
        tag.putInt("rfPerTick", rfPerTick);
        energyStorage.save(tag, "energy", provider);
        infusable.save(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(TeleporterModule.ITEM_MATTERTRANSMITTER_DATA);
        if (data != null) {
            setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
        }
        energyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        infusable.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(TeleporterModule.ITEM_MATTERTRANSMITTER_DATA, getData(TeleporterModule.MATTERTRANSMITTER_DATA));
        energyStorage.collectImplicitComponents(builder);
        infusable.collectImplicitComponents(builder);
    }

    public boolean isDialed() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return data.destinationId() != null || (data.destination() != null && data.destination().isValid());
    }

    public Integer getTeleportId() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (isDialed() && data.destinationId() == null) {
            getTeleportDestination();
        }
        return data.destinationId();
    }

    public TeleportDestination getTeleportDestination() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (data.destinationId() != null) {
            TeleportDestinations teleportDestinations = TeleportDestinations.get(level);
            GlobalPos gc = teleportDestinations.getCoordinateForId(data.destinationId());
            if (gc == null) {
                return null;
            } else {
                return teleportDestinations.getDestination(gc.pos(), gc.dimension());
            }
        }
        return data.destination();
    }

    public void setTeleportDestination(TeleportDestination teleportDestination, boolean once) {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        data = data.withDestination(TeleportDestination.INVALID);
        data = data.withDestinationId(null);
        data = data.withOnce(once);
        if (teleportDestination != null) {
            TeleportDestinations destinations = TeleportDestinations.get(level);
            Integer id = destinations.getIdForCoordinate(GlobalPos.of(teleportDestination.getDimension(), teleportDestination.getCoordinate()));
            if (id == null) {
                data = data.withDestination(teleportDestination);
            } else {
                data = data.withDestinationId(id);
            }
        }
        setData(TeleporterModule.MATTERTRANSMITTER_DATA, data);
        markDirtyClient();
    }

    private void consumeIdlePower() {
        if (TeleportConfiguration.rfMatterIdleTick.get() > 0 && teleportingPlayer == null) {
            if (energyStorage.getEnergyStored() >= TeleportConfiguration.rfMatterIdleTick.get()) {
                energyStorage.consumeEnergy(TeleportConfiguration.rfMatterIdleTick.get());
            } else {
                setTeleportDestination(null, false);
            }
        }
    }

    @Override
    protected void tickServer() {
        // Every few times we check if the receiver is ok (if we're dialed).
        if (isDialed()) {
            consumeIdlePower();

            checkReceiverStatusCounter--;
            if (checkReceiverStatusCounter <= 0) {
                checkReceiverStatusCounter = 20;
                int newstatus;
                if (DialingDeviceTileEntity.isDestinationAnalyzerAvailable(level, getBlockPos())) {
                    newstatus = checkReceiverStatus();
                } else {
                    newstatus = TeleportationTools.STATUS_OK;
                }
                if (newstatus != status) {
                    status = newstatus;
                    markDirtyClient();
                }
            }
        }

        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (isCoolingDown()) {
            // We're still in cooldown. Do nothing.
            return;
        } else if (teleportingPlayer == null) {
            // If we have a valid destination we check here if there is a player on this transmitter.
            if (isDestinationValid()) {
                searchForNearestPlayer();
            }
        } else if (data.destination() == null && data.destinationId() == null) {
            // We were teleporting a player but for some reason the destination went away. Interrupt.
            Player player = level.getServer().getPlayerList().getPlayer(teleportingPlayer);
            if (player != null) {
                Logging.warn(player, "The destination vanished! Aborting.");
            }
            clearTeleport(80);
        } else if (isPlayerOutsideBeam()) {
            // The player moved outside the beam. Interrupt the teleport.
            clearTeleport(80);
        } else {
            int rf = rfPerTick;
            if (energyStorage.getEnergyStored() < rf) {
                // We don't have enough energy to handle this tick.
                handleEnergyShortage();
            } else {
                // We have enough energy so this is a good tick.
                setChanged();
                energyStorage.consumeEnergy(rf);
                goodTicks++;

                teleportTimer--;
                if (teleportTimer <= 0) {
                    performTeleport();
                }
            }
        }
    }

    // Server side only
    private int checkReceiverStatus() {
        TeleportDestination destination = getTeleportDestination();
        if (destination == null) {
            return TeleportationTools.STATUS_WARN;
        }

        ResourceKey<Level> dimension = destination.getDimension();

        int powerPercentage = RFToolsDimCompat.getPowerPercentage(level, dimension.location());
        if (powerPercentage >= 0) {
            if (powerPercentage < TeleportConfiguration.DIMENSION_WARN_PERCENTAGE.get()) {
                return TeleportationTools.STATUS_WARN;
            }
        }

        Level w = LevelTools.getLevel(level, dimension);
        // By default we will not check if the dimension is not loaded. Can be changed in config.
        if (w == null) {
            if (TeleportConfiguration.matterTransmitterLoadWorld.get() == -1) {
                return TeleportationTools.STATUS_UNKNOWN;
            } else {
                w = LevelTools.getLevel(dimension);
                checkReceiverStatusCounter = TeleportConfiguration.matterTransmitterLoadWorld.get();
            }
        }
        BlockPos c = destination.getCoordinate();

        boolean exists = LevelTools.isLoaded(w, c);
        if (!exists) {
            if (TeleportConfiguration.matterTransmitterLoadChunk.get() == -1) {
                return TeleportationTools.STATUS_UNKNOWN;
            } else {
                checkReceiverStatusCounter = TeleportConfiguration.matterTransmitterLoadChunk.get();
            }
        }

        BlockEntity tileEntity = w.getBlockEntity(c);
        if (!(tileEntity instanceof MatterReceiverTileEntity receiver)) {
            return TeleportationTools.STATUS_WARN;
        }

        int status = receiver.checkStatus();
        return (status == DialingDeviceTileEntity.DIAL_OK) ? TeleportationTools.STATUS_OK : TeleportationTools.STATUS_WARN;
    }

    private void clearTeleport(int cooldown) {
        setChanged();
        TeleportationTools.applyBadEffectIfNeeded(level.getServer().getPlayerList().getPlayer(teleportingPlayer), 0, badTicks, totalTicks, false);
        cooldownTimer = cooldown;
        teleportingPlayer = null;
    }

    private boolean isDestinationValid() {
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        return data.destinationId() != null || (data.destination() != null && data.destination().isValid());
    }

    private boolean isCoolingDown() {
        setChanged();
        cooldownTimer--;
        if (cooldownTimer <= 0) {
            cooldownTimer = 0;
        } else {
            return true;
        }
        return false;
    }

    private AABB createBeamBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        return new AABB(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
    }

    private void searchForNearestPlayer() {
        List<Player> l = level.getEntitiesOfClass(Player.class, beamBox.get());
        Entity nearestPlayer = findNearestPlayer(l);

        if (nearestPlayer == null) {
            cooldownTimer = 5;
            return;
        }
        AABB playerBB = nearestPlayer.getBoundingBox();
        // Shouldn't be possible but there are mods...
        if (playerBB.intersects(beamBox.get())) {
            startTeleportation(nearestPlayer);
        } else {
            cooldownTimer = 5;
        }
    }

    private Entity findNearestPlayer(List<Player> l) {
        Entity nearestPlayer = null;
        double dmax = Double.MAX_VALUE;
        for (Entity entity : l) {
            if (entity instanceof Player player) {
                if (player.isPassenger() || player.isVehicle()) {
                    // Ignore players that are riding a horse
                    continue;
                }

                MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
                if ((!isPrivateAccess()) || data.players().contains(player.getDisplayName().getString())) { // @todo 1.16 was getFormattedText()
                    double d1 = entity.distanceToSqr(getBlockPos().getX() + .5, getBlockPos().getY() + 1.5, getBlockPos().getZ() + .5);

                    if (d1 <= dmax) {
                        nearestPlayer = entity;
                        dmax = d1;
                    }
                }
            }
        }
        return nearestPlayer;
    }

    private void performTeleport() {
        // First check if the destination is still valid.
        if (!isDestinationStillValid()) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(teleportingPlayer);
            if (player != null) {
                TeleportationTools.applyBadEffectIfNeeded(player, 10, badTicks, totalTicks, false);
                Logging.warn(player, "Missing destination!");
            }
            clearTeleport(200);
            return;
        }

        TeleportDestination dest = getTeleportDestination();

        // The destination is valid. If this is a 'once' dial then we clear the destination here.
        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        if (data.once()) {
            setTeleportDestination(null, false);
        }

        boolean boosted = DialingDeviceTileEntity.isMatterBoosterAvailable(level, getBlockPos());
        if (boosted && energyStorage.getEnergyStored() < TeleportConfiguration.rfBoostedTeleport.get()) {
            // Not enough energy. We cannot do a boosted teleport.
            boosted = false;
        }
        Player player = level.getServer().getPlayerList().getPlayer(teleportingPlayer);
        if (player != null) {
            boolean boostNeeded = TeleportationTools.performTeleport(player, dest, badTicks, totalTicks, boosted);
            if (boostNeeded) {
                energyStorage.consumeEnergy(TeleportConfiguration.rfBoostedTeleport.get());
            }
        }

        teleportingPlayer = null;
    }

    private boolean isDestinationStillValid() {
        TeleportDestination dest = getTeleportDestination();
        return TeleportDestinations.get(level).isDestinationValid(dest);
    }

    private void handleEnergyShortage() {
        setChanged();
        // Not enough energy. This is a bad tick.
        badTicks++;
        if (TeleportationTools.mustInterrupt(badTicks, totalTicks)) {
            // Too many bad ticks. Total failure!
            Player player = level.getServer().getPlayerList().getPlayer(teleportingPlayer);
            if (player != null) {
                Logging.warn(player, "Power failure during transit!");
            }
            clearTeleport(200);
        }
    }

    private boolean isPlayerOutsideBeam() {
        Player player = level.getServer().getPlayerList().getPlayer(teleportingPlayer);
        if (player == null) {
            return true;
        }
        AABB playerBB = player.getBoundingBox();
        // Shouldn't be possible but there are mods...
        if (!playerBB.intersects(beamBox.get())) {
            Logging.message(player, "Teleportation was interrupted!");
            return true;
        }
        return false;
    }

    public void startTeleportation(Entity entity) {
        if (cooldownTimer > 0) {
            // In cooldown. We can't do teleport right now.
            return;
        }
        if (teleportingPlayer != null) {
            // Already teleporting
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }
        if (player.isPassenger() || player.isVehicle()) {
            cooldownTimer = 80;
            return;
        }

        MatterTransmitterData data = getData(TeleporterModule.MATTERTRANSMITTER_DATA);
        TeleportDestination dest = data.destination();
        if (data.destinationId() != null) {
            dest = getTeleportDestination();
        }

        if (dest != null && dest.isValid()) {
            int defaultCost = TeleportationTools.calculateRFCost(level, getBlockPos(), dest);
            int cost = (int) (defaultCost * (4.0f - infusable.getInfusedFactor()) / 4.0f);

            if (energyStorage.getEnergyStored() < cost) {
                Logging.warn(player, "Not enough power to start the teleport!");
                cooldownTimer = 80;
                return;
            }

            ResourceKey<Level> srcId = level.dimension();
            ResourceKey<Level> dstId = dest.getDimension();
            if (!TeleportationTools.checkValidTeleport(player, srcId, dstId)) {
                cooldownTimer = 80;
                return;
            }

            Logging.message(player, "Start teleportation...");
            teleportingPlayer = player.getUUID();
            int defaultTeleportTimer = TeleportationTools.calculateTime(level, getBlockPos(), dest);
            int teleportTimer = (int) (defaultTeleportTimer * (1.2f - infusable.getInfusedFactor()) / 1.2f);

            int defaultRf = TeleportConfiguration.rfTeleportPerTick.get();
            int rf = (int) (defaultRf * (4.0f - infusable.getInfusedFactor()) / 4.0f);
            int totalRfUsed = cost + rf * (teleportTimer+1);
            rfPerTick = totalRfUsed / (teleportTimer+1);

            totalTicks = teleportTimer;
            goodTicks = 0;
            badTicks = 0;
        } else {
            Logging.warn(player, "Something is wrong with the destination!");
        }
    }

    public static final Key<String> PARAM_PLAYER = new Key<>("player", Type.STRING);

    @ServerCommand
    public static final Command<?> CMD_ADDPLAYER = Command.<MatterTransmitterTileEntity>create("receiver.addPlayer",
            (te, player, params) -> te.addPlayer(params.get(PARAM_PLAYER)));

    @ServerCommand
    public static final Command<?> CMD_DELPLAYER = Command.<MatterTransmitterTileEntity>create("receiver.delPlayer",
            (te, player, params) -> te.delPlayer(params.get(PARAM_PLAYER)));

    @ServerCommand(type = String.class)
    public static final ListCommand<?, ?> CMD_GETPLAYERS = ListCommand.<MatterTransmitterTileEntity, String>create("rftoolsutility.transmitter.getPlayers",
            (te, player, params) -> te.getAllowedPlayers(),
            (te, player, params, list) -> GuiMatterTransmitter.storeAllowedPlayersForClient(list));

    // @todo 1.14
//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return pass == 1;
//    }

    @Nonnull
    private IMachineInformation createMachineInfo() {
        return new IMachineInformation() {
            private final String[] TAGS = new String[]{"dim", "coord", "name"};
            private final String[] TAG_DESCRIPTIONS = new String[]{"The dimension this transmitter is dialed too", "The coordinate this transmitter is dialed too", "The name of the destination"};

            @Override
            public int getTagCount() {
                return TAGS.length;
            }

            @Override
            public String getTagName(int index) {
                return TAGS[index];
            }

            @Override
            public String getTagDescription(int index) {
                return TAG_DESCRIPTIONS[index];
            }

            @Override
            public String getData(int index, long millis) {
                TeleportDestination destination = getTeleportDestination();
                if (destination == null) {
                    return "<not dialed>";
                }
                return switch (index) {
                    case 0 -> destination.getDimension().location().toString();
                    case 1 -> destination.getCoordinate().toString();
                    case 2 -> destination.getName();
                    default -> null;
                };
            }
        };
    }

    // @todo 1.21 cap
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
//        if (cap == CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY) {
//            return infoHandler.cast();
//        }
//        return super.getCapability(cap, facing);
//    }
}
