package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenLevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class EnergyBarScreenModule implements IScreenModule<IModuleDataContents> {
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);
    private Direction side = Direction.DOWN;
    private ScreenModuleHelper helper = new ScreenModuleHelper();
    private boolean active = false;

    // Client side
    private String line = "";
    private int color = 0xffffff;
    private TextAlign align = TextAlign.ALIGN_LEFT;
    private ILevelRenderHelper rfRenderer = new ScreenLevelHelper().gradient(0xffff0000, 0xff333300);
    private String monitor = "";

    public static final Codec<EnergyBarScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Direction.CODEC.fieldOf("side").forGetter(module -> module.side),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align),
            ScreenLevelHelper.CODEC.fieldOf("rfRenderer").forGetter(module -> (ScreenLevelHelper) module.rfRenderer),
            Codec.STRING.fieldOf("monitor").forGetter(module -> module.monitor)
    ).apply(instance, EnergyBarScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyBarScreenModule> STREAM_CODEC = CompositeStreamCodec.composite(
            GlobalPos.STREAM_CODEC, module -> module.pos,
            Direction.STREAM_CODEC, module -> module.side,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            TextAlign.STREAM_CODEC, module -> module.align,
            ScreenLevelHelper.STREAM_CODEC, module -> (ScreenLevelHelper) module.rfRenderer,
            ByteBufCodecs.STRING_UTF8, module -> module.monitor,
            EnergyBarScreenModule::new);

    public EnergyBarScreenModule(GlobalPos pos, Direction side, String line, int color, TextAlign align, ILevelRenderHelper rfRenderer, String monitor) {
        this.pos = pos;
        this.side = side;
        this.line = line;
        this.color = color;
        this.align = align;
        this.rfRenderer = rfRenderer;
        this.monitor = monitor;
    }

    public EnergyBarScreenModule() {
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

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
        this.side = side;
    }

    public TextAlign getAlign() {
        return align;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    public int getPosColor() {
        return rfRenderer.getPosColor();
    }

    public void setPosColor(int poscolor) {
        rfRenderer.setPosColor(poscolor);
    }

    public int getNegColor() {
        return rfRenderer.getNegColor();
    }

    public void setNegColor(int negcolor) {
        rfRenderer.setNegColor(negcolor);
    }

    public boolean isHideBar() {
        return rfRenderer.isHideBar();
    }

    public void setHideBar(boolean hidebar) {
        rfRenderer.setHideBar(hidebar);
    }

    public FormatStyle getFormat() {
        return rfRenderer.getFormatStyle();
    }

    public void setFormat(FormatStyle format) {
        rfRenderer.setFormatStyle(format);
    }

    public ILevelRenderHelper getRfRenderer() {
        return rfRenderer;
    }

    @Override
    public IModuleDataContents getData(IScreenDataHelper h, Level worldObj, long millis) {
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
        if (!EnergyTools.isEnergyTE(te, side)) {
            return null;
        }
        EnergyTools.EnergyLevel energyLevel = EnergyTools.getEnergyLevelMulti(te, side);
        long energy = energyLevel.energy();
        long maxEnergy = energyLevel.maxEnergy();
        return helper.getContentsValue(millis, energy, maxEnergy);
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
        return ScreenConfiguration.ENERGY_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {
    }
}
