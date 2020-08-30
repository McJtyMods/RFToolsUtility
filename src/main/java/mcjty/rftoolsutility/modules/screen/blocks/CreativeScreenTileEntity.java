package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.rftoolsutility.modules.screen.ScreenModule;

public class CreativeScreenTileEntity extends ScreenTileEntity {

    public CreativeScreenTileEntity() {
        super(ScreenModule.TYPE_CREATIVE_SCREEN.get());
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
