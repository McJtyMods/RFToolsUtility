package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class RedstoneScreenModule implements IScreenModule<IModuleDataInteger> {
    private int channel = -1;
    private BlockPos coordinate = BlockPosTools.INVALID;
    private ResourceKey<Level> dim = Level.OVERWORLD;
    private Direction side = null;

    private boolean active = false;

    public static final Codec<RedstoneScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("channel", -1).forGetter(module -> module.channel),
            BlockPos.CODEC.optionalFieldOf("monitor", BlockPosTools.INVALID).forGetter(module -> module.coordinate),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dim", Level.OVERWORLD).forGetter(module -> module.dim),
            Direction.CODEC.optionalFieldOf("side", null).forGetter(module -> module.side)
    ).apply(instance, RedstoneScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.channel,
            BlockPos.STREAM_CODEC, module -> module.coordinate,
            ResourceKey.streamCodec(Registries.DIMENSION), module -> module.dim,
            Direction.STREAM_CODEC, module -> module.side,
            RedstoneScreenModule::new);

    public RedstoneScreenModule(int channel, BlockPos coordinate, ResourceKey<Level> dim, Direction side) {
        this.channel = channel;
        this.coordinate = coordinate;
        this.dim = dim;
        this.side = side;
    }

    public RedstoneScreenModule() {
    }

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        if (channel == -1) {
            // If we are monitoring some block then we can use that.
            if (!BlockPosTools.INVALID.equals(coordinate)) {
                Level world = LevelTools.getLevel(worldObj, dim);
                if (world != null) {
//                    int powerTo = world.isBlockProvidingPowerTo(coordinate.getX(), coordinate.getY(), coordinate.getZ(), side);
                    int powerTo = world.getSignal(coordinate.relative(side), side.getOpposite());
//                    int powerTo = world.getIndirectPowerLevelTo(coordinate.getX(), coordinate.getY(), coordinate.getZ(), side);

                    return helper.createInteger(powerTo);
                }
            }
            return null;
        }
        RedstoneChannels channels = RedstoneChannels.getChannels(worldObj);
        if (channels == null) {
            return null;
        }
        RedstoneChannels.RedstoneChannel ch = channels.getChannel(channel);
        if (ch == null) {
            return null;
        }
        return helper.createInteger(ch.getValue());
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
        return ScreenConfiguration.REDSTONE_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
