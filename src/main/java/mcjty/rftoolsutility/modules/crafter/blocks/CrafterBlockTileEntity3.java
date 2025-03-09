package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockTileEntity3 extends CrafterBaseTE {

    public CrafterBlockTileEntity3(BlockPos pos, BlockState state) {
        super(CrafterModule.CRAFTER3.be().get(), pos, state, 8);
    }
}
