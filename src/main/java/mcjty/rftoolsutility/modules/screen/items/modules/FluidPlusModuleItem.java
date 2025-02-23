package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.world.item.ItemStack;

public class FluidPlusModuleItem extends FluidModuleItem {

    @Override
    public boolean isPlusModule() {
        return true;
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.FLUIDPLUS_RFPERTICK.get();
    }
}