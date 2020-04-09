package mcjty.rftoolsutility.modules.logic.blocks;


import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;


public class WireTileEntity extends LogicTileEntity {

    private int loopDetector = 0;

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(WireTileEntity::new));
    }

    public WireTileEntity() {
        super(LogicBlockSetup.TYPE_WIRE.get());
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == getFacing(state).getInputSide()) {
            return powerLevel;
        } else {
            return 0;
        }
    }

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        super.checkRedstone(world, pos);
        if (loopDetector <= 0) {
            loopDetector++;
            BlockState state = world.getBlockState(pos);
            BlockPos offsetPos = pos.offset(getFacing(state).getInputSide().getOpposite());
            if (world.isBlockLoaded(offsetPos)) {
                world.neighborChanged(offsetPos, state.getBlock(), pos);
            }
            loopDetector--;
        }
    }
}
