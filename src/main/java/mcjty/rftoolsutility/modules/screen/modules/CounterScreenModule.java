package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
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

public class CounterScreenModule implements IScreenModule<IModuleDataInteger> {
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);
    private boolean active = false;

    // Client data
    private String line = "";
    private int color = 0xffffff;
    private int cntcolor = 0xffffff;
    private FormatStyle format = FormatStyle.MODE_FULL;
    private TextAlign align = TextAlign.ALIGN_LEFT;

    public static final Codec<CounterScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.INT.fieldOf("cntcolor").forGetter(module -> module.cntcolor),
            FormatStyle.CODEC.fieldOf("format").forGetter(module -> module.format),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align)
    ).apply(instance, CounterScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CounterScreenModule> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.INT, module -> module.cntcolor,
            FormatStyle.STREAM_CODEC, module -> module.format,
            TextAlign.STREAM_CODEC, module -> module.align,
            CounterScreenModule::new);

    public CounterScreenModule(GlobalPos pos, String line, int color, int cntcolor, FormatStyle format, TextAlign align) {
        this.pos = pos;
        this.line = line;
        this.color = color;
        this.cntcolor = cntcolor;
        this.format = format;
    }

    public CounterScreenModule() {
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCntcolor() {
        return cntcolor;
    }

    public void setCntcolor(int cntcolor) {
        this.cntcolor = cntcolor;
    }

    public TextAlign getAlign() {
        return align;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    public FormatStyle getFormat() {
        return format;
    }

    public void setFormat(FormatStyle format) {
        this.format = format;
    }

    public GlobalPos getPos() {
        return pos;
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
        return ScreenConfiguration.COUNTER_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
