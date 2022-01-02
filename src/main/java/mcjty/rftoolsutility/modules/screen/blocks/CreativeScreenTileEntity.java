package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class CreativeScreenTileEntity extends ScreenTileEntity {

    public CreativeScreenTileEntity() {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get());
    }

    public CreativeScreenTileEntity(ResourceKey<Level> type) {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get(), type);
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
