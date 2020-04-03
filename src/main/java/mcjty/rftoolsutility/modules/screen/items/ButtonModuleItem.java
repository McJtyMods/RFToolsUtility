package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.ButtonScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ButtonClientScreenModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

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
        super(new Properties()
                .defaultMaxDamage(1)
                .group(RFToolsUtility.setup.getTab()));
    }

    private int getChannel(ItemStack stack) {
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
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }
}