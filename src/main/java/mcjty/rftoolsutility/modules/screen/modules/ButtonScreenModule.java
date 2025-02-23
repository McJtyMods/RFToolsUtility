package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
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

    public static final Codec<ButtonScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("channel").forGetter(module -> module.channel),
            Codec.BOOL.fieldOf("toggle").forGetter(module -> module.toggle)
    ).apply(instance, ButtonScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ButtonScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.channel,
            ByteBufCodecs.BOOL, module -> module.toggle,
            ButtonScreenModule::new);

    public ButtonScreenModule(String line, int channel, boolean toggle) {
        this.line = line;
        this.channel = channel;
        this.toggle = toggle;
    }

    public ButtonScreenModule() {
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
