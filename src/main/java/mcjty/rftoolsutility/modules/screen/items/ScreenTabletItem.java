package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.gui.ManualEntry;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsbase.tools.ManualHelper;

public class ScreenTabletItem extends TabletItem {

    public ScreenTabletItem(Properties properties) {
        super(properties);
    }


    @Override
    public ManualEntry getManualEntry() {
        return ManualHelper.create("rftoolsutility:machines/screen_link");
    }
}
