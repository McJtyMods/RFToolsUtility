package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.modules.environmental.PeacefulAreaManager;
import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class PeacefulEModule extends BuffEModule {

    public PeacefulEModule() {
        super(PlayerBuff.BUFF_PEACEFUL);
    }

    @Override
    public float getRfPerTick() {
        return (float)(double) EnvironmentalConfiguration.REGENERATION_RFPERTICK.get();
    }

    @Override
    public void tick(World world, BlockPos pos, int radius, int miny, int maxy, EnvironmentalControllerTileEntity controllerTileEntity) {
        if (!isActive()) {
            return;
        }

        super.tick(world, pos, radius, miny, maxy, controllerTileEntity);
        PeacefulAreaManager.markArea(GlobalPos.of(world.dimension(), pos), radius, miny, maxy);
    }
}
