package mcjty.rftoolsutility.modules.spawner.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static mcjty.lib.builder.TooltipBuilder.*;

//@Optional.InterfaceList({
//        @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class MatterBeamerBlock extends BaseBlock {

    public MatterBeamerBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(MatterBeamerTileEntity::new)
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .infusable()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold()));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        if (world.isRemote) {
            MatterBeamerTileEntity matterBeamerTileEntity = (MatterBeamerTileEntity) world.getTileEntity(pos);
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            matterBeamerTileEntity.useWrench(player);
        }
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BlockStateProperties.LIT);
    }

//    @Override
//    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
//        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
//        boolean working = false;
//        if (te instanceof MatterBeamerTileEntity) {
//            working = ((MatterBeamerTileEntity)te).isGlowing();
//        }
//        return state.withProperty(WORKING, working);
//    }

}
