package mcjty.rftoolsutility.modules.spawner.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerSetup;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.input;

public class MatterBeamerTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int TICKTIME = 20;

    public static final int SLOT_MATERIAL = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
        .slot(input(), CONTAINER_CONTAINER, SLOT_MATERIAL, 28, 8)
        .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final GenericEnergyStorage storage = new GenericEnergyStorage(this, true, SpawnerConfiguration.BEAMER_MAXENERGY, SpawnerConfiguration.BEAMER_RECEIVEPERTICK);
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> storage);

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Matter Beamer")
            .containerSupplier((windowId,player) -> new GenericContainer(SpawnerSetup.CONTAINER_MATTER_BEAMER.get(), windowId, CONTAINER_FACTORY.get(), getPos(), MatterBeamerTileEntity.this))
            .itemHandler(itemHandler)
            .energyHandler(energyHandler));

    private final IInfusable infusable = new DefaultInfusable(MatterBeamerTileEntity.this);
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> infusable);

    public static final Key<BlockPos> VALUE_DESTINATION = new Key<>("destination", Type.BLOCKPOS);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_DESTINATION, this::getDestination, this::setDestination)
        };
    }


    // The location of the destination spawner..
    private BlockPos destination = null;
    private boolean glowing = false;

    private int ticker = TICKTIME;

    public MatterBeamerTileEntity() {
        super(SpawnerSetup.TYPE_MATTER_BEAMER.get());
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            checkStateServer();
        }
    }

    public boolean isPowered() {
        return powerLevel != 0;
    }

    public boolean isGlowing() {
        return glowing;
    }

    private void checkStateServer() {
        if (powerLevel == 0) {
            disableBlockGlow();
            return;
        }

        ticker--;
        if (ticker > 0) {
            return;
        }
        ticker = TICKTIME;

        TileEntity te = null;
        if (destination != null) {
            te = world.getTileEntity(destination);
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
        if (storage.getEnergyStored() < rf) {
            return;
        }
        storage.consumeEnergy(rf);

        if (spawnerTileEntity.addMatter(itemStack, numblocks, infusable.getInfusedFactor())) {
            items.decrStackSize(0, numblocks);
            enableBlockGlow();
        }
    }

    private void disableBlockGlow() {
        if (glowing) {
            glowing = false;
            world.setBlockState(pos, getBlockState().with(BlockStateProperties.LIT, glowing), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            markDirtyQuick();
        }
    }

    private void enableBlockGlow() {
        if (!glowing) {
            glowing = true;
            world.setBlockState(pos, getBlockState().with(BlockStateProperties.LIT, glowing), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            markDirtyQuick();
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        boolean oldglowing = glowing;

        super.onDataPacket(net, packet);

        super.onDataPacket(net, packet);

        if (world.isRemote) {
            // If needed send a render update.
            if (oldglowing != glowing) {
                world.setBlockState(pos, getBlockState().with(BlockStateProperties.LIT, glowing), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        int xCoord = getPos().getX();
        int yCoord = getPos().getY();
        int zCoord = getPos().getZ();
        return new AxisAlignedBB(xCoord - 4, yCoord - 4, zCoord - 4, xCoord + 5, yCoord + 5, zCoord + 5);
    }

    @Override
    public boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        if (world.isRemote) {
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            useWrench(player);
        }
        return true;
    }

    // Called from client side when a wrench is used.
    private void useWrench(PlayerEntity player) {
        BlockPos coord = RFToolsBase.instance.clientInfo.getSelectedTE();
        TileEntity tileEntity = null;
        if (coord != null) {
            tileEntity = world.getTileEntity(coord);
        }

        if (!(tileEntity instanceof MatterBeamerTileEntity)) {
            // None selected. Just select this one.
            RFToolsBase.instance.clientInfo.setSelectedTE(getPos());
            SpawnerTileEntity destinationTE = getDestinationTE();
            if (destinationTE == null) {
                RFToolsBase.instance.clientInfo.setDestinationTE(null);
            } else {
                RFToolsBase.instance.clientInfo.setDestinationTE(destinationTE.getPos());
            }
            Logging.message(player, "Select a spawner as destination");
        } else if (coord.equals(getPos())) {
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
        markDirty();

        if (world.isRemote) {
            // We're on the client. Send change to server.
            valueToServer(RFToolsUtilityMessages.INSTANCE, VALUE_DESTINATION, destination);
        } else {
            markDirtyClient();
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
        TileEntity te = world.getTileEntity(destination);
        if (te instanceof SpawnerTileEntity) {
            return (SpawnerTileEntity) te;
        } else {
            destination = null;
            markDirtyClient();
            return null;
        }
    }


    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        destination = BlockPosTools.read(tagCompound, "dest");
        glowing = tagCompound.getBoolean("glowing");
    }


    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        BlockPosTools.write(tagCompound, "dest", destination);
        tagCompound.putBoolean("glowing", glowing);
        return tagCompound;
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return automationItemHandler.cast();
        }
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
