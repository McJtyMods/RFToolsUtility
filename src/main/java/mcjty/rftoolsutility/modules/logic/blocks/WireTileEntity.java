package mcjty.rftoolsutility.modules.logic.blocks;


import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;


public class WireTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    private int loopDetector = 0;

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(WireTileEntity::new));
    }

    public WireTileEntity() {
        super(LogicBlockModule.TYPE_WIRE.get());
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        if (side == LogicSupport.getFacing(state).getInputSide()) {
            return powerLevel;
        } else {
            return 0;
        }
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
        if (loopDetector <= 0) {
            loopDetector++;
            BlockState state = world.getBlockState(pos);
            BlockPos offsetPos = pos.relative(LogicSupport.getFacing(state).getInputSide().getOpposite());
            if (world.hasChunkAt(offsetPos)) {
                world.neighborChanged(offsetPos, state.getBlock(), pos);
            }
            loopDetector--;
        }
    }
}
