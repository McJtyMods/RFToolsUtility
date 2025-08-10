package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

public record RedstoneScreenModule(int channel, GlobalPos pos, Direction side, boolean active, String line, String yestext, String notext, int color, int yescolor, int nocolor, boolean analog, TextAlign align, String monitor) implements IScreenModule<RedstoneScreenModule, IModuleDataInteger> {

    public static final RedstoneScreenModule DEFAULT = new RedstoneScreenModule(-1, GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), null, false, "", "on", "off", 0xffffff, 0xffffff, 0xffffff, false, TextAlign.ALIGN_LEFT, "");

    public static final Codec<RedstoneScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("channel").forGetter(module -> module.channel),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Direction.CODEC.optionalFieldOf("side").forGetter(module -> Optional.ofNullable(module.side)),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.STRING.fieldOf("yestext").forGetter(module -> module.yestext),
            Codec.STRING.fieldOf("notext").forGetter(module -> module.notext),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.INT.fieldOf("yescolor").forGetter(module -> module.yescolor),
            Codec.INT.fieldOf("nocolor").forGetter(module -> module.nocolor),
            Codec.BOOL.fieldOf("analog").forGetter(module -> module.analog),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align),
            Codec.STRING.fieldOf("monitor").forGetter(module -> module.monitor)
    ).apply(instance, RedstoneScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneScreenModule> STREAM_CODEC = CompositeStreamCodec.composite(
            ByteBufCodecs.INT, module -> module.channel,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.optional(Direction.STREAM_CODEC), module -> Optional.ofNullable(module.side),
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.STRING_UTF8, module -> module.yestext,
            ByteBufCodecs.STRING_UTF8, module -> module.notext,
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.INT, module -> module.yescolor,
            ByteBufCodecs.INT, module -> module.nocolor,
            ByteBufCodecs.BOOL, module -> module.analog,
            TextAlign.STREAM_CODEC, module -> module.align,
            ByteBufCodecs.STRING_UTF8, module -> module.monitor,
            RedstoneScreenModule::new);

    public RedstoneScreenModule(int channel, GlobalPos pos, Direction side, String line, String yestext, String notext, int color, int yescolor, int nocolor, boolean analog, TextAlign align, String monitor) {
        this(channel, pos, side, false, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule(int channel, GlobalPos pos, Optional<Direction> side, String line, String yestext, String notext, int color, int yescolor, int nocolor, boolean analog, TextAlign align, String monitor) {
        this(channel, pos, side.orElse(null), false, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule(int channel, GlobalPos pos, Direction side) {
        this(channel, pos, side, false, "", "on", "off", 0xffffff, 0xffffff, 0xffffff, false, TextAlign.ALIGN_LEFT, "");
    }

    public int getChannel() {
        return channel;
    }

    public Direction getSide() {
        return side;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public String getMonitor() {
        return monitor;
    }

    public String getLine() {
        return line;
    }

    public String getYestext() {
        return yestext;
    }

    public String getNotext() {
        return notext;
    }

    public int getColor() {
        return color;
    }

    public int getYescolor() {
        return yescolor;
    }

    public int getNocolor() {
        return nocolor;
    }

    public boolean isAnalog() {
        return analog;
    }

    public TextAlign getAlign() {
        return align;
    }

    public RedstoneScreenModule withChannel(int channel) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withPos(GlobalPos pos) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withSide(Direction side) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withActive(boolean active) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withLine(String line) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withYestext(String yestext) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withNotext(String notext) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withColor(int color) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withYescolor(int yescolor) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withNocolor(int nocolor) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withAnalog(boolean analog) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withAlign(TextAlign align) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    public RedstoneScreenModule withMonitor(String monitor) {
        return new RedstoneScreenModule(channel, pos, side, active, line, yestext, notext, color, yescolor, nocolor, analog, align, monitor);
    }

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        if (channel == -1) {
            // If we are monitoring some block then we can use that.
            if (BlockPosTools.isValid(pos.pos())) {
                Level world = LevelTools.getLevel(worldObj, pos.dimension());
                if (world != null) {
                    int powerTo = world.getSignal(pos.pos().relative(side), side.getOpposite());

                    return helper.createInteger(powerTo);
                }
            }
            return null;
        }
        RedstoneChannels channels = RedstoneChannels.getChannels(worldObj);
        if (channels == null) {
            return null;
        }
        RedstoneChannels.RedstoneChannel ch = channels.getOrCreateChannel(channel);
        if (ch == null) {
            return null;
        }
        return helper.createInteger(ch.getValue());
    }

    @Override
    public RedstoneScreenModule validate(Level world, BlockPos p, boolean isPlus) {
        if (isPlus) {
            return withActive(true);
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
        if (channel != -1) {
            // We are monitoring a channel so we are always active
            return withActive(true);
        }
        if (LevelTools.isLoaded(world, pos.pos())) {
            if (Objects.equals(pos.dimension(), world.dimension())) {
                int dx = Math.abs(pos.pos().getX() - p.getX());
                int dy = Math.abs(pos.pos().getY() - p.getY());
                int dz = Math.abs(pos.pos().getZ() - p.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                    return withActive(true);
                }
            }
        }
        return withActive(false);
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.REDSTONE_RFPERTICK.get();
    }

    @Override
    public ItemStack mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked, Player player) {
        return ItemStack.EMPTY;
    }
}
