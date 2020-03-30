package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.rftoolsutility.modules.screen.ScreenSetup;

public class CreativeScreenTileEntity extends ScreenTileEntity {

    public CreativeScreenTileEntity() {
        super(ScreenSetup.TYPE_CREATIVE_SCREEN.get());
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
