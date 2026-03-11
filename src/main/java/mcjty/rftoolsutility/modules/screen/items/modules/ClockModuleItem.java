package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ClockScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ClockClientScreenModule;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class ClockModuleItem extends GenericModuleItem {

    public ClockModuleItem() {
        super(RFToolsUtility.setup.defaultProperties()
                .stacksTo(16)
                .durability(1));
    }

    @Override
    public Codec<? extends IScreenModule<?, ?>> codec() {
        return ClockScreenModule.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?, ?>> streamCodec() {
        return ClockScreenModule.STREAM_CODEC;
    }

    @Override
    public DataComponentType<? extends IScreenModule<?, ?>> componentType() {
        return ScreenModule.MODULE_CLOCK_DATA.get();
    }

    @Override
    public IScreenModule<?, ?> createServerScreenModule() {
        return ClockScreenModule.DEFAULT;
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new ClockClientScreenModule();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.CLOCK_RFPERTICK.get();
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public String getModuleName() {
        return "Clock";
    }

    public static ClockScreenModule data(ItemStack stack) {
        ClockScreenModule data = stack.get(ScreenModule.MODULE_CLOCK_DATA);
        if (data == null) {
            data = ClockScreenModule.DEFAULT;
        }
        return data;
    }

    public static void data(ItemStack stack, Function<ClockScreenModule, ClockScreenModule> setter) {
        ClockScreenModule data = data(stack);
        data = setter.apply(data);
        stack.set(ScreenModule.MODULE_CLOCK_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> data(stack, d -> d.withLine(s)), stack -> data(stack).getLine(), "Label text")
                .color((stack, c) -> data(stack, d -> d.withColor(c)), stack -> data(stack).getColor(), "Label color")
                .nl().
                toggle((stack, b) -> data(stack, d -> d.withLarge(b)), stack -> data(stack).isLarge(), "Large", "Large or small font")
                .nl();
    }
}
