package mcjty.rftoolsutility.modules.teleporter.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;


import net.minecraft.block.AbstractBlock;

public class DestinationAnalyzerBlock extends Block {

    public DestinationAnalyzerBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .harvestLevel(0)
                .harvestTool(ToolType.PICKAXE)
                .strength(2.0f, 6.0f));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        PlayerEntity placer = context.getPlayer();
        return super.getStateForPlacement(context).setValue(BlockStateProperties.FACING, getFacingFromEntity(pos, placer));
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entityIn) {
        if (MathHelper.abs((float) entityIn.getX() - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.getZ() - clickedBlock.getZ()) < 2.0F) {
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }
}
