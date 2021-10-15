package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class RedstoneScreenModule implements IScreenModule<IModuleDataInteger> {
    private int channel = -1;
    private BlockPos coordinate = BlockPosTools.INVALID;
    private RegistryKey<World> dim = World.OVERWORLD;
    private Direction side = null;

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, World worldObj, long millis) {
        if (channel == -1) {
            // If we are monitoring some block then we can use that.
            if (!BlockPosTools.INVALID.equals(coordinate)) {
                World world = WorldTools.getWorld(worldObj, dim);
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
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            channel = -1;
            if (tagCompound.contains("channel")) {
                channel = tagCompound.getInt("channel");
            }
            if (tagCompound.contains("monitorx")) {
                side = OrientationTools.DIRECTION_VALUES[tagCompound.getInt("monitorside")];
                this.dim = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tagCompound.getString("monitordim")));
                if (Objects.equals(dim, this.dim)) {
                    BlockPos c = new BlockPos(tagCompound.getInt("monitorx"), tagCompound.getInt("monitory"), tagCompound.getInt("monitorz"));
                    int dx = Math.abs(c.getX() - pos.getX());
                    int dy = Math.abs(c.getY() - pos.getY());
                    int dz = Math.abs(c.getZ() - pos.getZ());
                    if (dx <= 64 && dy <= 64 && dz <= 64) {
                        coordinate = c;
                    }
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.REDSTONE_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {

    }
}
