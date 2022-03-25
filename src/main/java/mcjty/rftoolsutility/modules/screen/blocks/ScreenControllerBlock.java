package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public void onRemove(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ScreenControllerTileEntity) {
                ((ScreenControllerTileEntity) tileEntity).detach();
            }
        }
        super.onRemove(state, world, pos, newstate, isMoving);
    }
}
