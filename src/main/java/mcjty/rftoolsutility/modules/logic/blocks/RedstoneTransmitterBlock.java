package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.rftoolsutility.setup.Registration.standardBlockProperties;

public class RedstoneTransmitterBlock extends RedstoneChannelBlock {

    public RedstoneTransmitterBlock(BlockBehaviour.Properties properties) {
        super(properties, new BlockBuilder().properties(standardBlockProperties(properties))
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:logic/redstone_transmitter"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("channel", RedstoneChannelBlock::getChannelString))
                .tileEntitySupplier(RedstoneTransmitterTileEntity::new));
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Block blockIn, Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, orientation, isMoving);
        RedstoneTransmitterTileEntity te = (RedstoneTransmitterTileEntity) world.getBlockEntity(pos);
        te.update();
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide()) {
            // @todo double check
            ((RedstoneTransmitterTileEntity)world.getBlockEntity(pos)).update();
        }
    }
}
