package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface EnvironmentModule {

    float getRfPerTick();

    void tick(Level world, BlockPos pos, int radius, int miny, int maxy, EnvironmentalControllerTileEntity controllerTileEntity);

    // Apply the effect once on an entity. Return true if it worked
    boolean apply(Level world, BlockPos pos, LivingEntity entity, int duration);

    void activate(boolean a);
}
