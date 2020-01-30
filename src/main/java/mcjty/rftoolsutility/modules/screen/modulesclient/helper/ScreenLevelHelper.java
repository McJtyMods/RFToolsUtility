package mcjty.rftoolsutility.modules.screen.modulesclient.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.api.screens.FormatStyle;
import mcjty.rftoolsbase.api.screens.ILevelRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import net.minecraft.client.renderer.IRenderTypeBuffer;

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


    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int x, int y, @Nullable IModuleDataContents data, @Nonnull ModuleRenderInfo renderInfo) {
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
                RenderHelper.drawHorizontalGradientRect(matrixStack, buffer, x, y, (int) (x + value), y + 8, gradient1, gradient2);
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
                ScreenTextHelper.renderScaled(matrixStack, buffer, diffTxt, x, y, col, renderInfo.truetype);
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
