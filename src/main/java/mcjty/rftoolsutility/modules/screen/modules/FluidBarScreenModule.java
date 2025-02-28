package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenLevelHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FluidBarScreenModule implements IScreenModule<IModuleDataContents> {
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);
    private ScreenModuleHelper helper = new ScreenModuleHelper();
    private boolean active = false;

    // Client side
    private String line = "";
    private int color = 0xffffff;
    private TextAlign align = TextAlign.ALIGN_LEFT;
    private ILevelRenderHelper mbRenderer = new ScreenLevelHelper().gradient(0xff0088ff, 0xff003333);


    public static final Codec<FluidBarScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align),
            ScreenLevelHelper.CODEC.fieldOf("mbRenderer").forGetter(module -> (ScreenLevelHelper) module.mbRenderer)
    ).apply(instance, FluidBarScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidBarScreenModule> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            TextAlign.STREAM_CODEC, module -> module.align,
            ScreenLevelHelper.STREAM_CODEC, module -> (ScreenLevelHelper) module.mbRenderer,
            FluidBarScreenModule::new);

    public FluidBarScreenModule(GlobalPos pos, String line, int color, TextAlign align, ILevelRenderHelper mbRenderer) {
        this.pos = pos;
        this.line = line;
        this.color = color;
        this.align = align;
        this.mbRenderer = mbRenderer;
    }

    public FluidBarScreenModule() {
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

    public GlobalPos getPos() {
        return pos;
    }

    public TextAlign getAlign() {
        return align;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    public int getPosColor() {
        return mbRenderer.getPosColor();
    }

    public void setPosColor(int poscolor) {
        mbRenderer.setPosColor(poscolor);
    }

    public int getNegColor() {
        return mbRenderer.getNegColor();
    }

    public void setNegColor(int negcolor) {
        mbRenderer.setNegColor(negcolor);
    }

    public boolean isHideBar() {
        return mbRenderer.isHideBar();
    }

    public void setHideBar(boolean hidebar) {
        mbRenderer.setHideBar(hidebar);
    }

    public FormatStyle getFormat() {
        return mbRenderer.getFormatStyle();
    }

    public void setFormat(FormatStyle format) {
        mbRenderer.setFormatStyle(format);
    }

    public ILevelRenderHelper getMbRenderer() {
        return mbRenderer;
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
        // @todo 1.21 cap
//        if (!CapabilityTools.getFluidCapabilitySafe(te).map(hf -> {
//            if (hf.getTanks() > 0) {
//                if (!hf.getFluidInTank(0).isEmpty()) {
//                    contents.set(hf.getFluidInTank(0).getAmount());
//                }
//                maxContents.set(hf.getTankCapacity(0));
//            }
//            return true;
//        }).orElse(false)) {
//            return null;
//        }

        return helper.getContentsValue(millis, contents.get(), maxContents.get());
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
        return ScreenConfiguration.FLUID_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
