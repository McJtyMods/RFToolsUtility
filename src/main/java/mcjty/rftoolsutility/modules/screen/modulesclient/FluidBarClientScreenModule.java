package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenLevelHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FluidBarClientScreenModule implements IClientScreenModule<IModuleDataContents> {

    private String line = "";
    private int color = 0xffffff;
    protected ResourceKey<Level> dim = Level.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;

    private final ITextRenderHelper labelCache = new ScreenTextHelper();
    private ILevelRenderHelper mbRenderer = new ScreenLevelHelper().gradient(0xff0088ff, 0xff003333);

    public static final Codec<FluidBarClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(module -> module.dim),
            BlockPos.CODEC.fieldOf("coordinate").forGetter(module -> module.coordinate),
            ScreenLevelHelper.CODEC.fieldOf("mbRenderer").forGetter(module -> (ScreenLevelHelper) module.mbRenderer)
    ).apply(instance, FluidBarClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidBarClientScreenModule>  STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            ResourceKey.streamCodec(Registries.DIMENSION), module -> module.dim,
            BlockPos.STREAM_CODEC, module -> module.coordinate,
            ScreenLevelHelper.STREAM_CODEC, module -> (ScreenLevelHelper) module.mbRenderer,
            FluidBarClientScreenModule::new);

    public FluidBarClientScreenModule(String line, int color, ResourceKey<Level> dim, BlockPos coordinate, ScreenLevelHelper mbRenderer) {
        this.line = line;
        this.color = color;
        this.dim = dim;
        this.coordinate = coordinate;
        this.mbRenderer = mbRenderer;
    }

    public FluidBarClientScreenModule() {
    }

    @Override
    public TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataContents screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();

        int xoffset;
        if (!line.isEmpty()) {
            labelCache.setup(line, 160, renderInfo);
            labelCache.renderText(graphics, buffer, 0, currenty, color, renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }

        if (!BlockPosTools.INVALID.equals(coordinate)) {
            mbRenderer.render(graphics, buffer, xoffset, currenty, screenData, renderInfo);
        } else {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, 0xffff0000, renderInfo, "<invalid>");
        }
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked) {
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
