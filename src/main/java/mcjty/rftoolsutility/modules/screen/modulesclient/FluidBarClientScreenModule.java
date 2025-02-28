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
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FluidBarClientScreenModule implements IClientScreenModule<IModuleDataContents> {

    private String line = "";
    private int color = 0xffffff;
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);

    private final ITextRenderHelper labelCache = new ScreenTextHelper();
    private ILevelRenderHelper mbRenderer = new ScreenLevelHelper().gradient(0xff0088ff, 0xff003333);

    public static final Codec<FluidBarClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            ScreenLevelHelper.CODEC.fieldOf("mbRenderer").forGetter(module -> (ScreenLevelHelper) module.mbRenderer)
    ).apply(instance, FluidBarClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidBarClientScreenModule>  STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ScreenLevelHelper.STREAM_CODEC, module -> (ScreenLevelHelper) module.mbRenderer,
            FluidBarClientScreenModule::new);

    public FluidBarClientScreenModule(String line, int color, GlobalPos pos, ScreenLevelHelper mbRenderer) {
        this.line = line;
        this.color = color;
        this.pos = pos;
        this.mbRenderer = mbRenderer;
    }

    public FluidBarClientScreenModule() {
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

    public String getAlign() {
        return labelCache.getAlign().name();
    }

    public void setAlign(String align) {
        labelCache.align(TextAlign.get(align));
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

        if (!BlockPosTools.INVALID.equals(pos.pos())) {
            mbRenderer.render(graphics, buffer, xoffset, currenty, screenData, renderInfo);
        } else {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, 0xffff0000, renderInfo, "<invalid>");
        }
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
