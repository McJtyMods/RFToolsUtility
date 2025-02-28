package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ButtonScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ButtonClientScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

import java.util.function.Consumer;

public class ButtonModuleItem extends GenericModuleItem {

    @Override
    public Codec<? extends IScreenModule<?>> codec() {
        return ButtonScreenModule.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return ButtonScreenModule.STREAM_CODEC;
    }

    @Override
    public DataComponentType<? extends IScreenModule<?>> componentType() {
        return ScreenModule.MODULE_BUTTON_DATA.get();
    }

    @Override
    public IScreenModule<?> createServerScreenModule() {
        return new ButtonScreenModule();
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new ButtonClientScreenModule();
    }

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
        super(RFToolsUtility.setup.defaultProperties().durability(1));
    }

    public static int getChannel(ItemStack stack) {
        // @todo 1.21 data
        return 0;
//        return NBTTools.getInt(stack, "channel", -1);
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public String getModuleName() {
        return "Button";
    }

    private ButtonScreenModule cd(ItemStack stack) {
        ButtonScreenModule data = stack.get(ScreenModule.MODULE_BUTTON_DATA);
        if (data == null) {
            data = new ButtonScreenModule();
        }
        return data;
    }

    private void cd(ItemStack stack, Consumer<ButtonScreenModule> setter) {
        ButtonScreenModule data = cd(stack);
        setter.accept(data);
        stack.set(ScreenModule.MODULE_BUTTON_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> cd(stack, d -> d.setLine(s)), stack -> cd(stack).getLine(), "Label text")
                .color((stack, c) -> cd(stack, d -> d.setColor(c)), stack -> cd(stack).getColor(), "Label color")
                .nl()

                .label("Button:")
                .text((stack, s) -> cd(stack, d -> d.setButton(s)), stack -> cd(stack).getButton(), "Button text")
                .color((stack, c) -> cd(stack, d -> d.setButtonColor(c)), stack -> cd(stack).getButtonColor(), "Button color")
                .nl()

                .toggle((stack, b) -> cd(stack, d -> d.setToggle(b)), stack -> cd(stack).isToggle(), "Toggle", "Toggle button mode")
                .choices((stack, s) -> cd(stack, d -> d.setAlign(TextAlign.get(s))), stack -> cd(stack).getAlign().name(), "Label alignment", "Left", "Center", "Right")
                .nl();

    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }
}