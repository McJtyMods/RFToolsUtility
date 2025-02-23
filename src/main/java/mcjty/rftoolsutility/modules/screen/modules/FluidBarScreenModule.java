package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FluidBarScreenModule implements IScreenModule<IModuleDataContents> {
    protected ResourceKey<Level> dim = Level.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;
    protected ScreenModuleHelper helper = new ScreenModuleHelper();
    protected boolean active = false;

    public static final Codec<FluidBarScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(module -> module.dim),
            BlockPos.CODEC.fieldOf("coordinate").forGetter(module -> module.coordinate)
    ).apply(instance, FluidBarScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidBarScreenModule> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), module -> module.dim,
            BlockPos.STREAM_CODEC, module -> module.coordinate,
            FluidBarScreenModule::new);

    public FluidBarScreenModule(ResourceKey<Level> dim, BlockPos coordinate) {
        this.dim = dim;
        this.coordinate = coordinate;
    }

    public FluidBarScreenModule() {
    }

    @Override
    public IModuleDataContents getData(IScreenDataHelper h, Level worldObj, long millis) {
        Level world = LevelTools.getLevel(worldObj, dim);
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, coordinate)) {
            return null;
        }

        AtomicInteger contents = new AtomicInteger();
        AtomicInteger maxContents = new AtomicInteger();

        BlockEntity te = world.getBlockEntity(coordinate);
        // @todo 1.21 cap
//        if (!CapabilityTools.getFluidCapabilitySafe(te).map(hf -> {
//            if (hf.getTanks() > 0) {
//                if (!hf.getFluidInTank(0).isEmpty()) {
//                    contents.set(hf.getFluidInTank(0).getAmount());
//                }
//                maxContents.set(hf.getTankCapacity(0));
//            }
//            return true;
//        }).orElse(false)) {
//            return null;
//        }

        return helper.getContentsValue(millis, contents.get(), maxContents.get());
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
        return ScreenConfiguration.FLUID_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
