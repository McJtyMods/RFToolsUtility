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
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterReceiver;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_MATTER_RECEIVER;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.TYPE_MATTER_RECEIVER;

public class MatterReceiverTileEntity extends TickingTileEntity {

    private final Set<String> allowedPlayers = new HashSet<>();
    private int id = -1;

    private String name = null;
    @GuiValue
    public static final Value<?, String> VALUE_NAME = Value.<MatterReceiverTileEntity, String>create("name", Type.STRING, MatterReceiverTileEntity::getName, MatterReceiverTileEntity::setName);
    @GuiValue(name = "private")
    private boolean privateAccess = false;

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true,
            TeleportConfiguration.RECEIVER_MAXENERGY.get(), TeleportConfiguration.RECEIVER_RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Receiver")
            .containerSupplier(empty(CONTAINER_MATTER_RECEIVER, this))
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(MatterReceiverTileEntity.this));

    private BlockPos cachedPos;

    public MatterReceiverTileEntity(BlockPos pos, BlockState state) {
        super(TYPE_MATTER_RECEIVER.get(), pos, state);
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
        this.name = name;
        if (level.isClientSide()) {
            return;
        }
        TeleportDestinations destinations = TeleportDestinations.get(level);
        TeleportDestination destination = destinations.getDestination(getBlockPos(), level.dimension());
        if (destination != null) {
            destination.setName(name);
            destinations.save();
        }

        setChanged();
    }

    public void storeEnergy(long amount) {
        energyStorage.setEnergy(amount);
    }

    @Override
    protected void tickServer() {
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

    // Server side only
    public boolean checkAccess(UUID player) {
        if (!privateAccess) {
            return true;
        }
        Player playerByUuid = level.getServer().getPlayerList().getPlayer(player);
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
        if (!level.getBlockState(getBlockPos().above()).isAir()) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_BLOCKED_MASK;
        }
        if (!level.getBlockState(getBlockPos().above(2)).isAir()) {
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
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        cachedPos = new BlockPos(tagCompound.getInt("cachedX"), tagCompound.getInt("cachedY"), tagCompound.getInt("cachedZ"));
        readRestorableFromNBT(tagCompound);
    }

    public void readRestorableFromNBT(CompoundTag tagCompound) {
        energyStorage.setEnergy(tagCompound.getLong("Energy"));

        CompoundTag info = tagCompound.getCompound("Info");
        name = info.getString("tpName");

        privateAccess = info.getBoolean("private");

        allowedPlayers.clear();
        ListTag playerList = info.getList("players", Tag.TAG_STRING);
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
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        if (cachedPos != null) {
            tagCompound.putInt("cachedX", cachedPos.getX());
            tagCompound.putInt("cachedY", cachedPos.getY());
            tagCompound.putInt("cachedZ", cachedPos.getZ());
        }
        writeRestorableToNBT(tagCompound);
    }

    public void writeRestorableToNBT(CompoundTag tagCompound) {
        tagCompound.putLong("Energy", energyStorage.getEnergy());
        CompoundTag info = getOrCreateInfo(tagCompound);
        if (name != null && !name.isEmpty()) {
            info.putString("tpName", name);
        }

        info.putBoolean("private", privateAccess);

        ListTag playerTagList = new ListTag();
        for (String player : allowedPlayers) {
            playerTagList.add(StringTag.valueOf(player));
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
