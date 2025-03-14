package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
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
import java.util.function.Consumer;
import java.util.function.Function;

public class TextModuleItem extends GenericModuleItem {

    public TextModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(16).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?, ?>> codec() {
        return TextScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?, ?>> streamCodec() {
        return TextScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?, ?>> componentType() {
        return ScreenModule.MODULE_TEXT_DATA.get();
    }

    @Override
    public IScreenModule<?, ?> createServerScreenModule() {
        return TextScreenModule.DEFAULT;
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
        TextScreenModule data = data(stack);
        return data.getLine();
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, list, flag);
        list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Uses " + ScreenConfiguration.TEXT_RFPERTICK.get() + " RF/tick"));
        TextScreenModule data = data(itemStack);
        list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Text: " + data.getLine()));
    }

    @Override
    public String getModuleName() {
        return "Text";
    }

    public static TextScreenModule data(ItemStack stack) {
        TextScreenModule data = stack.get(ScreenModule.MODULE_TEXT_DATA);
        if (data == null) {
            data = TextScreenModule.DEFAULT;
        }
        return data;
    }

    public static void data(ItemStack stack, Function<TextScreenModule, TextScreenModule> setter) {
        TextScreenModule data = data(stack);
        data = setter.apply(data);
        stack.set(ScreenModule.MODULE_TEXT_DATA, data);
    }


    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Text:")
                .text((stack, s) -> data(stack, d -> d.withLine(s)), stack -> data(stack).getLine(), "Text to show")
                .color((stack, c) -> data(stack, d -> d.withColor(c)), stack -> data(stack).getColor(), "Color for the text")
                .nl()

                .toggle((stack, b) -> data(stack, d -> d.withLarge(b)), stack -> data(stack).isLarge(), "Large", "Large or small font")
                .choices((stack, c) -> data(stack, d -> d.withAlign(TextAlign.get(c))), stack -> data(stack).getAlign().getSerializedName(), "Label alignment", "Left", "Center", "Right")
                .nl();

    }
}