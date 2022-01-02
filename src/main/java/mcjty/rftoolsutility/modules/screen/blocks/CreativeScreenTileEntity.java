package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeScreenTileEntity extends ScreenTileEntity {

    public CreativeScreenTileEntity(BlockPos pos, BlockState state) {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get(), pos, state);
    }

    public CreativeScreenTileEntity(ResourceKey<Level> type) {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get(), type);
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
