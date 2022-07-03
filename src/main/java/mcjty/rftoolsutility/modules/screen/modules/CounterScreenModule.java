package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleDataInteger;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class CounterScreenModule implements IScreenModule<IModuleDataInteger> {
    protected RegistryKey<World> dim = World.OVERWORLD;
    protected BlockPos coordinate = BlockPosTools.INVALID;

    @Override
    public IModuleDataInteger getData(IScreenDataHelper helper, World worldObj, long millis) {
        World world = LevelTools.getLevel(worldObj, dim);
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, coordinate)) {
            return null;
        }

        TileEntity te = world.getBlockEntity(coordinate);

        if (!(te instanceof CounterTileEntity)) {
            return null;
        }

        CounterTileEntity counterTileEntity = (CounterTileEntity) te;
        return helper.createInteger(counterTileEntity.getCurrent());
    }

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            coordinate = BlockPosTools.INVALID;
            if (tagCompound.contains("monitorx")) {
                this.dim = LevelTools.getId(tagCompound.getString("monitordim"));
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
        return ScreenConfiguration.COUNTER_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {

    }
}
