package mcjty.rftoolsutility.modules.screen.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.WorldTools;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPlusBarScreenModule extends FluidBarScreenModule {

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, RegistryKey<World> dim, BlockPos pos) {
        if (tagCompound != null) {
            helper.setShowdiff(tagCompound.getBoolean("showdiff"));
            coordinate = BlockPosTools.INVALID;
            if (tagCompound.contains("monitorx")) {
                this.dim = WorldTools.getId(tagCompound.getString("monitordim"));
                coordinate = new BlockPos(tagCompound.getInt("monitorx"), tagCompound.getInt("monitory"), tagCompound.getInt("monitorz"));
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.FLUIDPLUS_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {

    }
}
