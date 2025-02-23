package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.Locale;

public class ClockClientScreenModule implements IClientScreenModule<IModuleData> {
    private int color = 0xffffff;
    private String line = "";
    private boolean large = false;

    public static final Codec<ClockClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.BOOL.fieldOf("large").forGetter(module -> module.large)
    ).apply(instance, ClockClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClockClientScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.BOOL, module -> module.large,
            ClockClientScreenModule::new);

    public ClockClientScreenModule(int color, String line, boolean large) {
        this.color = color;
        this.line = line;
        this.large = large;
    }

    public ClockClientScreenModule() {
    }

    @Override
    public IClientScreenModule.TransformMode getTransformMode() {
        return large ? TransformMode.TEXTLARGE : TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return large ? 20 : 10;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleData screenData, ModuleRenderInfo renderInfo) {
//        GlStateManager.disableLighting();
        Minecraft minecraft = Minecraft.getInstance();

        final long time = minecraft.level.getGameTime();
        long hour = (time / 1000 + 6) % 24;
        final long minute = (time % 1000) * 60 / 1000;
        String timeString = String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);

        int xoffset;
        int y;
        if (large) {
            xoffset = 4;
            y = currenty / 2 + 1;
        } else {
            xoffset = 7;
            y = currenty;
        }

        renderHelper.renderText(graphics, buffer, xoffset, y, color, renderInfo, line + " " + timeString);
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked) {

    }

    @Override
    public boolean needsServerData() {
        return false;
    }
}
