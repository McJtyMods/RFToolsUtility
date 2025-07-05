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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record TextScreenModule(String line, int color, TextAlign align, boolean large) implements IScreenModule<TextScreenModule, IModuleData> {

    public static final TextScreenModule DEFAULT = new TextScreenModule("", 0xffffff, TextAlign.ALIGN_LEFT, false);

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

    public String getLine() {
        return line;
    }

    public int getColor() {
        return color;
    }

    public TextAlign getAlign() {
        return align;
    }

    public boolean isLarge() {
        return large;
    }

    public TextScreenModule withLine(String line) {
        return new TextScreenModule(line, color, align, large);
    }

    public TextScreenModule withColor(int color) {
        return new TextScreenModule(line, color, align, large);
    }

    public TextScreenModule withAlign(TextAlign align) {
        return new TextScreenModule(line, color, align, large);
    }

    public TextScreenModule withLarge(boolean large) {
        return new TextScreenModule(line, color, align, large);
    }

    @Override
    public IModuleData getData(IScreenDataHelper helper, Level worldObj, long millis) {
        return null;
    }

    @Override
    public TextScreenModule validate(Level world, BlockPos pos, boolean isPlus) {
        return this;
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.TEXT_RFPERTICK.get();
    }

    @Override
    public ItemStack mouseClick(ItemStack moduleStack, Level world, int x, int y, boolean clicked, Player player) {
        return ItemStack.EMPTY;
    }
}
