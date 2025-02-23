package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TextScreenModule implements IScreenModule<IModuleData> {

    public static final Codec<TextScreenModule> CODEC = Codec.unit(TextScreenModule::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, TextScreenModule> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {},
            in -> new TextScreenModule()
    );

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
