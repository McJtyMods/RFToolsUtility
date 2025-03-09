package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockTileEntity2 extends CrafterBaseTE {

    public CrafterBlockTileEntity2(BlockPos pos, BlockState state) {
        super(CrafterModule.CRAFTER2.be().get(), pos, state, 4);
    }
}
