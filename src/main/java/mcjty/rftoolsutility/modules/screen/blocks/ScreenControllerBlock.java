package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

import static mcjty.lib.builder.TooltipBuilder.*;

public class ScreenControllerBlock extends BaseBlock {

    public ScreenControllerBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:machines/screen_controller"))
                .infusable()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(ScreenControllerTileEntity::new));
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ScreenControllerTileEntity controller) {
                controller.detach();
            }
        }
        super.onRemove(state, world, pos, newstate, isMoving);
    }
}
