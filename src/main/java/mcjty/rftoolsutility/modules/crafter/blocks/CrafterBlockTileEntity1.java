package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockTileEntity1 extends CrafterBaseTE {

    public CrafterBlockTileEntity1(BlockPos pos, BlockState state) {
        super(CrafterModule.CRAFTER1.be().get(), pos, state, 2);
    }
}
