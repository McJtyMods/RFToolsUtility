package mcjty.rftoolsutility.modules.tank.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.*;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.CustomTank;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.tank.TankConfiguration;
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
import net.minecraft.util.ActionResultType;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolsutility.modules.tank.TankSetup.TYPE_TANK;

public class TankTE extends GenericTileEntity {

    public static final int SLOT_FILTER = 0;

    public static final ModelProperty<Integer> AMOUNT = new ModelProperty<>();
    public static final ModelProperty<Fluid> FLUID = new ModelProperty<>();

    private int level = -1;
    // Client side only: the fluid for rendering
    private Fluid clientFluid = null;

    public static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory(1)
            .slot(specific(s -> s.getItem() instanceof BucketItem), CONTAINER_CONTAINER, SLOT_FILTER, 151, 10)
            .playerSlots(10, 70);


    private NoDirectionItemHander items = createItemHandler();
    private LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private LazyOptional<CustomTank> fluidHandler = LazyOptional.of(this::createFluidHandler);
    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Tank")
        .containerSupplier((windowId,player) -> new GenericContainer(TankSetup.CONTAINER_TANK.get(), windowId, CONTAINER_FACTORY, getPos(), TankTE.this))
        .itemHandler(itemHandler));

    private Fluid filterFluid = null;       // Cached value from the bucket in itemHandler

    public TankTE() {
        super(TYPE_TANK.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .tileEntitySupplier(TankTE::new)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), parameter("contents", stack -> getFluidString(stack) + " (" + Integer.toString(TankConfiguration.MAXCAPACITY.get()) + " mb)"))
        ) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    private static String getFluidString(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, (info, s) -> {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(info.getCompound(s));
            if (fluid.isEmpty()) {
                return "<empty>";
            } else {
                return fluid.getAmount() + "mb " + fluid.getDisplayName().getFormattedText();
            }
        }, "tank", "<empty");
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
            clientFluid = h.getFluid().getFluid();
        });
        updateFilterFluid(items.getStackInSlot(SLOT_FILTER));
    }

    private void updateFilterFluid(ItemStack stack) {
        filterFluid = FluidUtil.getFluidContained(stack).map(FluidStack::getFluid).orElse(null);
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

            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                updateFilterFluid(getStackInSlot(SLOT_FILTER));
            }
        };
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (!world.isRemote) {
            return fluidHandler.map(h -> {
                ItemStack heldItem = player.getHeldItem(hand);
                FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(heldItem, h, null, Integer.MAX_VALUE, player, true);
                if (fillResult.isSuccess()) {
                    player.setHeldItem(hand, fillResult.getResult());
                    return ActionResultType.SUCCESS;
                }
                fillResult = FluidUtil.tryFillContainerAndStow(heldItem, h, null, Integer.MAX_VALUE, player, true);
                if (fillResult.isSuccess()) {
                    player.setHeldItem(hand, fillResult.getResult());
                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.PASS;
            }).orElse(ActionResultType.PASS);
        }
        return ActionResultType.PASS;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        fluidHandler.ifPresent(tank -> {
            int oldLevel = computeLevel(tank);
            super.onDataPacket(net, packet);
            level = computeLevel(tank);
            if (oldLevel != level || !tank.getFluid().getFluid().equals(clientFluid)) {
                clientFluid = tank.getFluid().getFluid();
                ModelDataManager.requestModelDataRefresh(this);
                world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        });
    }


    private void updateLevel(CustomTank tank) {
        markDirtyQuick();
        int newlevel = computeLevel(tank);
        if (level != newlevel || !tank.getFluid().getFluid().equals(clientFluid)) {
            level = newlevel;
            clientFluid = tank.getFluid().getFluid();
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    private int computeLevel(CustomTank tank) {
        return (8 * tank.getFluidAmount()) / tank.getCapacity();
    }

    private CustomTank createFluidHandler() {
        return new CustomTank(TankConfiguration.MAXCAPACITY.get()) {
            @Override
            protected void onContentsChanged() {
                updateLevel(this);
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                if (filterFluid == null) {
                    return true;
                } else {
                    return filterFluid == stack.getFluid();
                }
            }
        };
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder()
                .withInitial(AMOUNT, level)
                .withInitial(FLUID, clientFluid)
                .build();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return automationItemHandler.cast();
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
