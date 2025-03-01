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
import net.minecraft.world.level.Level;

import java.util.Objects;

public class RedstoneScreenModule implements IScreenModule<IModuleDataInteger> {
    private int channel = -1;
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);
    private Direction side = null;

    private boolean active = false;

    // Client side
    private String line = "";
    private String yestext = "on";
    private String notext = "off";
    private int color = 0xffffff;
    private int yescolor = 0xffffff;
    private int nocolor = 0xffffff;
    private boolean analog = false;
    private TextAlign align = TextAlign.ALIGN_LEFT;
    private String monitor = "";

    public static final Codec<RedstoneScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("channel").forGetter(module -> module.channel),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Direction.CODEC.fieldOf("side").forGetter(module -> module.side),
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
            Direction.STREAM_CODEC, module -> module.side,
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
        this.channel = channel;
        this.pos = pos;
        this.side = side;
        this.line = line;
        this.yestext = yestext;
        this.notext = notext;
        this.color = color;
        this.yescolor = yescolor;
        this.analog = analog;
        this.nocolor = nocolor;
        this.align = align;
        this.monitor = monitor;
    }

    public RedstoneScreenModule(int channel, GlobalPos pos, Direction side) {
        this.channel = channel;
        this.pos = pos;
        this.side = side;
    }

    public RedstoneScreenModule() {
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
        this.side = side;
    }

    public void setPos(GlobalPos pos) {
        this.pos = pos;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getYestext() {
        return yestext;
    }

    public void setYestext(String yestext) {
        this.yestext = yestext;
    }

    public String getNotext() {
        return notext;
    }

    public void setNotext(String notext) {
        this.notext = notext;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getYescolor() {
        return yescolor;
    }

    public void setYescolor(int yescolor) {
        this.yescolor = yescolor;
    }

    public int getNocolor() {
        return nocolor;
    }

    public void setNocolor(int nocolor) {
        this.nocolor = nocolor;
    }

    public boolean isAnalog() {
        return analog;
    }

    public void setAnalog(boolean analog) {
        this.analog = analog;
    }

    public TextAlign getAlign() {
        return align;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        if (channel == -1) {
            // If we are monitoring some block then we can use that.
            if (!BlockPosTools.INVALID.equals(pos.pos())) {
                Level world = LevelTools.getLevel(worldObj, pos.dimension());
                if (world != null) {
//                    int powerTo = world.isBlockProvidingPowerTo(coordinate.getX(), coordinate.getY(), coordinate.getZ(), side);
                    int powerTo = world.getSignal(pos.pos().relative(side), side.getOpposite());
//                    int powerTo = world.getIndirectPowerLevelTo(coordinate.getX(), coordinate.getY(), coordinate.getZ(), side);

                    return helper.createInteger(powerTo);
                }
            }
            return null;
        }
        RedstoneChannels channels = RedstoneChannels.getChannels(worldObj);
        if (channels == null) {
            return null;
        }
        RedstoneChannels.RedstoneChannel ch = channels.getChannel(channel);
        if (ch == null) {
            return null;
        }
        return helper.createInteger(ch.getValue());
    }

    @Override
    public void validate(Level world, BlockPos p, boolean isPlus) {
        if (isPlus) {
            active = true;
            return;
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
        active = false;
        if (LevelTools.isLoaded(world, pos.pos())) {
            if (Objects.equals(pos.dimension(), world.dimension())) {
                int dx = Math.abs(pos.pos().getX() - p.getX());
                int dy = Math.abs(pos.pos().getY() - p.getY());
                int dz = Math.abs(pos.pos().getZ() - p.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                    active = true;
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.REDSTONE_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
