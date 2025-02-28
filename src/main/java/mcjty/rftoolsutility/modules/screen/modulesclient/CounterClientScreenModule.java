package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CounterClientScreenModule implements IClientScreenModule<IModuleDataInteger> {

    private String line = "";
    private int color = 0xffffff;
    private int cntcolor = 0xffffff;
    private FormatStyle format = FormatStyle.MODE_FULL;
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

    public static final Codec<CounterClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.INT.fieldOf("cntcolor").forGetter(module -> module.cntcolor),
            FormatStyle.CODEC.fieldOf("format").forGetter(module -> module.format),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos)
    ).apply(instance, CounterClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CounterClientScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.INT, module -> module.cntcolor,
            FormatStyle.STREAM_CODEC, module -> module.format,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            CounterClientScreenModule::new);

    public CounterClientScreenModule(String line, int color, int cntcolor, FormatStyle format, GlobalPos pos) {
        this.line = line;
        this.color = color;
        this.cntcolor = cntcolor;
        this.format = format;
        this.pos = pos;
    }

    public CounterClientScreenModule() {
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

    public String getAlign() {
        return labelCache.getAlign().name();
    }

    public void setAlign(String align) {
        labelCache.align(TextAlign.get(align));
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

    public void setPos(GlobalPos pos) {
        this.pos = pos;
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
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleDataInteger screenData, ModuleRenderInfo renderInfo) {
        // @todo 1.15
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
            int current;
            if (screenData != null) {
                current = screenData.get();
            } else {
                current = 0;
            }
            String output = renderHelper.format(String.valueOf(current), format);
            renderHelper.renderText(graphics, buffer, xoffset, currenty, cntcolor, renderInfo, output);
        } else {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, 0xff0000, renderInfo, "<invalid>");
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
