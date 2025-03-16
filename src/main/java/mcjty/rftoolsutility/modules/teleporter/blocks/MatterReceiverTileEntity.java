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
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.client.GuiMatterReceiver;
import mcjty.rftoolsutility.modules.teleporter.data.MatterReceiverData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.rftoolsutility.modules.teleporter.TeleporterModule.CONTAINER_MATTER_RECEIVER;

public class MatterReceiverTileEntity extends TickingTileEntity {

    @GuiValue
    public static final Value<?, String> VALUE_NAME = Value.create("name", Type.STRING, MatterReceiverTileEntity::getName, MatterReceiverTileEntity::setName);

    @GuiValue(name = "private")
    public static final Value<?, Boolean> VALUE_PRIVATE = Value.create("private", Type.BOOLEAN, MatterReceiverTileEntity::isPrivateAccess, MatterReceiverTileEntity::setPrivateAccess);

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true,
            TeleportConfiguration.RECEIVER_MAXENERGY.get(), TeleportConfiguration.RECEIVER_RECEIVEPERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<MatterReceiverTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<MatterReceiverTileEntity, MenuProvider> screenHandler = tile -> new DefaultContainerProvider<GenericContainer>("Matter Receiver")
            .containerSupplier(empty(CONTAINER_MATTER_RECEIVER, tile))
            .energyHandler(() -> tile.energyStorage)
            .setupSync(tile);

    private final DefaultInfusable infusable = new DefaultInfusable(MatterReceiverTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<MatterReceiverTileEntity, IInfusable> INFUSABLE_CAP = tile -> tile.infusable;

    private BlockPos cachedPos;

    public MatterReceiverTileEntity(BlockPos pos, BlockState state) {
        super(TeleporterModule.MATTER_RECEIVER.be().get(), pos, state);
    }

    public DefaultInfusable getInfusable() {
        return infusable;
    }

    public String getName() {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        return data.name() == null ? "" : data.name();
    }

    public int getOrCalculateID() {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        int id = data.id();
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
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        return data.id();
    }

    public void setId(int id) {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        data = data.withId(id);
        setData(TeleporterModule.MATTERRECEIVER_DATA, data);
    }

    public void setName(String name) {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        data = data.withName(name);
        setData(TeleporterModule.MATTERRECEIVER_DATA, data);
        if (level.isClientSide()) {
            return;
        }
        TeleportDestinations destinations = TeleportDestinations.get(level);
        TeleportDestination destination = destinations.getDestination(getBlockPos(), level.dimension());
        if (destination != null) {
            destination = destination.withName(name);
            destinations.setDestination(GlobalPos.of(level.dimension(), getBlockPos()), destination);
            destinations.save();
        }
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

            MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
            int id = data.id();
            if (id == -1) {
                id = destinations.getNewId(gc);
                setData(TeleporterModule.MATTERRECEIVER_DATA, data.withId(id));
            } else {
                destinations.assignId(gc, id);
            }
            destinations.addDestination(gc);
            destinations.save();

            setChanged();
        }
    }

    public boolean isPrivateAccess() {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        return data.privateAccess();
    }

    public void setPrivateAccess(boolean privateAccess) {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        data = data.withShowFav(privateAccess);
        setData(TeleporterModule.MATTERRECEIVER_DATA, data);
        updateDestination();
    }

    /**
     * This method is called after putting down a receiver that was earlier wrenched. We need to fix the data in
     * the destination.
     */
    public TeleportDestination updateDestination() {
        if (level.isClientSide()) {
            return null;
        }

        TeleportDestinations destinations = TeleportDestinations.get(level);

        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        GlobalPos gc = GlobalPos.of(level.dimension(), getBlockPos());
        TeleportDestination destination = destinations.getDestination(gc.pos(), gc.dimension());
        if (destination != null) {
            destination = destination.withName(data.name());

            int id = data.id();
            if (id == -1) {
                id = destinations.getNewId(gc);
                data = data.withId(id);
                setData(TeleporterModule.MATTERRECEIVER_DATA, data);
            } else {
                destinations.assignId(gc, id);
            }
            destination = destination.withPrivateAccess(data.privateAccess());
            destination = destination.withAllowedPlayers(data.players());
            destinations.setDestination(gc, destination);

            destinations.save();
        }
        setChanged();
        return destination;
    }

    // Server side only
//    public boolean checkAccess(UUID player) {
//        if (!privateAccess) {
//            return true;
//        }
//        Player playerByUuid = level.getServer().getPlayerList().getPlayer(player);
//        if (playerByUuid == null) {
//            return true;
//        }
//        return allowedPlayers.contains(playerByUuid.getDisplayName().getString());  // @todo 1.16 getFormattedText
//    }

    public Set<String> getAllowedPlayers() {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        return data.players();
    }

    public void addPlayer(String player) {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        if (!data.players().contains(player)) {
            data = data.addPlayer(player);
            setData(TeleporterModule.MATTERRECEIVER_DATA, data);
            updateDestination();
        }
    }

    public void delPlayer(String player) {
        MatterReceiverData data = getData(TeleporterModule.MATTERRECEIVER_DATA);
        if (data.players().contains(player)) {
            data = data.removePlayer(player);
            setData(TeleporterModule.MATTERRECEIVER_DATA, data);
            updateDestination();
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        cachedPos = new BlockPos(tag.getInt("cachedX"), tag.getInt("cachedY"), tag.getInt("cachedZ"));
        energyStorage.load(tag, "energy", provider);
        infusable.load(tag, "infusable");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (cachedPos != null) {
            tag.putInt("cachedX", cachedPos.getX());
            tag.putInt("cachedY", cachedPos.getY());
            tag.putInt("cachedZ", cachedPos.getZ());
        }
        energyStorage.save(tag, "energy", provider);
        infusable.save(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(TeleporterModule.ITEM_MATTERRECEIVER_DATA);
        if (data != null) {
            setData(TeleporterModule.MATTERRECEIVER_DATA, data);
        }
        energyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        infusable.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(TeleporterModule.ITEM_MATTERRECEIVER_DATA, getData(TeleporterModule.MATTERRECEIVER_DATA));
        energyStorage.collectImplicitComponents(builder);
        infusable.collectImplicitComponents(builder);
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
            (te, player, params) -> new ArrayList<>(te.getAllowedPlayers()),
            (te, player, params, list) -> GuiMatterReceiver.storeAllowedPlayersForClient(list));
}
