package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TextScreenModule implements IScreenModule<IModuleData> {
    private String line = "";
    private int color = 0xffffff;
    private TextAlign align = TextAlign.ALIGN_LEFT;
    private boolean large = false;

    public static final Codec<TextScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            TextAlign.CODEC.fieldOf("align").forGetter(module -> module.align),
            Codec.BOOL.fieldOf("large").forGetter(module -> module.large)
    ).apply(instance, TextScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TextScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.INT, module -> module.color,
            TextAlign.STREAM_CODEC, module -> module.align,
            ByteBufCodecs.BOOL, module -> module.large,
            TextScreenModule::new);

    public TextScreenModule(String line, int color, TextAlign align, boolean large) {
        this.line = line;
        this.color = color;
        this.align = align;
        this.large = large;
    }

    public TextScreenModule() {
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

    public TextAlign getAlign() {
        return align;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    public boolean isLarge() {
        return large;
    }

    public void setLarge(boolean large) {
        this.large = large;
    }

    @Override
    public IModuleData getData(IScreenDataHelper helper, Level worldObj, long millis) {
        return null;
    }

    @Override
    public void validate(Level world, BlockPos pos, boolean isPlus) {
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.TEXT_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
