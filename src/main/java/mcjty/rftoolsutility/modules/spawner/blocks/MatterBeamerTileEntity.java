package mcjty.rftoolsutility.modules.spawner.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.SlotDefinition.generic;

public class MatterBeamerTileEntity extends TickingTileEntity {

    public static final int TICKTIME = 20;

    public static final int SLOT_MATERIAL = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
        .slot(generic().in(), SLOT_MATERIAL, 28, 8)
        .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.basic(this, CONTAINER_FACTORY);

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, SpawnerConfiguration.BEAMER_MAXENERGY, SpawnerConfiguration.BEAMER_RECEIVEPERTICK);

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Beamer")
            .containerSupplier(container(SpawnerModule.CONTAINER_MATTER_BEAMER, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(MatterBeamerTileEntity.this);

    public static final Key<BlockPos> VALUE_DESTINATION = new Key<>("destination", Type.BLOCKPOS);

    // The location of the destination spawner..
    private BlockPos destination = null;
    private boolean glowing = false;

    private int ticker = TICKTIME;

    public MatterBeamerTileEntity(BlockPos pos, BlockState state) {
        super(SpawnerModule.TYPE_MATTER_BEAMER.get(), pos, state);
    }

    public boolean isPowered() {
        return powerLevel != 0;
    }

    public boolean isGlowing() {
        return glowing;
    }

    @Override
    protected void tickServer() {
        if (powerLevel == 0) {
            disableBlockGlow();
            return;
        }

        ticker--;
        if (ticker > 0) {
            return;
        }
        ticker = TICKTIME;

        BlockEntity te = null;
        if (destination != null) {
            te = level.getBlockEntity(destination);
            if (!(te instanceof SpawnerTileEntity)) {
                setDestination(null);
                return;
            }
        } else {
            return;
        }

        ItemStack itemStack = items.getStackInSlot(SLOT_MATERIAL);
        if (itemStack.isEmpty()) {
            disableBlockGlow();
            return;
        }

        SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) te;

        int maxblocks = (int) (SpawnerConfiguration.beamBlocksPerSend * (1.01 + infusable.getInfusedFactor() * 2.0));
        int numblocks = Math.min(maxblocks, itemStack.getCount());

        int rf = (int) (SpawnerConfiguration.beamRfPerObject * numblocks * (4.0f - infusable.getInfusedFactor()) / 4.0f);
        if (energyStorage.getEnergyStored() < rf) {
            return;
        }
        energyStorage.consumeEnergy(rf);

        if (spawnerTileEntity.addMatter(itemStack, numblocks, infusable.getInfusedFactor())) {
            items.decrStackSize(0, numblocks);
            enableBlockGlow();
        }
    }

    private void disableBlockGlow() {
        if (glowing) {
            glowing = false;
            level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, glowing), Block.UPDATE_ALL_IMMEDIATE);
            markDirtyQuick();
        }
    }

    private void enableBlockGlow() {
        if (!glowing) {
            glowing = true;
            level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, glowing), Block.UPDATE_ALL_IMMEDIATE);
            markDirtyQuick();
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        boolean oldglowing = glowing;

        super.onDataPacket(net, packet);

        super.onDataPacket(net, packet);

        if (level.isClientSide) {
            // If needed send a render update.
            if (oldglowing != glowing) {
                level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, glowing), Block.UPDATE_ALL_IMMEDIATE);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        return new AABB(xCoord - 4, yCoord - 4, zCoord - 4, xCoord + 5, yCoord + 5, zCoord + 5);
    }

    @Override
    public boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        if (world.isClientSide) {
            // @todo 1.19.3 .get()
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            useWrench(player);
        }
        return true;
    }

    // Called from client side when a wrench is used.
    private void useWrench(Player player) {
        BlockPos coord = RFToolsBase.instance.clientInfo.getSelectedTE();
        BlockEntity tileEntity = null;
        if (coord != null) {
            tileEntity = level.getBlockEntity(coord);
        }

        if (!(tileEntity instanceof MatterBeamerTileEntity)) {
            // None selected. Just select this one.
            RFToolsBase.instance.clientInfo.setSelectedTE(getBlockPos());
            SpawnerTileEntity destinationTE = getDestinationTE();
            if (destinationTE == null) {
                RFToolsBase.instance.clientInfo.setDestinationTE(null);
            } else {
                RFToolsBase.instance.clientInfo.setDestinationTE(destinationTE.getBlockPos());
            }
            Logging.message(player, "Select a spawner as destination");
        } else if (coord.equals(getBlockPos())) {
            // Unselect this one.
            RFToolsBase.instance.clientInfo.setSelectedTE(null);
            RFToolsBase.instance.clientInfo.setDestinationTE(null);
            setDestination(null);
            Logging.message(player, "Destination cleared!");
        }
    }

    public void setDestination(BlockPos destination) {
        this.destination = destination;
        disableBlockGlow();
        setChanged();

        if (level.isClientSide) {
            // We're on the client. Send change to server.
            PacketServerCommandTyped packet = new PacketServerCommandTyped(getBlockPos(), getDimension(), CMD_SETDESTINATION.name(), TypedMap.builder()
                    .put(PARAM_DESTINATION, destination)
                    .build());
            RFToolsUtilityMessages.INSTANCE.sendToServer(packet);
        } else {
            setChanged();
        }
    }

    public BlockPos getDestination() {
        return destination;
    }

    /**
     * Get the current destination. This function checks first if that destination is
     * still valid and if not it is reset to null (i.e. the destination was removed).
     * @return the destination TE or null if there is no valid one
     */
    private SpawnerTileEntity getDestinationTE() {
        if (destination == null) {
            return null;
        }
        BlockEntity te = level.getBlockEntity(destination);
        if (te instanceof SpawnerTileEntity) {
            return (SpawnerTileEntity) te;
        } else {
            destination = null;
            setChanged();
            return null;
        }
    }

    public static final Key<BlockPos> PARAM_DESTINATION = new Key<>("dest", Type.BLOCKPOS);
    @ServerCommand
    public static final Command<?> CMD_SETDESTINATION = Command.<MatterBeamerTileEntity>create("setDestination",
            (te, player, params) -> te.setDestination(params.get(PARAM_DESTINATION)));

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        destination = BlockPosTools.read(tagCompound, "dest");
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        BlockPosTools.write(tagCompound, "dest", destination);
        tagCompound.putBoolean("glowing", glowing);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        BlockPosTools.write(tagCompound, "dest", destination);
        tagCompound.putBoolean("glowing", glowing);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        destination = BlockPosTools.read(tagCompound, "dest");
        glowing = tagCompound.getBoolean("glowing");
    }

}
