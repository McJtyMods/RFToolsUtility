package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EnvironmentModule {

    float getRfPerTick();

    void tick(World world, BlockPos pos, int radius, int miny, int maxy, EnvironmentalControllerTileEntity controllerTileEntity);

    // Apply the effect once on an entity. Return true if it worked
    boolean apply(World world, BlockPos pos, LivingEntity entity, int duration);

    void activate(boolean a);
}
