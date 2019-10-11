package mcjty.rftoolsutility.modules.tank.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.rftoolsutility.modules.tank.TankSetup.TYPE_TANK;

public class TankTE extends GenericTileEntity {

    public static final int SLOT_FILTER = 0;

    public static final ModelProperty<Integer> AMOUNT = new ModelProperty<>();
    public static final ModelProperty<Fluid> FLUID = new ModelProperty<>();

    private int level = -1;

    public static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory(1) {
        @Override
        protected void setup() {
            slot(SlotDefinition.specific(s -> s.getItem() instanceof BucketItem),
                    CONTAINER_CONTAINER, SLOT_FILTER, 100, 10);
            playerSlots(10, 70);
        }
    };


    private LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(this::createItemHandler);
    private LazyOptional<FluidTank> fluidHandler = LazyOptional.of(this::createFluidHandler);
    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Tank")
        .containerSupplier((windowId,player) -> new GenericContainer(TankSetup.CONTAINER_TANK, windowId, CONTAINER_FACTORY, getPos(), TankTE.this))
        .itemHandler(itemHandler));

    public TankTE() {
        super(TYPE_TANK);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock("tank", new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .tileEntitySupplier(TankTE::new)) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        level = tagCompound.getInt("level");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("level", level);
        return super.write(tagCompound);
    }

    @Override
    protected void readCaps(CompoundNBT tagCompound) {
        super.readCaps(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        fluidHandler.ifPresent(h -> {
            h.readFromNBT(info.getCompound("tank"));
        });
    }

    @Override
    protected void writeCaps(CompoundNBT tagCompound) {
        super.writeCaps(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        fluidHandler.ifPresent(h -> {
            CompoundNBT nbt = new CompoundNBT();
            h.writeToNBT(nbt);
            info.put("tank", nbt);
        });
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(TankTE.this, CONTAINER_FACTORY) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof BucketItem;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof BucketItem;
            }
        };
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        int oldLevel = level;
        super.onDataPacket(net, packet);
        if (oldLevel != level) {
            ModelDataManager.requestModelDataRefresh(this);
            world.func_225319_b(getPos(), null, null);
        }
    }


    private void updateLevel(FluidTank tank) {
        markDirtyQuick();
        int newlevel = computeLevel(tank);
        if (level != newlevel) {
            level = newlevel;
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    private int computeLevel(FluidTank tank) {
        return (8 * tank.getFluidAmount()) / tank.getCapacity();
    }

    private FluidTank createFluidHandler() {
        // @todo capacity configurable
//        return new FluidTank(16000, fluidStack -> );
        return new FluidTank(16000) {
            @Override
            protected void onContentsChanged() {
                updateLevel(this);
            }
        };
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        return fluidHandler.map(h -> {
            ItemStack heldItem = player.getHeldItem(hand);
            FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(heldItem, h, null, Integer.MAX_VALUE, player, true);
            if (fillResult.isSuccess()) {
                return true;
            }
            fillResult = FluidUtil.tryEmptyContainerAndStow(heldItem, h, null, Integer.MAX_VALUE, player, true);
            if (fillResult.isSuccess()) {
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        FluidTank tank = fluidHandler.map(h -> h).orElseThrow(RuntimeException::new);
        return new ModelDataMap.Builder()
                .withInitial(AMOUNT, computeLevel(tank))
                .withInitial(FLUID, tank.getFluid().getFluid())
                .build();
    }



    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }


}
