package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.TextScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.TextClientScreenModule;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

import net.minecraft.world.item.Item.Properties;

import javax.annotation.Nonnull;

public class TextModuleItem extends GenericModuleItem {

    public TextModuleItem() {
        super(new Properties().stacksTo(16).defaultDurability(1).tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.TEXT_RFPERTICK.get();
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        return NBTTools.getString(stack, "text", "<unset>");
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        list.add(new TextComponent(ChatFormatting.GREEN + "Uses " + ScreenConfiguration.TEXT_RFPERTICK.get() + " RF/tick"));
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            list.add(new TextComponent(ChatFormatting.YELLOW + "Text: " + tagCompound.getString("text")));
        }
    }

    @Override
    public Class<TextScreenModule> getServerScreenModule() {
        return TextScreenModule.class;
    }

    @Override
    public Class<TextClientScreenModule> getClientScreenModule() {
        return TextClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Text";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Text:").text("text", "Text to show").color("color", "Color for the text").nl()
                .toggle("large", "Large", "Large or small font")
                .choices("align", "Text alignment", "Left", "Center", "Right").nl();

    }
}