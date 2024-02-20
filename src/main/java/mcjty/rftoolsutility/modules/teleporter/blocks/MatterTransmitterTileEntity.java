package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Cached;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsutility.compat.RFToolsDimCompat;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterTransmitter;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_MATTER_TRANSMITTER;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_MATTER_TRANSMITTER;

public class MatterTransmitterTileEntity extends TickingTileEntity {

    // Server side: current dialing destination. Old system.
    private TeleportDestination teleportDestination = null;
    // Server side: current dialing destination. New system.
    private Integer teleportId = null;
    // If this is true the dial is cleared as soon as a player teleports.
    private boolean once = false;

    private final Set<String> allowedPlayers = new HashSet<>();
    private int status = TeleportationTools.STATUS_OK;

    // Server side: the player we're currently teleporting.
    private UUID teleportingPlayer = null;
    private int teleportTimer = 0;
    private int cooldownTimer = 0;
    private int totalTicks;
    private int goodTicks;
    private int badTicks;
    private int rfPerTick = 0;

    private int checkReceiverStatusCounter = 20;

    private final Cached<AABB> beamBox = Cached.of(this::createBeamBox);

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, TeleportConfiguration.TRANSMITTER_MAXENERGY.get(), TeleportConfiguration.TRANSMITTER_RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Transmitter")
            .containerSupplier(empty(CONTAINER_MATTER_TRANSMITTER, this))
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(MatterTransmitterTileEntity.this));

    private final LazyOptional<IMachineInformation> infoHandler = LazyOptional.of(this::createMachineInfo);

    @GuiValue
    private String name = null;

    @GuiValue(name = "private")
    private boolean privateAccess = false;

    @GuiValue(name = "beam")
    private boolean beamHidden = false;

    public MatterTransmitterTileEntity(BlockPos pos, BlockState state) {
        super(TYPE_MATTER_TRANSMITTER.get(), pos, state);
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
        setChanged();
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    public void setPrivateAccess(boolean privateAccess) {
        this.privateAccess = privateAccess;
        setChanged();
    }

    public boolean isBeamHidden() {
        return beamHidden;
    }

    public void setBeamHidden(boolean b) {
        this.beamHidden = b;
        setChanged();
    }

    public boolean isOnce() {
        return once;
    }

    public boolean checkAccess(String player) {
        if (!privateAccess) {
            return true;
        }
        return allowedPlayers.contains(player);
    }

    public boolean checkAccess(UUID player) {
        if (!privateAccess) {
            return true;
        }
        ServerPlayer entity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player);
        if (entity == null) {
            return false;
        }
        return allowedPlayers.contains(entity.getDisplayName().getString());    // @todo 1.16 getFormattedText()
    }

    public int getStatus() {
        return status;
    }

    public List<String> getAllowedPlayers() {
        return new ArrayList<>(allowedPlayers);
    }

    public void addPlayer(String player) {
        if (!allowedPlayers.contains(player)) {
            allowedPlayers.add(player);
            setChanged();
        }
    }

    public void delPlayer(String player) {
        if (allowedPlayers.contains(player)) {
            allowedPlayers.remove(player);
            setChanged();
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        CompoundTag info = getOrCreateInfo(tagCompound);
        if (teleportDestination != null) {
            BlockPos c = teleportDestination.getCoordinate();
            if (c != null) {
                BlockPosTools.write(info, "dest", c);
                info.putString("dim", teleportDestination.getDimension().location().toString());
            }
        }
        if (teleportId != null) {
            info.putInt("destId", teleportId);
        }
        info.putBoolean("hideBeam", beamHidden);
        tagCompound.putInt("status", status);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        CompoundTag info = tagCompound.getCompound("Info");
        BlockPos c = BlockPosTools.read(info, "dest");
        if (c == null) {
            teleportDestination = null;
        } else {
            String dim = info.getString("dim");
            teleportDestination = new TeleportDestination(c, LevelTools.getId(dim));
        }
        if (info.contains("destId")) {
            teleportId = info.getInt("destId");
        } else {
            teleportId = null;
        }
        beamHidden = info.getBoolean("hideBeam");
        status = tagCompound.getInt("status");
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        teleportTimer = tagCompound.getInt("tpTimer");
        cooldownTimer = tagCompound.getInt("cooldownTimer");
        totalTicks = tagCompound.getInt("totalTicks");
        goodTicks = tagCompound.getInt("goodTicks");
        badTicks = tagCompound.getInt("badTicks");
        if (tagCompound.hasUUID("tpPlayer")) {
            teleportingPlayer = tagCompound.getUUID("tpPlayer");
        } else {
            teleportingPlayer = null;
        }
        status = tagCompound.getInt("status");
        rfPerTick = tagCompound.getInt("rfPerTick");
    }

    @Override
    protected void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        loadClientDataFromNBT(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        name = info.getString("tpName");
        privateAccess = info.getBoolean("private");
        once = info.getBoolean("once");

        allowedPlayers.clear();
        ListTag playerList = info.getList("players", Tag.TAG_STRING);
        for (int i = 0 ; i < playerList.size() ; i++) {
            String player = playerList.getString(i);
            allowedPlayers.add(player);
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putInt("tpTimer", teleportTimer);
        tagCompound.putInt("cooldownTimer", cooldownTimer);
        tagCompound.putInt("totalTicks", totalTicks);
        tagCompound.putInt("goodTicks", goodTicks);
        tagCompound.putInt("badTicks", badTicks);
        if (teleportingPlayer != null) {
            tagCompound.putUUID("tpPlayer", teleportingPlayer);
        }
        tagCompound.putInt("status", status);
        tagCompound.putInt("rfPerTick", rfPerTick);
    }

    @Override
    protected void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        if (name != null && !name.isEmpty()) {
            info.putString("tpName", name);
        }
        saveClientDataToNBT(tagCompound);

        info.putBoolean("private", privateAccess);
        info.putBoolean("once", once);

        ListTag playerTagList = new ListTag();
        for (String player : allowedPlayers) {
            playerTagList.add(StringTag.valueOf(player));
        }
        info.put("players", playerTagList);
    }

    public boolean isDialed() {
        return teleportId != null || teleportDestination != null;
    }

    public Integer getTeleportId() {
        if (isDialed() && teleportId == null) {
            getTeleportDestination();
        }
        return teleportId;
    }

    public TeleportDestination getTeleportDestination() {
        if (teleportId != null) {
            TeleportDestinations teleportDestinations = TeleportDestinations.get(level);
            GlobalPos gc = teleportDestinations.getCoordinateForId(teleportId);
            if (gc == null) {
                return null;
            } else {
                return teleportDestinations.getDestination(gc.pos(), gc.dimension());
            }
        }
        return teleportDestination;
    }

    public void setTeleportDestination(TeleportDestination teleportDestination, boolean once) {
        this.teleportDestination = null;
        this.teleportId = null;
        this.once = once;
        if (teleportDestination != null) {
            TeleportDestinations destinations = TeleportDestinations.get(level);
            Integer id = destinations.getIdForCoordinate(GlobalPos.of(teleportDestination.getDimension(), teleportDestination.getCoordinate()));
            if (id == null) {
                this.teleportDestination = teleportDestination;
            } else {
                this.teleportId = id;
            }
        }
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

        if (isCoolingDown()) {
            // We're still in cooldown. Do nothing.
            return;
        } else if (teleportingPlayer == null) {
            // If we have a valid destination we check here if there is a player on this transmitter.
            if (isDestinationValid()) {
                searchForNearestPlayer();
            }
        } else if (teleportDestination == null && teleportId == null) {
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
        return teleportId != null || (teleportDestination != null && teleportDestination.isValid());
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

                if ((!isPrivateAccess()) || allowedPlayers.contains(player.getDisplayName().getString())) { // @todo 1.16 was getFormattedText()
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
        if (once) {
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

        TeleportDestination dest = teleportDestination;
        if (teleportId != null) {
            dest = getTeleportDestination();
        }

        if (dest != null && dest.isValid()) {
            int defaultCost = TeleportationTools.calculateRFCost(level, getBlockPos(), dest);
            int cost = infusableHandler.map(inf -> (int) (defaultCost * (4.0f - inf.getInfusedFactor()) / 4.0f)).orElse(defaultCost);

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
            int teleportTimer = infusableHandler.map(inf -> (int) (defaultTeleportTimer * (1.2f - inf.getInfusedFactor()) / 1.2f)).orElse(defaultTeleportTimer);

            int defaultRf = TeleportConfiguration.rfTeleportPerTick.get();
            int rf = infusableHandler.map(inf -> (int) (defaultRf * (4.0f - inf.getInfusedFactor()) / 4.0f)).orElse(defaultRf);
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

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos(), getBlockPos().offset(1, 4, 1));
    }

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY) {
            return infoHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
