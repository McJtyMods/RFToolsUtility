package mcjty.rftoolsutility.modules.screen.modulesclient.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.rftoolsbase.api.screens.FormatStyle;
import mcjty.rftoolsbase.api.screens.ILevelRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class ScreenLevelHelper implements ILevelRenderHelper {

    private boolean hidebar = false;
    private boolean hidetext = false;
    private boolean showdiff = false;
    private boolean showpct = false;
    private FormatStyle formatStyle = FormatStyle.MODE_FULL;
    private int poscolor = 0xffffff;
    private int negcolor = 0xffffff;
    private int gradient1 = 0xffff0000;
    private int gradient2 = 0xff333300;
    private String label = "";

    public static final Codec<ScreenLevelHelper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("hidebar").forGetter(module -> module.hidebar),
            Codec.BOOL.fieldOf("hidetext").forGetter(module -> module.hidetext),
            Codec.BOOL.fieldOf("showdiff").forGetter(module -> module.showdiff),
            Codec.BOOL.fieldOf("showpct").forGetter(module -> module.showpct),
            FormatStyle.CODEC.fieldOf("formatStyle").forGetter(module -> module.formatStyle),
            Codec.INT.fieldOf("poscolor").forGetter(module -> module.poscolor),
            Codec.INT.fieldOf("negcolor").forGetter(module -> module.negcolor),
            Codec.INT.fieldOf("gradient1").forGetter(module -> module.gradient1),
            Codec.INT.fieldOf("gradient2").forGetter(module -> module.gradient2),
            Codec.STRING.fieldOf("label").forGetter(module -> module.label)
    ).apply(instance, ScreenLevelHelper::new));

    public static final StreamCodec<FriendlyByteBuf, ScreenLevelHelper> STREAM_CODEC = CompositeStreamCodec.composite(
            ByteBufCodecs.BOOL, module -> module.hidebar,
            ByteBufCodecs.BOOL, module -> module.hidetext,
            ByteBufCodecs.BOOL, module -> module.showdiff,
            ByteBufCodecs.BOOL, module -> module.showpct,
            FormatStyle.STREAM_CODEC, module -> module.formatStyle,
            ByteBufCodecs.INT, module -> module.poscolor,
            ByteBufCodecs.INT, module -> module.negcolor,
            ByteBufCodecs.INT, module -> module.gradient1,
            ByteBufCodecs.INT, module -> module.gradient2,
            ByteBufCodecs.STRING_UTF8, module -> module.label,
            ScreenLevelHelper::new);


    public ScreenLevelHelper(boolean hidebar, boolean hidetext, boolean showdiff, boolean showpct, FormatStyle formatStyle, int poscolor, int negcolor, int gradient1, int gradient2, String label) {
        this.hidebar = hidebar;
        this.hidetext = hidetext;
        this.showdiff = showdiff;
        this.showpct = showpct;
        this.formatStyle = formatStyle;
        this.poscolor = poscolor;
        this.negcolor = negcolor;
        this.gradient1 = gradient1;
        this.gradient2 = gradient2;
        this.label = label;
    }

    public ScreenLevelHelper() {
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, int x, int y, @Nullable IModuleDataContents data, @Nonnull ModuleRenderInfo renderInfo) {
        if (data == null) {
            return;
        }

        long maxContents  = data.getMaxContents();
        if (maxContents > 0) {
            if (!hidebar) {
                long contents = data.getContents();

                int width = 80 - x + 7 + 40;
                long value = contents * width / maxContents;
                if (value < 0) {
                    value = 0;
                } else if (value > width) {
                    value = width;
                }
                RenderHelper.drawHorizontalGradientRect(graphics, buffer, x, y, (int) (x + value), y + 8, gradient1, gradient2,
                        renderInfo.getLightmapValue());
            }
        }
        if (!hidetext) {
            String diffTxt = null;
            int col = poscolor;
            if (showdiff) {
                long diff = data.getLastPerTick();
                if (diff < 0) {
                    col = negcolor;
                    diffTxt = diff + " " + label + "/t";
                } else {
                    diffTxt = "+" + diff + " " + label + "/t";
                }
            } else if (maxContents > 0) {
                long contents = data.getContents();
                if (showpct) {
                    long value = contents * 100 / maxContents;
                    if (value < 0) {
                        value = 0;
                    } else if (value > 100) {
                        value = 100;
                    }
                    diffTxt = value + "%";
                } else {
                    diffTxt = format(String.valueOf(contents), formatStyle) + label;
                }
            }
            if (diffTxt != null) {
                ScreenTextHelper.renderScaled(ScreenConfiguration.getTrueTypeFont(), graphics, buffer, diffTxt, x, y, col, renderInfo.truetype, renderInfo.getLightmapValue());
            }
        }
    }

    @Override
    public ILevelRenderHelper label(String label) {
        this.label = label;
        return this;
    }

    @Override
    public ILevelRenderHelper settings(boolean hidebar, boolean hidetext, boolean showpct, boolean showdiff) {
        this.hidebar = hidebar;
        this.hidetext = hidetext;
        this.showpct = showpct;
        this.showdiff = showdiff;
        return this;
    }

    @Override
    public ILevelRenderHelper color(int poscolor, int negcolor) {
        this.poscolor = poscolor;
        this.negcolor = negcolor;
        return this;
    }

    @Override
    public ILevelRenderHelper gradient(int gradient1, int gradient2) {
        this.gradient1 = gradient1;
        this.gradient2 = gradient2;
        return this;
    }

    @Override
    public ILevelRenderHelper format(FormatStyle formatStyle) {
        this.formatStyle = formatStyle;
        return this;
    }

    @Override
    public int getPosColor() {
        return poscolor;
    }

    @Override
    public int getNegColor() {
        return negcolor;
    }

    @Override
    public int getGradient1() {
        return gradient1;
    }

    @Override
    public int getGradient2() {
        return gradient2;
    }

    @Override
    public FormatStyle getFormatStyle() {
        return formatStyle;
    }

    @Override
    public boolean isHideBar() {
        return hidebar;
    }

    @Override
    public boolean isHideText() {
        return hidetext;
    }

    @Override
    public boolean isShowPct() {
        return showpct;
    }

    @Override
    public boolean isShowDiff() {
        return showdiff;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setPosColor(int poscolor) {
        this.poscolor = poscolor;
    }

    @Override
    public void setNegColor(int negcolor) {
        this.negcolor = negcolor;
    }

    @Override
    public void setGradient1(int gradient1) {
        this.gradient1 = gradient1;
    }

    @Override
    public void setGradient2(int gradient2) {
        this.gradient2 = gradient2;
    }

    @Override
    public void setFormatStyle(FormatStyle formatStyle) {
        this.formatStyle = formatStyle;
    }

    @Override
    public void setHideBar(boolean hidebar) {
        this.hidebar = hidebar;
    }

    @Override
    public void setHideText(boolean hidetext) {
        this.hidetext = hidetext;
    }

    @Override
    public void setShowPct(boolean showpct) {
        this.showpct = showpct;
    }

    @Override
    public void setShowDiff(boolean showdiff) {
        this.showdiff = showdiff;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    private static DecimalFormat dfCommas = new DecimalFormat("###,###");

    private String format(String in, FormatStyle style) {
        switch (style) {
            case MODE_FULL:
                return in;
            case MODE_COMPACT: {
                long contents = Long.parseLong(in);
                int unit = 1000;
                if (contents < unit) {
                    return in;
                }
                int exp = (int) (Math.log(contents) / Math.log(unit));
                char pre = "kMGTPE".charAt(exp-1);
                return String.format("%.1f %s", contents / Math.pow(unit, exp), pre);
            }
            case MODE_COMMAS:
                return dfCommas.format(Long.parseLong(in));
        }
        return in;
    }
}
