package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.ButtonScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ButtonClientScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

public class ButtonModuleItem extends GenericModuleItem {

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.BUTTON_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return getChannel(stack) == -1;
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        int channel = getChannel(stack);
        if (channel != -1) {
            return Integer.toString(channel);
        }
        return "<unset>";
    }

    public ButtonModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().defaultDurability(1));
    }

    public static int getChannel(ItemStack stack) {
        return NBTTools.getInt(stack, "channel", -1);
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public Class<ButtonScreenModule> getServerScreenModule() {
        return ButtonScreenModule.class;
    }

    @Override
    public Class<ButtonClientScreenModule> getClientScreenModule() {
        return ButtonClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Button";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:").text("text", "Label text").color("color", "Label color").nl()
                .label("Button:").text("button", "Button text").color("buttonColor", "Button color").nl()
                .toggle("toggle", "Toggle", "Toggle button mode")
                .choices("align", "Label alignment", "Left", "Center", "Right").nl();

    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }
}