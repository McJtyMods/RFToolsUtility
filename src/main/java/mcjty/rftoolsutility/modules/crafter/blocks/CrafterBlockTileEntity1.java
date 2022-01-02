package mcjty.rftoolsutility.modules.crafter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static mcjty.rftoolsutility.modules.crafter.CrafterModule.TYPE_CRAFTER1;

public class CrafterBlockTileEntity1 extends CrafterBaseTE {

    public CrafterBlockTileEntity1(BlockPos pos, BlockState state) {
        super(TYPE_CRAFTER1.get(), pos, state, 2);
    }
}
