package mcjty.rftoolsutility.modules.teleporter.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class MatterBoosterBlock extends Block {

    public MatterBoosterBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f, 6.0f));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Player placer = context.getPlayer();
        return super.getStateForPlacement(context).setValue(BlockStateProperties.FACING, getFacingFromEntity(pos, placer));
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entityIn) {
        if (Mth.abs((float) entityIn.getX() - clickedBlock.getX()) < 2.0F && Mth.abs((float) entityIn.getZ() - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.getY() + entityIn.getEyeHeight();

            if (d0 - clickedBlock.getY() > 2.0D) {
                return Direction.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0D) {
                return Direction.DOWN;
            }
        }

        return entityIn.getDirection().getOpposite();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }
}
