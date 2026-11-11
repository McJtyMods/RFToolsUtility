package mcjty.rftoolsutility.modules.screen.items.modules;



public class InventoryPlusModuleItem extends InventoryModuleItem {

    public InventoryPlusModuleItem(Properties properties) {
        super(properties);
    }


    @Override
    public boolean isPlusModule() {
        return true;
    }
}
