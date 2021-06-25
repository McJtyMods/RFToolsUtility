package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.ComputerScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ComputerClientScreenModule;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class ComputerModuleItem extends GenericModuleItem {

    public ComputerModuleItem() {
        super(new Properties().stacksTo(16).defaultDurability(1).tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.COMPUTER_RFPERTICK.get();
    }

    //    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }
//
    @Override
    public Class<ComputerScreenModule> getServerScreenModule() {
        return ComputerScreenModule.class;
    }

    @Override
    public Class<ComputerClientScreenModule> getClientScreenModule() {
        return ComputerClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Comp";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .leftLabel("Contents of this module is").nl()
                .leftLabel("controlled with a computer.").nl()
                .leftLabel("Only works with OpenComputers.").nl() // "Only works with OC or CC."
                .label("Tag:").text("moduleTag", "Tag used by LUA to identify module").nl();
    }
}
