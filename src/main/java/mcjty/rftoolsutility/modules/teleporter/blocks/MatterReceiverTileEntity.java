package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterReceiver;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterSetup.CONTAINER_MATTER_RECEIVER;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterSetup.TYPE_MATTER_RECEIVER;

public class MatterReceiverTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final String CMD_ADDPLAYER = "receiver.addPlayer";
    public static final String CMD_DELPLAYER = "receiver.delPlayer";
    public static final Key<String> PARAM_PLAYER = new Key<>("player", Type.STRING);

    public static final String CMD_GETPLAYERS = "getPlayers";
    public static final String CLIENTCMD_GETPLAYERS = "getPlayers";

    private String name = null;
    private boolean privateAccess = false;
    private final Set<String> allowedPlayers = new HashSet<>();
    private int id = -1;

    public static final Key<String> VALUE_NAME = new Key<>("name", Type.STRING);
    public static final Key<Boolean> VALUE_PRIVATE = new Key<>("private", Type.BOOLEAN);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_NAME, this::getName, this::setName),
                new DefaultValue<>(VALUE_PRIVATE, this::isPrivateAccess, this::setPrivateAccess),
        };
    }

    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> new GenericEnergyStorage(this, true,
            TeleportConfiguration.RECEIVER_MAXENERGY.get(), TeleportConfiguration.RECEIVER_RECEIVEPERTICK.get()));
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Receiver")
            .containerSupplier((windowId,player) -> new GenericContainer(CONTAINER_MATTER_RECEIVER.get(), windowId, EmptyContainer.CONTAINER_FACTORY.get(), getPos(), MatterReceiverTileEntity.this))
            .energyHandler(energyHandler));
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(MatterReceiverTileEntity.this));

    private BlockPos cachedPos;

    public MatterReceiverTileEntity() {
        super(TYPE_MATTER_RECEIVER.get());
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public int getOrCalculateID() {
        if (id == -1) {
            TeleportDestinations destinations = TeleportDestinations.get(world);
            GlobalCoordinate gc = new GlobalCoordinate(getPos(), world);
            id = destinations.getNewId(gc);

            destinations.save();
            setId(id);
        }
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        markDirtyClient();
    }

    public void setName(String name) {
        this.name = name;
        TeleportDestinations destinations = TeleportDestinations.get(world);
        TeleportDestination destination = destinations.getDestination(getPos(), DimensionId.fromWorld(world));
        if (destination != null) {
            destination.setName(name);
            destinations.save();
        }

        markDirtyClient();
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            checkStateServer();
        }
    }

    public void consumeEnergy(long amount) {
        energyHandler.ifPresent(h -> {
            h.consumeEnergy(amount);
        });
    }

    private void checkStateServer() {
        if (!getPos().equals(cachedPos)) {
            TeleportDestinations destinations = TeleportDestinations.get(world);

            destinations.removeDestination(cachedPos, DimensionId.fromWorld(world));

            cachedPos = getPos();

            GlobalCoordinate gc = new GlobalCoordinate(getPos(), world);

            if (id == -1) {
                id = destinations.getNewId(gc);
            } else {
                destinations.assignId(gc, id);
            }
            destinations.addDestination(gc);
            destinations.save();

            markDirty();
        }
    }

    /**
     * This method is called after putting down a receiver that was earlier wrenched. We need to fix the data in
     * the destination.
     */
    public void updateDestination() {
        TeleportDestinations destinations = TeleportDestinations.get(world);

        GlobalCoordinate gc = new GlobalCoordinate(getPos(), world);
        TeleportDestination destination = destinations.getDestination(gc.getCoordinate(), gc.getDimension());
        if (destination != null) {
            destination.setName(name);

            if (id == -1) {
                id = destinations.getNewId(gc);
                markDirty();
            } else {
                destinations.assignId(gc, id);
            }

            destinations.save();
        }
        markDirtyClient();
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    public void setPrivateAccess(boolean privateAccess) {
        this.privateAccess = privateAccess;
        markDirtyClient();
    }

    public boolean checkAccess(UUID player) {
        if (!privateAccess) {
            return true;
        }
        PlayerEntity playerByUuid = world.getPlayerByUuid(player);
        return allowedPlayers.contains(playerByUuid.getDisplayName().getString());  // @todo 1.16 getFormattedText
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

    public int checkStatus() {
        BlockState state = world.getBlockState(getPos().up());
        Block block = state.getBlock();
        if (!block.isAir(state, world, getPos().up())) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_BLOCKED_MASK;
        }
        block = world.getBlockState(getPos().up(2)).getBlock();
        if (!block.isAir(state, world, getPos().up(2))) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_BLOCKED_MASK;
        }

        if (getStoredPower() < TeleportConfiguration.rfPerTeleportReceiver.get()) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_POWER_LOW_MASK;
        }

        return DialingDeviceTileEntity.DIAL_OK;
    }

    private int getStoredPower() {
        return energyHandler.map(GenericEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        cachedPos = new BlockPos(tagCompound.getInt("cachedX"), tagCompound.getInt("cachedY"), tagCompound.getInt("cachedZ"));
        readRestorableFromNBT(tagCompound);
    }

    public void readRestorableFromNBT(CompoundNBT tagCompound) {
        energyHandler.ifPresent(h -> h.setEnergy(tagCompound.getLong("Energy")));

        CompoundNBT info = tagCompound.getCompound("Info");
        name = info.getString("tpName");

        privateAccess = info.getBoolean("private");

        allowedPlayers.clear();
        ListNBT playerList = info.getList("players", Constants.NBT.TAG_STRING);
        for (int i = 0 ; i < playerList.size() ; i++) {
            allowedPlayers.add(playerList.getString(i));
        }
        if (info.contains("destinationId")) {
            id = info.getInt("destinationId");
        } else {
            id = -1;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        if (cachedPos != null) {
            tagCompound.putInt("cachedX", cachedPos.getX());
            tagCompound.putInt("cachedY", cachedPos.getY());
            tagCompound.putInt("cachedZ", cachedPos.getZ());
        }
        writeRestorableToNBT(tagCompound);
        return tagCompound;
    }

    public void writeRestorableToNBT(CompoundNBT tagCompound) {
        energyHandler.ifPresent(h -> tagCompound.putLong("Energy", h.getEnergy()));
        CompoundNBT info = getOrCreateInfo(tagCompound);
        if (name != null && !name.isEmpty()) {
            info.putString("tpName", name);
        }

        info.putBoolean("private", privateAccess);

        ListNBT playerTagList = new ListNBT();
        for (String player : allowedPlayers) {
            playerTagList.add(StringNBT.valueOf(player));
        }
        info.put("players", playerTagList);
        info.putInt("destinationId", id);
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_ADDPLAYER.equals(command)) {
            String playerName = params.get(PARAM_PLAYER);
            addPlayer(playerName);
            return true;
        } else if (CMD_DELPLAYER.equals(command)) {
            String playerName = params.get(PARAM_PLAYER);
            delPlayer(playerName);
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
            GuiMatterReceiver.storeAllowedPlayersForClient(Type.STRING.convert(list));
            return true;
        }
        return false;
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
        return super.getCapability(cap, facing);
    }
}
