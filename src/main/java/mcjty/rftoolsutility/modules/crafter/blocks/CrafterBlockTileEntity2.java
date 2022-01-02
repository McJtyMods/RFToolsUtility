package mcjty.rftoolsutility.modules.crafter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static mcjty.rftoolsutility.modules.crafter.CrafterModule.TYPE_CRAFTER2;

public class CrafterBlockTileEntity2 extends CrafterBaseTE {

    public CrafterBlockTileEntity2(BlockPos pos, BlockState state) {
        super(TYPE_CRAFTER2.get(), pos, state, 4);
    }
}
