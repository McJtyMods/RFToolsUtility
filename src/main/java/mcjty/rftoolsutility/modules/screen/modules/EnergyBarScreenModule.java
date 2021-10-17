package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataContents;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class EnergyBarScreenModule implements IScreenModule<IModuleDataContents> {
    protected RegistryKey<World> dim = World.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;
    protected Direction side = Direction.DOWN;
    protected ScreenModuleHelper helper = new ScreenModuleHelper();

    @Override
    public IModuleDataContents getData(IScreenDataHelper h, World worldObj, long millis) {
        World world = WorldTools.getLevel(worldObj, dim);
        if (world == null) {
            return null;
        }

        if (!WorldTools.isLoaded(world, coordinate)) {
            return null;
        }

        TileEntity te = world.getBlockEntity(coordinate);
        if (!EnergyTools.isEnergyTE(te, side)) {
            return null;
        }
        EnergyTools.EnergyLevel energyLevel = EnergyTools.getEnergyLevelMulti(te, side);
        long energy = energyLevel.getEnergy();
        long maxEnergy = energyLevel.getMaxEnergy();
        return helper.getContentsValue(millis, energy, maxEnergy);
    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            helper.setShowdiff(tagCompound.getBoolean("showdiff"));
            coordinate = BlockPosTools.INVALID;
            if (tagCompound.contains("monitorx")) {
                this.dim = WorldTools.getId(tagCompound.getString("monitordim"));
                if (Objects.equals(dim, this.dim)) {
                    BlockPos c = new BlockPos(tagCompound.getInt("monitorx"), tagCompound.getInt("monitory"), tagCompound.getInt("monitorz"));
                    int dx = Math.abs(c.getX() - pos.getX());
                    int dy = Math.abs(c.getY() - pos.getY());
                    int dz = Math.abs(c.getZ() - pos.getZ());
                    if (dx <= 64 && dy <= 64 && dz <= 64) {
                        coordinate = c;
                        if(tagCompound.contains("monitorside")) {
                            side = OrientationTools.DIRECTION_VALUES[tagCompound.getInt("monitorside")];
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.ENERGY_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {

    }
}
