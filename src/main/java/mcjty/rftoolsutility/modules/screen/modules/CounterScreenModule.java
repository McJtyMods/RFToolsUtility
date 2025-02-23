package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class CounterScreenModule implements IScreenModule<IModuleDataInteger> {
    protected ResourceKey<Level> dim = Level.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;
    protected boolean active = false;

    public static final Codec<CounterScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(module -> module.dim),
            BlockPos.CODEC.fieldOf("coordinate").forGetter(module -> module.coordinate)
    ).apply(instance, CounterScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CounterScreenModule> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), module -> module.dim,
            BlockPos.STREAM_CODEC, module -> module.coordinate,
            CounterScreenModule::new);

    public CounterScreenModule(ResourceKey<Level> dim, BlockPos coordinate) {
        this.dim = dim;
        this.coordinate = coordinate;
    }

    public CounterScreenModule() {
    }

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        Level world = LevelTools.getLevel(worldObj, dim);
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, coordinate)) {
            return null;
        }

        BlockEntity te = world.getBlockEntity(coordinate);

        if (!(te instanceof CounterTileEntity counterTileEntity)) {
            return null;
        }
        return helper.createInteger(counterTileEntity.getCurrent());
    }

    @Override
    public void validate(Level world, BlockPos pos, boolean isPlus) {
        if (isPlus) {
            active = true;
            return;
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
        active = false;
        if (LevelTools.isLoaded(world, coordinate)) {
            if (Objects.equals(dim, world.dimension())) {
                int dx = Math.abs(coordinate.getX() - pos.getX());
                int dy = Math.abs(coordinate.getY() - pos.getY());
                int dz = Math.abs(coordinate.getZ() - pos.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                    active = true;
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.COUNTER_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
