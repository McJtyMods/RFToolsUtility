package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import mcjty.rftoolsutility.modules.screen.modulesclient.helper.ScreenLevelHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class EnergyBarClientScreenModule implements IClientScreenModule<IModuleDataContents> {

    private String line = "";
    private int color = 0xffffff;
    private GlobalPos pos = GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID);

    private final ITextRenderHelper labelCache = new ScreenTextHelper();
    private final ILevelRenderHelper rfRenderer = new ScreenLevelHelper().gradient(0xffff0000, 0xff333300);

    public static final Codec<EnergyBarClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos)
    ).apply(instance, EnergyBarClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyBarClientScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            EnergyBarClientScreenModule::new);

    public EnergyBarClientScreenModule(String line, int color, GlobalPos pos) {
        this.line = line;
        this.color = color;
        this.pos = pos;
    }

    public EnergyBarClientScreenModule() {
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

    public void setPos(GlobalPos pos) {
        this.pos = pos;
    }

    public String getAlign() {
        return labelCache.getAlign().name();
    }

    public void setAlign(String align) {
        labelCache.align(TextAlign.get(align));
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
            rfRenderer.render(graphics, buffer, xoffset, currenty, screenData, renderInfo);
        } else {
            renderHelper.renderText(graphics, buffer, xoffset, currenty, 0xff0000, renderInfo, "<invalid>");
        }
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked) {

    }

    @Override
    public void setupFromNBT(CompoundTag tagCompound, ResourceKey<Level> dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.contains("color")) {
                color = tagCompound.getInt("color");
            } else {
                color = 0xffffff;
            }
            int rfcolor;
            if (tagCompound.contains("rfcolor")) {
                rfcolor = tagCompound.getInt("rfcolor");
            } else {
                rfcolor = 0xffffff;
            }
            int rfcolorNeg;
            if (tagCompound.contains("rfcolor_neg")) {
                rfcolorNeg = tagCompound.getInt("rfcolor_neg");
            } else {
                rfcolorNeg = 0xffffff;
            }
            rfRenderer.color(rfcolor, rfcolorNeg);

            if (tagCompound.contains("align")) {
                String alignment = tagCompound.getString("align");
                labelCache.align(TextAlign.get(alignment));
            } else {
                labelCache.align(TextAlign.ALIGN_LEFT);
            }

            boolean hidebar = tagCompound.getBoolean("hidebar");
            boolean hidetext = tagCompound.getBoolean("hidetext");
            boolean showdiff = tagCompound.getBoolean("showdiff");
            boolean showpct = tagCompound.getBoolean("showpct");
            rfRenderer.settings(hidebar, hidetext, showpct, showdiff);

//            rfRenderer.format(FormatStyle.values()[tagCompound.getInt("format")]);
            rfRenderer.format(FormatStyle.getStyle(tagCompound.getString("format")));

            setupCoordinateFromNBT(tagCompound, dim, pos);
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
