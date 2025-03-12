package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.FormatStyle;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public record CounterScreenModule(GlobalPos pos, boolean active, String line, int color, int cntcolor, FormatStyle format, TextAlign align, String monitor) implements IScreenModule<CounterScreenModule, IModuleDataInteger> {

    public static final CounterScreenModule DEFAULT = new CounterScreenModule(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), "", 0xffffff, 0xffffff, FormatStyle.MODE_FULL, TextAlign.ALIGN_LEFT, "");

    public static final Codec<CounterScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.INT.fieldOf("cntcolor").forGetter(module -> module.cntcolor),
            FormatStyle.CODEC.fieldOf("format").forGetter(module -> module.format),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align),
            Codec.STRING.fieldOf("monitor").forGetter(module -> module.monitor)
    ).apply(instance, CounterScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CounterScreenModule> STREAM_CODEC = CompositeStreamCodec.composite(
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.INT, module -> module.cntcolor,
            FormatStyle.STREAM_CODEC, module -> module.format,
            TextAlign.STREAM_CODEC, module -> module.align,
            ByteBufCodecs.STRING_UTF8, module -> module.monitor,
            CounterScreenModule::new);

    public CounterScreenModule(GlobalPos pos, String line, int color, int cntcolor, FormatStyle format, TextAlign align, String monitor) {
        this(pos, false, line, color, cntcolor, format, align, monitor);
    }

    public String getLine() {
        return line;
    }

    public int getColor() {
        return color;
    }

    public int getCntcolor() {
        return cntcolor;
    }

    public TextAlign getAlign() {
        return align;
    }

    public FormatStyle getFormat() {
        return format;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public String getMonitor() {
        return monitor;
    }

    public CounterScreenModule withLine(String line) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withColor(int color) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withCntcolor(int cntcolor) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withFormat(FormatStyle format) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withAlign(TextAlign align) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withMonitor(String monitor) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withPos(GlobalPos pos) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    public CounterScreenModule withActive(boolean active) {
        return new CounterScreenModule(pos, active, line, color, cntcolor, format, align, monitor);
    }

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        Level world = LevelTools.getLevel(worldObj, pos.dimension());
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, pos.pos())) {
            return null;
        }

        BlockEntity te = world.getBlockEntity(pos.pos());

        if (!(te instanceof CounterTileEntity counterTileEntity)) {
            return null;
        }
        return helper.createInteger(counterTileEntity.getCurrent());
    }

    @Override
    public CounterScreenModule validate(Level world, BlockPos p, boolean isPlus) {
        if (isPlus) {
            return withActive(true);
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
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
        return ScreenConfiguration.COUNTER_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
