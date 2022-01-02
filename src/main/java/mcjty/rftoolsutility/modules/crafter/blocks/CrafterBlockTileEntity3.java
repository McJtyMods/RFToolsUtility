package mcjty.rftoolsutility.modules.crafter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static mcjty.rftoolsutility.modules.crafter.CrafterModule.TYPE_CRAFTER3;

public class CrafterBlockTileEntity3 extends CrafterBaseTE {

    public CrafterBlockTileEntity3(BlockPos pos, BlockState state) {
        super(TYPE_CRAFTER3.get(), pos, state, 8);
    }
}
