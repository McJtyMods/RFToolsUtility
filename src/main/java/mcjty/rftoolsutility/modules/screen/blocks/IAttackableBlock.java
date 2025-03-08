package mcjty.rftoolsutility.modules.screen.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface IAttackableBlock {
    void doAttack(Level world, @NotNull BlockPos pos);
}
