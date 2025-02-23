package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.TextScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.TextClientScreenModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class TextModuleItem extends GenericModuleItem {

    public TextModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(16).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?>> codec() {
        return TextScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return TextScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?>> componentType() {
        return ScreenModule.MODULE_TEXT_DATA.get();
    }

    @Override
    public IScreenModule<?> createServerScreenModule() {
        return new TextScreenModule();
    }

    @Override
    public @Nullable Codec<? extends IClientScreenModule<?>> clientCodec() {
        return TextClientScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IClientScreenModule<?>> clientStreamCodec() {
        return TextClientScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IClientScreenModule<?>> clientComponentType() {
        return ScreenModule.CLIENTMODULE_TEXT_DATA.get();
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new TextClientScreenModule();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.TEXT_RFPERTICK.get();
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        // @todo 1.21 data
        return "<unset>";
//        return NBTTools.getString(stack, "text", "<unset>");
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, list, flag);
        list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Uses " + ScreenConfiguration.TEXT_RFPERTICK.get() + " RF/tick"));
        // @todo 1.21 data
//        CompoundTag tagCompound = itemStack.getTag();
//        if (tagCompound != null) {
//            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Text: " + tagCompound.getString("text")));
//        }
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