package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.api.screens.data.IModuleDataBoolean;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record ButtonScreenModule(String line, int channel, boolean toggle, String button, int color, int buttonColor, TextAlign align) implements IScreenModule<ButtonScreenModule, IModuleDataBoolean> {

    public static final ButtonScreenModule DEFAULT = new ButtonScreenModule("", -1, false, "", 0xffffff, 0xffffff, TextAlign.ALIGN_LEFT);

    public static final Codec<ButtonScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("channel").forGetter(module -> module.channel),
            Codec.BOOL.fieldOf("toggle").forGetter(module -> module.toggle),
            Codec.STRING.fieldOf("button").forGetter(module -> module.button),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.INT.fieldOf("buttonColor").forGetter(module -> module.buttonColor),
            Codec.STRING.fieldOf("align").forGetter(module -> module.align.name())
    ).apply(instance, ButtonScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ButtonScreenModule> STREAM_CODEC = CompositeStreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.channel,
            ByteBufCodecs.BOOL, module -> module.toggle,
            ByteBufCodecs.STRING_UTF8, module -> module.button,
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.INT, module -> module.buttonColor,
            ByteBufCodecs.STRING_UTF8, module -> module.align.name(),
            ButtonScreenModule::new);

    public ButtonScreenModule(String line, int channel, boolean toggle, String button, int color, int buttonColor, String align) {
        this(line, channel, toggle, button, color, buttonColor, TextAlign.get(align));
    }

    public int getChannel() {
        return channel;
    }

    public String getLine() {
        return line;
    }

    public boolean isToggle() {
        return toggle;
    }

    public String getButton() {
        return button;
    }

    public int getColor() {
        return color;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public TextAlign getAlign() {
        return align;
    }

    public ButtonScreenModule withLine(String line) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    public ButtonScreenModule withChannel(int channel) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    public ButtonScreenModule withToggle(boolean toggle) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    public ButtonScreenModule withButton(String button) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    public ButtonScreenModule withColor(int color) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    public ButtonScreenModule withButtonColor(int buttonColor) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    public ButtonScreenModule withAlign(TextAlign align) {
        return new ButtonScreenModule(line, channel, toggle, button, color, buttonColor, align);
    }

    @Override
    public IModuleDataBoolean getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (channel != -1 && toggle) {
            RedstoneChannels channels = RedstoneChannels.getChannels(worldObj);
            RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
            return helper.createBoolean(ch.getValue() > 0);
        }
        return null;
    }

    @Override
    public ButtonScreenModule validate(Level world, BlockPos pos, boolean isPlus) {
        return this;
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {
        int xoffset;
        if (!line.isEmpty()) {
            xoffset = 80;
        } else {
            xoffset = 5;
        }
        if (x >= xoffset) {
            if (channel != -1) {
                if (toggle) {
                    if (clicked) {
                        RedstoneChannels channels = RedstoneChannels.getChannels(world);
                        RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
                        ch.setValue((ch.getValue() == 0) ? 15 : 0);
                        channels.save();
                    }
                } else {
                    RedstoneChannels channels = RedstoneChannels.getChannels(world);
                    RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
                    ch.setValue(clicked ? 15 : 0);
                    channels.save();
                }
            } else {
                if (player != null) {
                    player.displayClientMessage(ComponentFactory.literal(ChatFormatting.RED + "Module is not linked to redstone channel!"), false);
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.BUTTON_RFPERTICK.get();
    }
}
