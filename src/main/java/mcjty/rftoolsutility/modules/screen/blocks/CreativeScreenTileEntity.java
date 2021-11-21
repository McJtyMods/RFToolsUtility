package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public class CreativeScreenTileEntity extends ScreenTileEntity {

    public CreativeScreenTileEntity() {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get());
    }

    public CreativeScreenTileEntity(RegistryKey<World> type) {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get(), type);
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
