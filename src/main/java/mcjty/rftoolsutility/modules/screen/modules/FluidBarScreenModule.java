package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenLevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public record FluidBarScreenModule(GlobalPos pos, ScreenModuleHelper helper, boolean active, String line, int color, TextAlign align, ILevelRenderHelper mbRenderer, String monitor) implements IScreenModule<FluidBarScreenModule, IModuleDataContents> {

    public static final FluidBarScreenModule DEFAULT = new FluidBarScreenModule(GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO), "", 0xffffff, TextAlign.ALIGN_LEFT, new ScreenLevelHelper(), "");

    public static final Codec<FluidBarScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align),
            ScreenLevelHelper.CODEC.fieldOf("mbRenderer").forGetter(module -> (ScreenLevelHelper) module.mbRenderer),
            Codec.STRING.fieldOf("monitor").forGetter(module -> module.monitor)
    ).apply(instance, FluidBarScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidBarScreenModule> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            TextAlign.STREAM_CODEC, module -> module.align,
            ScreenLevelHelper.STREAM_CODEC, module -> (ScreenLevelHelper) module.mbRenderer,
            ByteBufCodecs.STRING_UTF8, module -> module.monitor,
            FluidBarScreenModule::new);

    public FluidBarScreenModule(GlobalPos pos, String line, int color, TextAlign align, ILevelRenderHelper mbRenderer, String monitor) {
        this(pos, new ScreenModuleHelper(), false, line, color, align, mbRenderer, monitor);
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

    public TextAlign getAlign() {
        return align;
    }

    public int getPosColor() {
        return mbRenderer.getPosColor();
    }

    public int getNegColor() {
        return mbRenderer.getNegColor();
    }

    public boolean isHideBar() {
        return mbRenderer.isHideBar();
    }

    public FormatStyle getFormat() {
        return mbRenderer.getFormatStyle();
    }

    public BarMode getBarMode() {
        return mbRenderer.getBarMode();
    }

    public ILevelRenderHelper getMbRenderer() {
        return mbRenderer;
    }

    public FluidBarScreenModule withLine(String line) {
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withColor(int color) {
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withAlign(TextAlign align) {
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withPos(GlobalPos pos) {
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withMonitor(String monitor) {
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withActive(boolean active) {
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withPosColor(int posColor) {
        mbRenderer.setPosColor(posColor);
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withNegColor(int negColor) {
        mbRenderer.setNegColor(negColor);
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withHideBar(boolean hideBar) {
        mbRenderer.setHideBar(hideBar);
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withFormat(FormatStyle format) {
        mbRenderer.setFormatStyle(format);
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
    }

    public FluidBarScreenModule withBarMode(BarMode mode) {
        mbRenderer.setBarMode(mode);
        return new FluidBarScreenModule(pos, helper, active, line, color, align, mbRenderer, monitor);
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

        AtomicInteger contents = new AtomicInteger();
        AtomicInteger maxContents = new AtomicInteger();

        BlockEntity te = world.getBlockEntity(pos.pos());
        IFluidHandler hf = CapabilityTools.getFluidCapabilitySafe(te);
        if (hf != null) {
            if (hf.getTanks() > 0) {
                if (!hf.getFluidInTank(0).isEmpty()) {
                    contents.set(hf.getFluidInTank(0).getAmount());
                }
                maxContents.set(hf.getTankCapacity(0));
            }
        }
        return helper.getContentsValue(millis, contents.get(), maxContents.get());
    }

    @Override
    public FluidBarScreenModule validate(Level world, BlockPos p, boolean isPlus) {
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
        return ScreenConfiguration.FLUID_RFPERTICK.get();
    }

    @Override
    public ItemStack mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked, Player player) {
        return ItemStack.EMPTY;
    }
}
