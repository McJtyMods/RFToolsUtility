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

public record EnergyBarScreenModule(GlobalPos pos, Direction side, ScreenModuleHelper helper, boolean active, String line, int color, TextAlign align, ILevelRenderHelper rfRenderer, String monitor) implements IScreenModule<EnergyBarScreenModule, IModuleDataContents> {

    public static final EnergyBarScreenModule DEFAULT = new EnergyBarScreenModule(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), Direction.DOWN, new ScreenModuleHelper(), false, "", 0xffffff, TextAlign.ALIGN_LEFT, new ScreenLevelHelper().gradient(0xffff0000, 0xff333300), "");

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
        this(pos, side, new ScreenModuleHelper(), false, line, color, align, rfRenderer, monitor);
    }

    public String getLine() {
        return line;
    }

    public int getColor() {
        return color;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public String getMonitor() {
        return monitor;
    }

    public Direction getSide() {
        return side;
    }

    public TextAlign getAlign() {
        return align;
    }

    public int getPosColor() {
        return rfRenderer.getPosColor();
    }

    public int getNegColor() {
        return rfRenderer.getNegColor();
    }

    public boolean isHideBar() {
        return rfRenderer.isHideBar();
    }

    public FormatStyle getFormat() {
        return rfRenderer.getFormatStyle();
    }

    public BarMode getBarMode() {
        return rfRenderer.getBarMode();
    }

    public ILevelRenderHelper getRfRenderer() {
        return rfRenderer;
    }

    public EnergyBarScreenModule withLine(String line) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }
    
    public EnergyBarScreenModule withColor(int color) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }
    
    public EnergyBarScreenModule withAlign(TextAlign align) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }
    
    public EnergyBarScreenModule withPos(GlobalPos pos) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }
    
    public EnergyBarScreenModule withMonitor(String monitor) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }
    
    public EnergyBarScreenModule withActive(boolean active) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }
    
    public EnergyBarScreenModule withSide(Direction side) {
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }

    public EnergyBarScreenModule withPosColor(int posColor) {
        rfRenderer.setPosColor(posColor);
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }

    public EnergyBarScreenModule withNegColor(int negColor) {
        rfRenderer.setNegColor(negColor);
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }

    public EnergyBarScreenModule withHideBar(boolean hideBar) {
        rfRenderer.setHideBar(hideBar);
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }

    public EnergyBarScreenModule withFormat(FormatStyle format) {
        rfRenderer.setFormatStyle(format);
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
    }

    public EnergyBarScreenModule withBarMode(BarMode mode) {
        rfRenderer.setBarMode(mode);
        return new EnergyBarScreenModule(pos, side, helper, active, line, color, align, rfRenderer, monitor);
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
    public EnergyBarScreenModule validate(Level world, BlockPos p, boolean isPlus) {
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
        return ScreenConfiguration.ENERGY_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {
    }
}
