package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsbase.api.screens.IModuleProvider;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static net.minecraft.state.properties.BlockStateProperties.FACING;

public class ScreenBlock extends BaseBlock {

    public static final DirectionProperty HORIZ_FACING = DirectionProperty.create("horizfacing", Direction.Plane.HORIZONTAL);

    private final boolean creative;

    public ScreenBlock(Supplier<TileEntity> supplier, boolean creative) {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:machines/screen"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(supplier));
        this.creative = creative;
    }

    public boolean isCreative() {
        return creative;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).setValue(HORIZ_FACING, context.getPlayer().getDirection().getOpposite());
    }

    private static long lastTime = 0;

    public static boolean hasModuleProvider(ItemStack stack) {
        return stack.getItem() instanceof IModuleProvider;// @todo || stack.getCapability(IModuleProvider.CAPABILITY).isPresent();
    }

    public static LazyOptional<IModuleProvider> getModuleProvider(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IModuleProvider) {
            return LazyOptional.of(() -> (IModuleProvider) item);
        } else {
            return stack.getCapability(IModuleProvider.CAPABILITY);
        }
    }

    public ActionResultType activate(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        return use(state, world, pos, player, hand, result);
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
        // Doesn't make sense to rotate a potentially 3x3 screen,
        // and is incompatible with our special wrench actions.
        return state;
    }


    @Override
    public void attack(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
        if (world.isClientSide) {
            RayTraceResult mouseOver = SafeClientTools.getClientMouseOver();
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
            if (mouseOver instanceof BlockRayTraceResult) {
                screenTileEntity.hitScreenClient(mouseOver.getLocation().x - pos.getX(), mouseOver.getLocation().y - pos.getY(), mouseOver.getLocation().z - pos.getZ(),
                        ((BlockRayTraceResult) mouseOver).getDirection(), world.getBlockState(pos).getValue(HORIZ_FACING));
            }
        }
    }

    private void setInvisibleBlockSafe(World world, BlockPos pos, int dx, int dy, int dz, Direction facing) {
        int yy = pos.getY() + dy;
        if (yy < 0 || yy >= world.getMaxBuildHeight()) {
            return;
        }
        int xx = pos.getX() + dx;
        int zz = pos.getZ() + dz;
        BlockPos posO = new BlockPos(xx, yy, zz);
        if (world.isEmptyBlock(posO)) {
            world.setBlock(posO, ScreenModule.SCREEN_HIT.get().defaultBlockState().setValue(FACING, facing), 3);
            ScreenHitTileEntity screenHitTileEntity = (ScreenHitTileEntity) world.getBlockEntity(posO);
            screenHitTileEntity.setRelativeLocation(-dx, -dy, -dz);
        }
    }

    private void setInvisibleBlocks(World world, BlockPos pos, int size) {
        BlockState state = world.getBlockState(pos);
        Direction facing = state.getValue(FACING);
        Direction horizontalFacing = state.getValue(HORIZ_FACING);

        for (int i = 0; i <= size; i++) {
            for (int j = 0; j <= size; j++) {
                if (i != 0 || j != 0) {
                    if (facing == Direction.NORTH) {
                        setInvisibleBlockSafe(world, pos, -i, -j, 0, facing);
                    } else if (facing == Direction.SOUTH) {
                        setInvisibleBlockSafe(world, pos, i, -j, 0, facing);
                    } else if (facing == Direction.WEST) {
                        setInvisibleBlockSafe(world, pos, 0, -i, j, facing);
                    } else if (facing == Direction.EAST) {
                        setInvisibleBlockSafe(world, pos, 0, -i, -j, facing);
                    } else if (facing == Direction.UP) {
                        if (horizontalFacing == Direction.NORTH) {
                            setInvisibleBlockSafe(world, pos, -i, 0, -j, facing);
                        } else if (horizontalFacing == Direction.SOUTH) {
                            setInvisibleBlockSafe(world, pos, i, 0, j, facing);
                        } else if (horizontalFacing == Direction.WEST) {
                            setInvisibleBlockSafe(world, pos, -i, 0, j, facing);
                        } else if (horizontalFacing == Direction.EAST) {
                            setInvisibleBlockSafe(world, pos, i, 0, -j, facing);
                        }
                    } else if (facing == Direction.DOWN) {
                        if (horizontalFacing == Direction.NORTH) {
                            setInvisibleBlockSafe(world, pos, -i, 0, j, facing);
                        } else if (horizontalFacing == Direction.SOUTH) {
                            setInvisibleBlockSafe(world, pos, i, 0, -j, facing);
                        } else if (horizontalFacing == Direction.WEST) {
                            setInvisibleBlockSafe(world, pos, i, 0, j, facing);
                        } else if (horizontalFacing == Direction.EAST) {
                            setInvisibleBlockSafe(world, pos, -i, 0, -j, facing);
                        }
                    }
                }
            }
        }
    }

    private void clearInvisibleBlockSafe(World world, BlockPos pos) {
        if (pos.getY() < 0 || pos.getY() >= world.getMaxBuildHeight()) {
            return;
        }
        if (world.getBlockState(pos).getBlock() == ScreenModule.SCREEN_HIT.get()) {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    private void clearInvisibleBlocks(World world, BlockPos pos, BlockState state, int size) {
        Direction facing = state.getValue(FACING);
        Direction horizontalFacing = state.getValue(HORIZ_FACING);
        for (int i = 0; i <= size; i++) {
            for (int j = 0; j <= size; j++) {
                if (i != 0 || j != 0) {
                    if (facing == Direction.NORTH) {
                        clearInvisibleBlockSafe(world, pos.offset(-i, -j, 0));
                    } else if (facing == Direction.SOUTH) {
                        clearInvisibleBlockSafe(world, pos.offset(i, -j, 0));
                    } else if (facing == Direction.WEST) {
                        clearInvisibleBlockSafe(world, pos.offset(0, -i, j));
                    } else if (facing == Direction.EAST) {
                        clearInvisibleBlockSafe(world, pos.offset(0, -i, -j));
                    } else if (facing == Direction.UP) {
                        if (horizontalFacing == Direction.NORTH) {
                            clearInvisibleBlockSafe(world, pos.offset(-i, 0, -j));
                        } else if (horizontalFacing == Direction.SOUTH) {
                            clearInvisibleBlockSafe(world, pos.offset(i, 0, j));
                        } else if (horizontalFacing == Direction.WEST) {
                            clearInvisibleBlockSafe(world, pos.offset(-i, 0, j));
                        } else if (horizontalFacing == Direction.EAST) {
                            clearInvisibleBlockSafe(world, pos.offset(i, 0, -j));
                        }
                    } else if (facing == Direction.DOWN) {
                        if (horizontalFacing == Direction.NORTH) {
                            clearInvisibleBlockSafe(world, pos.offset(-i, 0, j));
                        } else if (horizontalFacing == Direction.SOUTH) {
                            clearInvisibleBlockSafe(world, pos.offset(i, 0, -j));
                        } else if (horizontalFacing == Direction.WEST) {
                            clearInvisibleBlockSafe(world, pos.offset(i, 0, j));
                        } else if (horizontalFacing == Direction.EAST) {
                            clearInvisibleBlockSafe(world, pos.offset(-i, 0, -j));
                        }
                    }
                }
            }
        }
    }

    private static class Setup {
        private final boolean transparent;
        private final int size;

        public Setup(int size, boolean transparent) {
            this.size = size;
            this.transparent = transparent;
        }

        public int getSize() {
            return size;
        }

        public boolean isTransparent() {
            return transparent;
        }
    }

    private static Setup transitions[] = new Setup[]{
            new Setup(ScreenTileEntity.SIZE_NORMAL, false),
            new Setup(ScreenTileEntity.SIZE_NORMAL, true),
            new Setup(ScreenTileEntity.SIZE_LARGE, false),
            new Setup(ScreenTileEntity.SIZE_LARGE, true),
            new Setup(ScreenTileEntity.SIZE_HUGE, false),
            new Setup(ScreenTileEntity.SIZE_HUGE, true),
    };

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        cycleSizeTranspMode(world, pos);
        return true;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZ_FACING);
    }


    public void cycleSizeTranspMode(World world, BlockPos pos) {
        ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);
        clearInvisibleBlocks(world, pos, state, screenTileEntity.getSize());
        for (int i = 0; i < transitions.length; i++) {
            Setup setup = transitions[i];
            if (setup.isTransparent() == screenTileEntity.isTransparent() && setup.getSize() == screenTileEntity.getSize()) {
                Setup next = transitions[(i + 1) % transitions.length];
                screenTileEntity.setTransparent(next.isTransparent());
                screenTileEntity.setSize(next.getSize());
                setInvisibleBlocks(world, pos, screenTileEntity.getSize());
                break;
            }
        }
    }

    public void cycleSizeMode(World world, BlockPos pos) {
        ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);
        clearInvisibleBlocks(world, pos, state, screenTileEntity.getSize());
        for (int i = 0; i < transitions.length; i++) {
            Setup setup = transitions[i];
            if (setup.isTransparent() == screenTileEntity.isTransparent() && setup.getSize() == screenTileEntity.getSize()) {
                Setup next = transitions[(i + 2) % transitions.length];
                screenTileEntity.setTransparent(next.isTransparent());
                screenTileEntity.setSize(next.getSize());
                setInvisibleBlocks(world, pos, screenTileEntity.getSize());
                break;
            }
        }
    }

    public void cycleTranspMode(World world, BlockPos pos) {
        ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);
        clearInvisibleBlocks(world, pos, state, screenTileEntity.getSize());
        for (int i = 0; i < transitions.length; i++) {
            Setup setup = transitions[i];
            if (setup.isTransparent() == screenTileEntity.isTransparent() && setup.getSize() == screenTileEntity.getSize()) {
                Setup next = transitions[(i % 2) == 0 ? (i + 1) : (i - 1)];
                screenTileEntity.setTransparent(next.isTransparent());
                screenTileEntity.setSize(next.getSize());
                setInvisibleBlocks(world, pos, screenTileEntity.getSize());
                break;
            }
        }
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, PlayerEntity player) {
        ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
        if (!itemStack.isEmpty() && itemStack.getItem() == Items.BLACK_DYE) {   // @Todo 1.14, use tags to get all dyes
            int damage = itemStack.getDamageValue(); // @todo 1.14 don't use damage!
            if (damage < 0) {
                damage = 0;
            } else if (damage > 15) {
                damage = 15;
            }
            DyeColor color = DyeColor.byId(damage);
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(new BlockPos(x, y, z));
            screenTileEntity.setColor(color.getMaterialColor().col); // @todo 1.14
            return true;
        }
        if (player.isShiftKeyDown()) {
            return super.openGui(world, x, y, z, player);
        } else {
            if (world.isClientSide) {
                activateOnClient(world, new BlockPos(x, y, z));
            }
            return true;
        }
    }

    private void activateOnClient(World world, BlockPos pos) {
        RayTraceResult mouseOver = SafeClientTools.getClientMouseOver();
        ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
        if (mouseOver instanceof BlockRayTraceResult) {
            screenTileEntity.hitScreenClient(mouseOver.getLocation().x - pos.getX(), mouseOver.getLocation().y - pos.getY(), mouseOver.getLocation().z - pos.getZ(),
                    ((BlockRayTraceResult) mouseOver).getDirection(), world.getBlockState(pos).getValue(HORIZ_FACING));
        }
    }

    public static final VoxelShape BLOCK_AABB = VoxelShapes.box(0F, 0F, 0F, 1F, 1F, 1F);
    public static final VoxelShape NORTH_AABB = VoxelShapes.box(.01F, .01F, 15F / 16f, .99F, .99F, 1F);
    public static final VoxelShape SOUTH_AABB = VoxelShapes.box(.01F, .01F, 0F, .99F, .99F, 1F / 16f);
    public static final VoxelShape WEST_AABB = VoxelShapes.box(15F / 16f, .01F, .01F, 1F, .99F, .99F);
    public static final VoxelShape EAST_AABB = VoxelShapes.box(0F, .01F, .01F, 1F / 16f, .99F, .99F);
    public static final VoxelShape UP_AABB = VoxelShapes.box(.01F, 0F, .01F, 1F, .99F / 16f, .99F);
    public static final VoxelShape DOWN_AABB = VoxelShapes.box(.01F, 15F / 16f, .01F, .99F, 1F, .99F);

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH) {
            return NORTH_AABB;
        } else if (facing == Direction.SOUTH) {
            return SOUTH_AABB;
        } else if (facing == Direction.WEST) {
            return WEST_AABB;
        } else if (facing == Direction.EAST) {
            return EAST_AABB;
        } else if (facing == Direction.UP) {
            return UP_AABB;
        } else if (facing == Direction.DOWN) {
            return DOWN_AABB;
        } else {
            return BLOCK_AABB;
        }
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    // @todo 1.14
//    @Override
//    public boolean isBlockNormalCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullBlock(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }
//
//    /**
//     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
//     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
//     */
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }

    @Override
    public void setPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity entityLivingBase, @Nonnull ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, entityLivingBase, itemStack);

        if (entityLivingBase instanceof PlayerEntity) {
            // @todo achievements
//            Achievements.trigger((PlayerEntity) entityLivingBase, Achievements.clearVision);
        }
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ScreenTileEntity) {
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) tileEntity;
            if (screenTileEntity.getSize() > ScreenTileEntity.SIZE_NORMAL) {
                setInvisibleBlocks(world, pos, screenTileEntity.getSize());
            }
        }
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof ScreenTileEntity) {
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) te;
            if (screenTileEntity.getSize() > ScreenTileEntity.SIZE_NORMAL) {
                clearInvisibleBlocks(world, pos, state, screenTileEntity.getSize());
            }
        }
        super.onRemove(state, world, pos, newstate, isMoving);
    }
}
