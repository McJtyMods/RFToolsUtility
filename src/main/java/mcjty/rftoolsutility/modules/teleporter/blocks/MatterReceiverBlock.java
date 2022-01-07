package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MatterReceiverBlock extends BaseBlock {

    public MatterReceiverBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:machines/matter_receiver"))
                .infusable()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold(), parameter("info", MatterReceiverBlock::getName))
                .tileEntitySupplier(MatterReceiverTileEntity::new));
    }

    private static String getName(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getString, "tpName", "<unset>");
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Level world = context.getLevel();
        if (world.isClientSide) {
            return state;
        }
        TeleportDestinations destinations = TeleportDestinations.get(world);

        BlockPos pos = context.getClickedPos();
        GlobalPos gc = GlobalPos.of(world.dimension(), pos);

        destinations.getNewId(gc);
        destinations.addDestination(gc);
        destinations.save();

        return state;
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
        // We don't want what BaseBlock does.
        // This is called AFTER onBlockPlaced below. Here we need to fix the destination settings.
        // @todo 1.14 check
//        restoreBlockFromNBT(world, pos, stack);
        if (!world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof MatterReceiverTileEntity receiver) {
                receiver.getOrCalculateID();
                receiver.updateDestination();
            }
        }
        setOwner(world, pos, placer);
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        super.onRemove(state, world, pos, newstate, isMoving);
        if (world.isClientSide) {
            return;
        }
        TeleportDestinations destinations = TeleportDestinations.get(world);
        destinations.removeDestination(pos, world.dimension());
        destinations.save();
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }
}
