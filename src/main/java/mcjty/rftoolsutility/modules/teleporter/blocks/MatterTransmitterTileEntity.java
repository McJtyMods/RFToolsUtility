package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Cached;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterTransmitter;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_MATTER_TRANSMITTER;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_MATTER_TRANSMITTER;

public class MatterTransmitterTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final String CMD_ADDPLAYER = "transmitter.addPlayer";
    public static final String CMD_DELPLAYER = "transmitter.delPlayer";
    public static final Key<String> PARAM_PLAYER = new Key<>("player", Type.STRING);

    public static final String CMD_GETPLAYERS = "getPlayers";
    public static final String CLIENTCMD_GETPLAYERS = "getPlayers";

    // Server side: current dialing destination. Old system.
    private TeleportDestination teleportDestination = null;
    // Server side: current dialing destination. New system.
    private Integer teleportId = null;
    // If this is true the dial is cleared as soon as a player teleports.
    private boolean once = false;

    private String name = null;
    private boolean privateAccess = false;
    private boolean beamHidden = false;
    private Set<String> allowedPlayers = new HashSet<>();
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

    private final Cached<AxisAlignedBB> beamBox = Cached.of(this::createBeamBox);

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, TeleportConfiguration.TRANSMITTER_MAXENERGY.get(), TeleportConfiguration.TRANSMITTER_RECEIVEPERTICK.get());
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Transmitter")
            .containerSupplier((windowId,player) -> new GenericContainer(CONTAINER_MATTER_TRANSMITTER.get(), windowId, ContainerFactory.EMPTY.get(), getBlockPos(), MatterTransmitterTileEntity.this))
            .energyHandler(() -> energyStorage));
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(MatterTransmitterTileEntity.this));
    private final LazyOptional<IMachineInformation> infoHandler = LazyOptional.of(this::createMachineInfo);

    public static final Key<String> VALUE_NAME = new Key<>("name", Type.STRING);
    public static final Key<Boolean> VALUE_PRIVATE = new Key<>("private", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_BEAM = new Key<>("beam", Type.BOOLEAN);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_NAME, this::getName, this::setName),
                new DefaultValue<>(VALUE_PRIVATE, this::isPrivateAccess, this::setPrivateAccess),
                new DefaultValue<>(VALUE_BEAM, this::isBeamHidden, this::setBeamHidden),
        };
    }

    public MatterTransmitterTileEntity() {
        super(TYPE_MATTER_TRANSMITTER.get());
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
        markDirtyClient();
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    public void setPrivateAccess(boolean privateAccess) {
        this.privateAccess = privateAccess;
        markDirtyClient();
    }

    public boolean isBeamHidden() {
        return beamHidden;
    }

    public void setBeamHidden(boolean b) {
        this.beamHidden = b;
        markDirtyClient();
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
        ServerPlayerEntity entity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player);
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
            markDirtyClient();
        }
    }

    public void delPlayer(String player) {
        if (allowedPlayers.contains(player)) {
            allowedPlayers.remove(player);
            markDirtyClient();
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
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
    protected void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        name = info.getString("tpName");
        BlockPos c = BlockPosTools.read(info, "dest");
        if (c == null) {
            teleportDestination = null;
        } else {
            String dim = info.getString("dim");
            teleportDestination = new TeleportDestination(c, RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim)));
        }
        if (info.contains("destId")) {
            teleportId = info.getInt("destId");
        } else {
            teleportId = null;
        }
        privateAccess = info.getBoolean("private");
        beamHidden = info.getBoolean("hideBeam");
        once = info.getBoolean("once");

        allowedPlayers.clear();
        ListNBT playerList = info.getList("players", Constants.NBT.TAG_STRING);
        for (int i = 0 ; i < playerList.size() ; i++) {
            String player = playerList.getString(i);
            allowedPlayers.add(player);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
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
        return tagCompound;
    }

    @Override
    protected void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        if (name != null && !name.isEmpty()) {
            info.putString("tpName", name);
        }
        if (teleportDestination != null) {
            BlockPos c = teleportDestination.getCoordinate();
            if (c != null) {
                BlockPosTools.write(info, "dest", c);
                info.putString("dim", teleportDestination.getDimension().getRegistryName().toString());
            }
        }
        if (teleportId != null) {
            info.putInt("destId", teleportId);
        }

        info.putBoolean("private", privateAccess);
        info.putBoolean("hideBeam", beamHidden);
        info.putBoolean("once", once);

        ListNBT playerTagList = new ListNBT();
        for (String player : allowedPlayers) {
            playerTagList.add(StringNBT.valueOf(player));
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
    public void tick() {
        if (!level.isClientSide) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
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
            PlayerEntity player = level.getPlayerByUUID(teleportingPlayer);
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

        RegistryKey<World> dimension = destination.getDimension();

        // @todo
//        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
//        if (dimensionManager.getDimensionInformation(dimension) != null) {
//            // This is an RFTools dimension. Check power.
//            DimensionStorage dimensionStorage = DimensionStorage.getDimensionStorage(world);
//            int energyLevel = dimensionStorage.getEnergyLevel(dimension);
//            if (energyLevel < DimletConfiguration.DIMPOWER_WARN_TP) {
//                return TeleportationTools.STATUS_WARN;
//            }
//        }


        World w = WorldTools.getWorld(level, dimension);
        // By default we will not check if the dimension is not loaded. Can be changed in config.
        if (w == null) {
            if (TeleportConfiguration.matterTransmitterLoadWorld.get() == -1) {
                return TeleportationTools.STATUS_UNKNOWN;
            } else {
                w = WorldTools.loadWorld(dimension);
                checkReceiverStatusCounter = TeleportConfiguration.matterTransmitterLoadWorld.get();
            }
        }
        BlockPos c = destination.getCoordinate();

        boolean exists = WorldTools.isLoaded(w, c);
        if (!exists) {
            if (TeleportConfiguration.matterTransmitterLoadChunk.get() == -1) {
                return TeleportationTools.STATUS_UNKNOWN;
            } else {
                checkReceiverStatusCounter = TeleportConfiguration.matterTransmitterLoadChunk.get();
            }
        }

        TileEntity tileEntity = w.getBlockEntity(c);
        if (!(tileEntity instanceof MatterReceiverTileEntity)) {
            return TeleportationTools.STATUS_WARN;
        }

        MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) tileEntity;

        int status = matterReceiverTileEntity.checkStatus();
        return (status == DialingDeviceTileEntity.DIAL_OK) ? TeleportationTools.STATUS_OK : TeleportationTools.STATUS_WARN;
    }

    private void clearTeleport(int cooldown) {
        setChanged();
        TeleportationTools.applyBadEffectIfNeeded(level.getPlayerByUUID(teleportingPlayer), 0, badTicks, totalTicks, false);
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

    private AxisAlignedBB createBeamBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        return new AxisAlignedBB(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
    }

    private void searchForNearestPlayer() {
        List<Entity> l = level.getEntitiesOfClass(PlayerEntity.class, beamBox.get());
        Entity nearestPlayer = findNearestPlayer(l);

        if (nearestPlayer == null) {
            cooldownTimer = 5;
            return;
        }
        AxisAlignedBB playerBB = nearestPlayer.getBoundingBox();
        // Shouldn't be possible but there are mods...
        if (playerBB == null) {
            cooldownTimer = 5;
            return;
        }
        if (playerBB.intersects(beamBox.get())) {
            startTeleportation(nearestPlayer);
        } else {
            cooldownTimer = 5;
        }
    }

    private Entity findNearestPlayer(List<Entity> l) {
        Entity nearestPlayer = null;
        double dmax = Double.MAX_VALUE;
        for (Entity entity : l) {
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (player.isPassenger() || player.isVehicle()) {
                    // Ignore players that are riding a horse
                    continue;
                }

                if (player.getName() != null) {
                    if ((!isPrivateAccess()) || allowedPlayers.contains(player.getDisplayName().getString())) { // @todo 1.16 was getFormattedText()
                        double d1 = entity.distanceToSqr(getBlockPos().getX() + .5, getBlockPos().getY() + 1.5, getBlockPos().getZ() + .5);

                        if (d1 <= dmax) {
                            nearestPlayer = entity;
                            dmax = d1;
                        }
                    }
                }
            }
        }
        return nearestPlayer;
    }

    private void performTeleport() {
        // First check if the destination is still valid.
        if (!isDestinationStillValid()) {
            PlayerEntity player = level.getPlayerByUUID(teleportingPlayer);
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
        PlayerEntity player = level.getPlayerByUUID(teleportingPlayer);
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
            PlayerEntity player = level.getPlayerByUUID(teleportingPlayer);
            if (player != null) {
                Logging.warn(player, "Power failure during transit!");
            }
            clearTeleport(200);
        }
        return;
    }

    private boolean isPlayerOutsideBeam() {
        PlayerEntity player = level.getPlayerByUUID(teleportingPlayer);
        if (player == null) {
            return true;
        }
        AxisAlignedBB playerBB = player.getBoundingBox();
        // Shouldn't be possible but there are mods...
        if (playerBB == null) {
            return true;
        }
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
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
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

            RegistryKey<World> srcId = level.dimension();
            RegistryKey<World> dstId = dest.getDimension();
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

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_ADDPLAYER.equals(command)) {
            addPlayer(params.get(PARAM_PLAYER));
            return true;
        } else if (CMD_DELPLAYER.equals(command)) {
            delPlayer(params.get(PARAM_PLAYER));
            return true;
        }
        return false;
    }


    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, TypedMap args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (CMD_GETPLAYERS.equals(command)) {
            return type.convert(getAllowedPlayers());
        }
        return Collections.emptyList();
    }

    @Override
    public <T> boolean receiveListFromServer(String command, List<T> list, Type<T> type) {
        boolean rc = super.receiveListFromServer(command, list, type);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETPLAYERS.equals(command)) {
            GuiMatterTransmitter.storeAllowedPlayersForClient(Type.STRING.convert(list));
            return true;
        }
        return false;
    }


    // @todo 1.14
//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return pass == 1;
//    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getBlockPos(), getBlockPos().offset(1, 4, 1));
    }

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
                switch (index) {
                    case 0: return destination.getDimension().getRegistryName().toString();
                    case 1: return destination.getCoordinate().toString();
                    case 2: return destination.getName();
                }
                return null;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        if (cap == CapabilityInfusable.INFUSABLE_CAPABILITY) {
            return infusableHandler.cast();
        }
        if (cap == CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY) {
            return infoHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
