package mcjty.rftoolsutility.modules.screen.modulesclient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleRenderHelper;
import mcjty.rftoolsbase.api.screens.ITextRenderHelper;
import mcjty.rftoolsbase.api.screens.ModuleRenderInfo;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsbase.tools.ScreenTextHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TextClientScreenModule implements IClientScreenModule<IModuleData> {
    private String line = "";
    private int color = 0xffffff;

    private final ITextRenderHelper cache = new ScreenTextHelper();

    public static final Codec<TextClientScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color)
    ).apply(instance, TextClientScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TextClientScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            TextClientScreenModule::new);

    public TextClientScreenModule(String line, int color) {
        this.line = line;
        this.color = color;
    }

    public TextClientScreenModule() {
    }

    @Override
    public TransformMode getTransformMode(ItemStack moduleItem) {
        return cache.isLarge() ? TransformMode.TEXTLARGE : TransformMode.TEXT;
    }

    @Override
    public int getHeight(ItemStack moduleItem) {
        return cache.isLarge() ? 20 : 10;
    }

    @Override
    public void render(GuiGraphics graphics, MultiBufferSource buffer, IModuleRenderHelper renderHelper, Font fontRenderer, int currenty, IModuleData screenData, ModuleRenderInfo renderInfo) {
        cache.setup(line, 512, renderInfo);
        int y = cache.isLarge() ? (currenty / 2 + 1) : currenty;
        cache.renderText(graphics, buffer, 0, y, color, renderInfo);
    }

    @Override
    public void mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked) {

    }

    public void setLine(String line) {
        this.line = line;
        cache.setDirty();
    }

    public void setColor(int color) {
        this.color = color;
        cache.setDirty();
    }

    public void setLarge(boolean large) {
        cache.large(large);
        cache.setDirty();
    }

    @Override
    public boolean needsServerData() {
        return false;
    }
}
