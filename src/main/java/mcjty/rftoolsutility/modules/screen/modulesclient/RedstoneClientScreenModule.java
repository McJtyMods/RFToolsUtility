package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.CompositeStreamCodec;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class RedstoneClientScreenModule implements IClientScreenModule<IModuleDataInteger> {

    private String line = "";
    private String yestext = "on";
    private String notext = "off";
    private int color = 0xffffff;
    private int yescolor = 0xffffff;
    private int nocolor = 0xffffff;
    private boolean analog = false;

    public static final Codec<RedstoneClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.STRING.fieldOf("yestext").forGetter(module -> module.yestext),
            Codec.STRING.fieldOf("notext").forGetter(module -> module.notext),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.INT.fieldOf("yescolor").forGetter(module -> module.yescolor),
            Codec.INT.fieldOf("nocolor").forGetter(module -> module.nocolor),
            Codec.BOOL.fieldOf("analog").forGetter(module -> module.analog)
    ).apply(instance, RedstoneClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneClientScreenModule> STREAM_CODEC = CompositeStreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.STRING_UTF8, module -> module.yestext,
            ByteBufCodecs.STRING_UTF8, module -> module.notext,
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.INT, module -> module.yescolor,
            ByteBufCodecs.INT, module -> module.nocolor,
            ByteBufCodecs.BOOL, module -> module.analog,
            RedstoneClientScreenModule::new);

    public RedstoneClientScreenModule(String line, String yestext, String notext, int color, int yescolor, int nocolor, boolean analog) {
        this.line = line;
        this.yestext = yestext;
        this.notext = notext;
        this.color = color;
        this.yescolor = yescolor;
        this.nocolor = nocolor;
        this.analog = analog;
    }

    public RedstoneClientScreenModule() {
    }

    private final ITextRenderHelper labelCache = new ScreenTextHelper();

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

        String text;
        int col;
        if (screenData != null) {
            int power = screenData.get();
            boolean rs = power > 0;
            if(analog) {
                text = Integer.toString(power);
            } else {
                text = rs ? yestext : notext;
            }
            col = rs ? yescolor : nocolor;
        } else {
            text = "<invalid>";
            col = 0xff0000;
        }
        renderHelper.renderText(graphics, buffer, xoffset, currenty, col, renderInfo, text);
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked) {

    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
