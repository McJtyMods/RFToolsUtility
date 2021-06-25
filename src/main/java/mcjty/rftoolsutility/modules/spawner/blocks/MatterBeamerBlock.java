package mcjty.rftoolsutility.modules.spawner.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MatterBeamerBlock extends BaseBlock {

    public MatterBeamerBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(MatterBeamerTileEntity::new)
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsutility:todo"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold()));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.LIT);
    }
}
