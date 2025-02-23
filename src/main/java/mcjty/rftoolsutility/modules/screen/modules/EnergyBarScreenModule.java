package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class EnergyBarScreenModule implements IScreenModule<IModuleDataContents> {
    protected ResourceKey<Level> dim = Level.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;
    protected Direction side = Direction.DOWN;
    protected ScreenModuleHelper helper = new ScreenModuleHelper();
    protected boolean active = false;

    public static final Codec<EnergyBarScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(module -> module.dim),
            BlockPos.CODEC.fieldOf("coordinate").forGetter(module -> module.coordinate),
            Direction.CODEC.fieldOf("side").forGetter(module -> module.side)
    ).apply(instance, EnergyBarScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyBarScreenModule> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), module -> module.dim,
            BlockPos.STREAM_CODEC, module -> module.coordinate,
            Direction.STREAM_CODEC, module -> module.side,
            EnergyBarScreenModule::new);

    public EnergyBarScreenModule(ResourceKey<Level> dim, BlockPos coordinate, Direction side) {
        this.dim = dim;
        this.coordinate = coordinate;
        this.side = side;
    }

    public EnergyBarScreenModule() {
    }

    @Override
    public IModuleDataContents getData(IScreenDataHelper h, Level worldObj, long millis) {
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
        if (!EnergyTools.isEnergyTE(te, side)) {
            return null;
        }
        EnergyTools.EnergyLevel energyLevel = EnergyTools.getEnergyLevelMulti(te, side);
        long energy = energyLevel.energy();
        long maxEnergy = energyLevel.maxEnergy();
        return helper.getContentsValue(millis, energy, maxEnergy);
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
        return ScreenConfiguration.ENERGY_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {
    }
}
