package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsutility.RFToolsUtility;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_MATTER_RECEIVER;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_MATTER_RECEIVER;

public class MatterReceiverTileEntity extends GenericTileEntity implements ITickableTileEntity {

    private String name = null;
    private boolean privateAccess = false;
    private final Set<String> allowedPlayers = new HashSet<>();
    private int id = -1;

    @GuiValue
    public static final Value<?, String> VALUE_NAME = Value.<MatterReceiverTileEntity, String>create("name", Type.STRING, MatterReceiverTileEntity::getName, MatterReceiverTileEntity::setName);
    @GuiValue
    public static final Value<?, Boolean> VALUE_PRIVATE = Value.<MatterReceiverTileEntity, Boolean>create("private", Type.BOOLEAN, MatterReceiverTileEntity::isPrivateAccess, MatterReceiverTileEntity::setPrivateAccess);

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true,
            TeleportConfiguration.RECEIVER_MAXENERGY.get(), TeleportConfiguration.RECEIVER_RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Receiver")
            .containerSupplier((windowId,player) -> new GenericContainer(CONTAINER_MATTER_RECEIVER.get(), windowId, ContainerFactory.EMPTY.get(), getBlockPos(), MatterReceiverTileEntity.this))
            .dataListener(Sync.values(new ResourceLocation(RFToolsUtility.MODID, "data"), this))
            .energyHandler(() -> energyStorage));

    @Cap(type = CapType.INFUSABLE)
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
            TeleportDestinations destinations = TeleportDestinations.get(level);
            GlobalPos gc = GlobalPos.of(level.dimension(), getBlockPos());
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
        setChanged();
    }

    public void setName(String name) {
        if (level.isClientSide()) {
            return;
        }
        this.name = name;
        TeleportDestinations destinations = TeleportDestinations.get(level);
        TeleportDestination destination = destinations.getDestination(getBlockPos(), level.dimension());
        if (destination != null) {
            destination.setName(name);
            destinations.save();
        }

        setChanged();
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            checkStateServer();
        }
    }

    public void storeEnergy(long amount) {
        energyStorage.setEnergy(amount);
    }

    private void checkStateServer() {
        if (!getBlockPos().equals(cachedPos)) {
            TeleportDestinations destinations = TeleportDestinations.get(level);

            destinations.removeDestination(cachedPos, level.dimension());

            cachedPos = getBlockPos();

            GlobalPos gc = GlobalPos.of(level.dimension(), getBlockPos());

            if (id == -1) {
                id = destinations.getNewId(gc);
            } else {
                destinations.assignId(gc, id);
            }
            destinations.addDestination(gc);
            destinations.save();

            setChanged();
        }
    }

    /**
     * This method is called after putting down a receiver that was earlier wrenched. We need to fix the data in
     * the destination.
     */
    public void updateDestination() {
        TeleportDestinations destinations = TeleportDestinations.get(level);

        GlobalPos gc = GlobalPos.of(level.dimension(), getBlockPos());
        TeleportDestination destination = destinations.getDestination(gc.pos(), gc.dimension());
        if (destination != null) {
            destination.setName(name);

            if (id == -1) {
                id = destinations.getNewId(gc);
                setChanged();
            } else {
                destinations.assignId(gc, id);
            }

            destinations.save();
        }
        setChanged();
    }

    public boolean isPrivateAccess() {
        return privateAccess;
    }

    public void setPrivateAccess(boolean privateAccess) {
        this.privateAccess = privateAccess;
        setChanged();
    }

    public boolean checkAccess(UUID player) {
        if (!privateAccess) {
            return true;
        }
        PlayerEntity playerByUuid = level.getPlayerByUUID(player);
        if (playerByUuid == null) {
            return true;
        }
        return allowedPlayers.contains(playerByUuid.getDisplayName().getString());  // @todo 1.16 getFormattedText
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

    public int checkStatus() {
        BlockState state = level.getBlockState(getBlockPos().above());
        Block block = state.getBlock();
        if (!block.isAir(state, level, getBlockPos().above())) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_BLOCKED_MASK;
        }
        block = level.getBlockState(getBlockPos().above(2)).getBlock();
        if (!block.isAir(state, level, getBlockPos().above(2))) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_BLOCKED_MASK;
        }

        if (getStoredPower() < TeleportConfiguration.rfPerTeleportReceiver.get()) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_POWER_LOW_MASK;
        }

        return DialingDeviceTileEntity.DIAL_OK;
    }

    private int getStoredPower() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        cachedPos = new BlockPos(tagCompound.getInt("cachedX"), tagCompound.getInt("cachedY"), tagCompound.getInt("cachedZ"));
        readRestorableFromNBT(tagCompound);
    }

    public void readRestorableFromNBT(CompoundNBT tagCompound) {
        energyStorage.setEnergy(tagCompound.getLong("Energy"));

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

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        super.save(tagCompound);
        if (cachedPos != null) {
            tagCompound.putInt("cachedX", cachedPos.getX());
            tagCompound.putInt("cachedY", cachedPos.getY());
            tagCompound.putInt("cachedZ", cachedPos.getZ());
        }
        writeRestorableToNBT(tagCompound);
        return tagCompound;
    }

    public void writeRestorableToNBT(CompoundNBT tagCompound) {
        tagCompound.putLong("Energy", energyStorage.getEnergy());
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

    public static final Key<String> PARAM_PLAYER = new Key<>("player", Type.STRING);

    @ServerCommand
    public static final Command<?> CMD_ADDPLAYER = Command.<MatterReceiverTileEntity>create("receiver.addPlayer",
            (te, player, params) -> te.addPlayer(params.get(PARAM_PLAYER)));

    @ServerCommand
    public static final Command<?> CMD_DELPLAYER = Command.<MatterReceiverTileEntity>create("receiver.delPlayer",
            (te, player, params) -> te.delPlayer(params.get(PARAM_PLAYER)));

    @ServerCommand(type = String.class)
    public static final ListCommand<?, ?> CMD_GETPLAYERS = ListCommand.<MatterReceiverTileEntity, String>create("rftoolsutility.receiver.getPlayers",
            (te, player, params) -> te.getAllowedPlayers(),
            (te, player, params, list) -> GuiMatterReceiver.storeAllowedPlayersForClient(list));
}
