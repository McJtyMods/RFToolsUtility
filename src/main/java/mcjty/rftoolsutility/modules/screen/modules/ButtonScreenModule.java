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

public class ButtonScreenModule implements IScreenModule<IModuleDataBoolean> {
    private String line = "";
    private int channel = -1;
    private boolean toggle = false;

    // Client side
    private String button = "";
    private int color = 0xffffff;
    private int buttonColor = 0xffffff;
    private TextAlign align = TextAlign.ALIGN_LEFT;

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

    public static final ButtonScreenModule DEFAULT = new ButtonScreenModule();

    public ButtonScreenModule(String line, int channel, boolean toggle, String button, int color, int buttonColor, String align) {
        this.line = line;
        this.channel = channel;
        this.toggle = toggle;
        this.button = button;
        this.color = color;
        this.buttonColor = buttonColor;
        this.align = TextAlign.get(align);
    }

    public ButtonScreenModule() {
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

    public void setLine(String line) {
        this.line = line;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
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
    public void validate(Level world, BlockPos pos, boolean isPlus) {
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
