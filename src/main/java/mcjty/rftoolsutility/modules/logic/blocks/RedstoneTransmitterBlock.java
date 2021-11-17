package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class RedstoneTransmitterBlock extends RedstoneChannelBlock {

    public RedstoneTransmitterBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/redstone_transmitter"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("channel", RedstoneChannelBlock::getChannelString))
                .tileEntitySupplier(RedstoneTransmitterTileEntity::new));
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        RedstoneTransmitterTileEntity te = (RedstoneTransmitterTileEntity) world.getBlockEntity(pos);
        te.update();
    }

    @Override
    public void setPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide) {
            // @todo double check
            ((RedstoneTransmitterTileEntity)world.getBlockEntity(pos)).update();
        }
    }
}
