package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.rftoolsutility.setup.Registration.standardBlockProperties;

public class WireTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    private int loopDetector = 0;

    public static LogicSlabBlock createBlock(BlockBehaviour.Properties properties) {
        return new LogicSlabBlock(new BlockBuilder().properties(standardBlockProperties(properties))
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(WireTileEntity::new));
    }

    public WireTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.WIRE.be().get(), pos, state);
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
                world.neighborChanged(offsetPos, state.getBlock(), null);
            }
            loopDetector--;
        }
    }
}
