package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsbase.api.screens.IModuleProvider;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class ScreenBlock extends BaseBlock {

    public static final DirectionProperty HORIZ_FACING = DirectionProperty.create("horizfacing", Direction.Plane.HORIZONTAL);

    private final boolean creative;

    public ScreenBlock(BlockEntityType.BlockEntitySupplier<BlockEntity> supplier, boolean creative) {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(HORIZ_FACING, context.getPlayer().getDirection().getOpposite());
    }

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

    public InteractionResult activate(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        return use(state, world, pos, player, hand, result);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rot) {
        // Doesn't make sense to rotate a potentially 3x3 screen,
        // and is incompatible with our special wrench actions.
        return state;
    }


    @Override
    public void attack(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player) {
        if (world.isClientSide) {
            HitResult mouseOver = SafeClientTools.getClientMouseOver();
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
            if (mouseOver instanceof BlockHitResult blockResult) {
                screenTileEntity.hitScreenClient(mouseOver.getLocation().x - pos.getX(), mouseOver.getLocation().y - pos.getY(), mouseOver.getLocation().z - pos.getZ(),
                        blockResult.getDirection(), world.getBlockState(pos).getValue(HORIZ_FACING));
            }
        }
    }

    private void setInvisibleBlockSafe(Level world, BlockPos pos, int dx, int dy, int dz, Direction facing) {
        int yy = pos.getY() + dy;
        if (yy < world.getMinBuildHeight() || yy >= world.getMaxBuildHeight()) {
            return;
        }
        int xx = pos.getX() + dx;
        int zz = pos.getZ() + dz;
        BlockPos posO = new BlockPos(xx, yy, zz);
        if (world.isEmptyBlock(posO)) {
            world.setBlock(posO, ScreenModule.SCREEN_HIT.get().defaultBlockState().setValue(BlockStateProperties.FACING, facing), 3);
            ScreenHitTileEntity screenHitTileEntity = (ScreenHitTileEntity) world.getBlockEntity(posO);
            screenHitTileEntity.setRelativeLocation(-dx, -dy, -dz);
        }
    }

    private void setInvisibleBlocks(Level world, BlockPos pos, int size) {
        BlockState state = world.getBlockState(pos);
        Direction facing = state.getValue(BlockStateProperties.FACING);
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
                        switch (horizontalFacing) {
                            case NORTH -> setInvisibleBlockSafe(world, pos, -i, 0, -j, facing);
                            case SOUTH -> setInvisibleBlockSafe(world, pos, i, 0, j, facing);
                            case WEST -> setInvisibleBlockSafe(world, pos, -i, 0, j, facing);
                            case EAST -> setInvisibleBlockSafe(world, pos, i, 0, -j, facing);
                        }
                    } else if (facing == Direction.DOWN) {
                        switch (horizontalFacing) {
                            case NORTH -> setInvisibleBlockSafe(world, pos, -i, 0, j, facing);
                            case SOUTH -> setInvisibleBlockSafe(world, pos, i, 0, -j, facing);
                            case WEST -> setInvisibleBlockSafe(world, pos, i, 0, j, facing);
                            case EAST -> setInvisibleBlockSafe(world, pos, -i, 0, -j, facing);
                        }
                    }
                }
            }
        }
    }

    private void clearInvisibleBlockSafe(Level world, BlockPos pos) {
        if (pos.getY() < world.getMinBuildHeight() || pos.getY() >= world.getMaxBuildHeight()) {
            return;
        }
        if (world.getBlockState(pos).getBlock() == ScreenModule.SCREEN_HIT.get()) {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    private void clearInvisibleBlocks(Level world, BlockPos pos, BlockState state, int size) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
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
                        switch (horizontalFacing) {
                            case NORTH -> clearInvisibleBlockSafe(world, pos.offset(-i, 0, -j));
                            case SOUTH -> clearInvisibleBlockSafe(world, pos.offset(i, 0, j));
                            case WEST -> clearInvisibleBlockSafe(world, pos.offset(-i, 0, j));
                            case EAST -> clearInvisibleBlockSafe(world, pos.offset(i, 0, -j));
                        }
                    } else if (facing == Direction.DOWN) {
                        switch (horizontalFacing) {
                            case NORTH -> clearInvisibleBlockSafe(world, pos.offset(-i, 0, j));
                            case SOUTH -> clearInvisibleBlockSafe(world, pos.offset(i, 0, -j));
                            case WEST -> clearInvisibleBlockSafe(world, pos.offset(i, 0, j));
                            case EAST -> clearInvisibleBlockSafe(world, pos.offset(-i, 0, -j));
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

    private static final Setup[] transitions = new Setup[]{
            new Setup(ScreenTileEntity.SIZE_NORMAL, false),
            new Setup(ScreenTileEntity.SIZE_NORMAL, true),
            new Setup(ScreenTileEntity.SIZE_LARGE, false),
            new Setup(ScreenTileEntity.SIZE_LARGE, true),
            new Setup(ScreenTileEntity.SIZE_HUGE, false),
            new Setup(ScreenTileEntity.SIZE_HUGE, true),
    };

    @Override
    protected boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        cycleSizeTranspMode(world, pos);
        return true;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZ_FACING);
    }


    public void cycleSizeTranspMode(Level world, BlockPos pos) {
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

    public void cycleSizeMode(Level world, BlockPos pos) {
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

    public void cycleTranspMode(Level world, BlockPos pos) {
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
    protected boolean openGui(Level world, int x, int y, int z, Player player) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemStack.isEmpty() && itemStack.getItem() == Items.BLACK_DYE) {   // @Todo 1.14, use tags to get all dyes
            int damage = itemStack.getDamageValue(); // @todo 1.14 don't use damage!
            if (damage < 0) {
                damage = 0;
            } else if (damage > 15) {
                damage = 15;
            }
            DyeColor color = DyeColor.byId(damage);
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(new BlockPos(x, y, z));
            screenTileEntity.setColor(color.getMapColor().col); // @todo 1.14
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

    private void activateOnClient(Level world, BlockPos pos) {
        HitResult mouseOver = SafeClientTools.getClientMouseOver();
        ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos);
        if (mouseOver instanceof BlockHitResult hitResult) {
            screenTileEntity.hitScreenClient(mouseOver.getLocation().x - pos.getX(), mouseOver.getLocation().y - pos.getY(), mouseOver.getLocation().z - pos.getZ(),
                    hitResult.getDirection(), world.getBlockState(pos).getValue(HORIZ_FACING));
        }
    }

    public static final VoxelShape BLOCK_AABB = Shapes.box(0F, 0F, 0F, 1F, 1F, 1F);
    public static final VoxelShape NORTH_AABB = Shapes.box(.01F, .01F, 15F / 16f, .99F, .99F, 1F);
    public static final VoxelShape SOUTH_AABB = Shapes.box(.01F, .01F, 0F, .99F, .99F, 1F / 16f);
    public static final VoxelShape WEST_AABB = Shapes.box(15F / 16f, .01F, .01F, 1F, .99F, .99F);
    public static final VoxelShape EAST_AABB = Shapes.box(0F, .01F, .01F, 1F / 16f, .99F, .99F);
    public static final VoxelShape UP_AABB = Shapes.box(.01F, 0F, .01F, 1F, .99F / 16f, .99F);
    public static final VoxelShape DOWN_AABB = Shapes.box(.01F, 15F / 16f, .01F, .99F, 1F, .99F);

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.FACING)) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            case UP -> UP_AABB;
            case DOWN -> DOWN_AABB;
        };
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity entityLivingBase, @Nonnull ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, entityLivingBase, itemStack);

        if (entityLivingBase instanceof Player) {
            // @todo achievements
//            Achievements.trigger((PlayerEntity) entityLivingBase, Achievements.clearVision);
        }
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ScreenTileEntity screen) {
            if (screen.getSize() > ScreenTileEntity.SIZE_NORMAL) {
                setInvisibleBlocks(world, pos, screen.getSize());
            }
        }
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ScreenTileEntity screen) {
            if (screen.getSize() > ScreenTileEntity.SIZE_NORMAL) {
                clearInvisibleBlocks(world, pos, state, screen.getSize());
            }
        }
        super.onRemove(state, world, pos, newstate, isMoving);
    }
}
