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
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.tank.TankConfiguration;
import mcjty.rftoolsutility.modules.tank.TankModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import java.util.function.Function;

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

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> stack.getItem() instanceof BucketItem)
            .onUpdate((slot, stack) -> updateFilterFluid(stack))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final static Function<TankTE, GenericItemHandler> ITEM_HANDLER = te -> te.items;

    private final CustomTank fluidHandler = createFluidHandler();
    @Cap(type = CapType.FLUIDS)
    private final static Function<TankTE, CustomTank> FLUID_HANDLER = te -> te.fluidHandler;

    @Cap(type = CapType.CONTAINER)
    private static final Function<TankTE, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Tank")
            .containerSupplier(container(TankModule.CONTAINER_TANK, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .setupSync(be);

    private Fluid filterFluid = null;       // Cached value from the bucket in itemHandler

    public TankTE(BlockPos pos, BlockState state) {
        super(TYPE_TANK.get(), pos, state);
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
        // @todo 1.21 data
        return "<empty>";
//        return NBTTools.getInfoNBT(stack, (info, s) -> {
//            FluidStack fluid = FluidStack.loadFluidStackFromNBT(info.getCompound(s));
//            if (fluid.isEmpty()) {
//                return "<empty>";
//            } else {
//                return fluid.getAmount() + "mb " + fluid.getDisplayName().getString() /* was getFormattedText() */;
//            }
//        }, "tank", "<empty");
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        amount = tag.getInt("level");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("level", amount);
        super.saveAdditional(tag, provider);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag info = tag.getCompound("Info");
        // @todo 1.21 data
//        fluidHandler.readFromNBT(info.getCompound("tank"));
        clientFluid = fluidHandler.getFluid().getFluid();
        amount = tag.getInt("level");
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        // @todo 1.21 data
//        CompoundTag info = getOrCreateInfo(tag);
        CompoundTag nbt = new CompoundTag();
//        fluidHandler.writeToNBT(nbt);
//        info.put("tank", nbt);
        tag.putInt("level", amount);
    }

    // @todo 1.21 data
//    @Override
//    protected void loadCaps(CompoundTag tagCompound) {
//        super.loadCaps(tagCompound);
//        CompoundTag info = tagCompound.getCompound("Info");
//        fluidHandler.readFromNBT(info.getCompound("tank"));
//        clientFluid = fluidHandler.getFluid().getFluid();
//        updateFilterFluid(items.getStackInSlot(SLOT_FILTER));
//    }

    private void updateFilterFluid(ItemStack stack) {
        filterFluid = FluidUtil.getFluidContained(stack).map(FluidStack::getFluid).orElse(null);
    }

    // @todo 1.21 data
//    @Override
//    protected void saveCaps(CompoundTag tagCompound) {
//        super.saveCaps(tagCompound);
//        CompoundTag info = getOrCreateInfo(tagCompound);
//        CompoundTag nbt = new CompoundTag();
//        fluidHandler.writeToNBT(nbt);
//        info.put("tank", nbt);
//    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide) {
            ItemStack heldItem = player.getItemInHand(hand);
            FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(heldItem, fluidHandler, null, Integer.MAX_VALUE, player, true);
            if (fillResult.isSuccess()) {
                player.setItemInHand(hand, fillResult.getResult());
                return InteractionResult.SUCCESS;
            }
            fillResult = FluidUtil.tryFillContainerAndStow(heldItem, fluidHandler, null, Integer.MAX_VALUE, player, true);
            if (fillResult.isSuccess()) {
                player.setItemInHand(hand, fillResult.getResult());
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        int oldLevel = computeLevel(fluidHandler);
        super.onDataPacket(net, packet, provider);
        amount = computeLevel(fluidHandler);
        if (oldLevel != amount || !fluidHandler.getFluid().getFluid().equals(clientFluid)) {
            clientFluid = fluidHandler.getFluid().getFluid();
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }


    private void updateLevel(CustomTank tank) {
        markDirtyQuick();
        int newlevel = computeLevel(tank);
        if (amount != newlevel || !tank.getFluid().getFluid().equals(clientFluid)) {
            amount = newlevel;
            clientFluid = tank.getFluid().getFluid();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
    public ModelData getModelData() {
        return ModelData.builder()
                .with(AMOUNT, amount)
                .with(FLUID, clientFluid)
                .build();
    }
}
