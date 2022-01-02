package mcjty.rftoolsutility.modules.tank.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.CustomTank;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.tank.TankConfiguration;
import mcjty.rftoolsutility.modules.tank.TankModule;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolsutility.modules.tank.TankModule.TYPE_TANK;

public class TankTE extends GenericTileEntity {

    public static final int SLOT_FILTER = 0;

    public static final ModelProperty<Integer> AMOUNT = new ModelProperty<>();
    public static final ModelProperty<Fluid> FLUID = new ModelProperty<>();

    private int amount = -1;
    // Client side only: the fluid for rendering
    private Fluid clientFluid = null;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(s -> s.getItem() instanceof BucketItem).in().out(), SLOT_FILTER, 151, 10)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> stack.getItem() instanceof BucketItem)
            .onUpdate((slot, stack) -> updateFilterFluid(stack))
            .build();

    @Cap(type = CapType.FLUIDS)
    private final LazyOptional<CustomTank> fluidHandler = LazyOptional.of(this::createFluidHandler);

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Tank")
            .containerSupplier(container(TankModule.CONTAINER_TANK, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .setupSync(this));

    private Fluid filterFluid = null;       // Cached value from the bucket in itemHandler

    public TankTE() {
        super(TYPE_TANK.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .tileEntitySupplier(TankTE::new)
                .manualEntry(ManualHelper.create("rftoolsutility:machines/tank"))
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
                return fluid.getAmount() + "mb " + fluid.getDisplayName().getString() /* was getFormattedText() */;
            }
        }, "tank", "<empty");
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        amount = tagCompound.getInt("level");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        tagCompound.putInt("level", amount);
        super.saveAdditional(tagCompound);
    }

    @Override
    protected void loadCaps(CompoundTag tagCompound) {
        super.loadCaps(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
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
    protected void saveCaps(CompoundTag tagCompound) {
        super.saveCaps(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        fluidHandler.ifPresent(h -> {
            CompoundTag nbt = new CompoundTag();
            h.writeToNBT(nbt);
            info.put("tank", nbt);
        });
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide) {
            return fluidHandler.map(h -> {
                ItemStack heldItem = player.getItemInHand(hand);
                FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(heldItem, h, null, Integer.MAX_VALUE, player, true);
                if (fillResult.isSuccess()) {
                    player.setItemInHand(hand, fillResult.getResult());
                    return InteractionResult.SUCCESS;
                }
                fillResult = FluidUtil.tryFillContainerAndStow(heldItem, h, null, Integer.MAX_VALUE, player, true);
                if (fillResult.isSuccess()) {
                    player.setItemInHand(hand, fillResult.getResult());
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        fluidHandler.ifPresent(tank -> {
            int oldLevel = computeLevel(tank);
            super.onDataPacket(net, packet);
            amount = computeLevel(tank);
            if (oldLevel != amount || !tank.getFluid().getFluid().equals(clientFluid)) {
                clientFluid = tank.getFluid().getFluid();
                ModelDataManager.requestModelDataRefresh(this);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        });
    }


    private void updateLevel(CustomTank tank) {
        markDirtyQuick();
        int newlevel = computeLevel(tank);
        if (amount != newlevel || !tank.getFluid().getFluid().equals(clientFluid)) {
            amount = newlevel;
            clientFluid = tank.getFluid().getFluid();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    private int computeLevel(CustomTank tank) {
        int amount = tank.getFluidAmount();
        if (amount <= 0) {
            return 0;
        }

        int total = (8 * amount) / tank.getCapacity() + 1;
        if (total > 8) {
            total = 8;
        }
        return total;
    }

    @Nonnull
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
                .withInitial(AMOUNT, amount)
                .withInitial(FLUID, clientFluid)
                .build();
    }
}
