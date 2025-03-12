package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public record ClockScreenModule(int color, String line, boolean large) implements IScreenModule<ClockScreenModule, IModuleData> {

    public static final ClockScreenModule DEFAULT = new ClockScreenModule(0xffffff, "", false);

    public static final Codec<ClockScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(module -> module.color),
            Codec.STRING.fieldOf("line").forGetter(module -> module.line),
            Codec.BOOL.fieldOf("large").forGetter(module -> module.large)
    ).apply(instance, ClockScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClockScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.color,
            ByteBufCodecs.STRING_UTF8, module -> module.line,
            ByteBufCodecs.BOOL, module -> module.large,
            ClockScreenModule::new);

    public int getColor() {
        return color;
    }

    public String getLine() {
        return line;
    }

    public boolean isLarge() {
        return large;
    }

    public ClockScreenModule withColor(int color) {
        return new ClockScreenModule(color, line, large);
    }

    public ClockScreenModule withLine(String line) {
        return new ClockScreenModule(color, line, large);
    }

    public ClockScreenModule withLarge(boolean large) {
        return new ClockScreenModule(color, line, large);
    }

    @Override
    public IModuleData getData(IScreenDataHelper helper, Level worldObj, long millis) {
        return null;
    }

    @Override
    public ClockScreenModule validate(Level world, BlockPos pos, boolean isPlus) {
        return this;
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.CLOCK_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
