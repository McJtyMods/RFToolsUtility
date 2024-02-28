package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.tools.AreaType;
import mcjty.rftoolsutility.modules.logic.tools.GroupType;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidBlock;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.GenericItemHandler.no;
import static mcjty.lib.container.SlotDefinition.ghost;

public class SensorTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();
    public static final int SLOT_ITEMMATCH = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(ghost(), SLOT_ITEMMATCH, 154, 24)
            .playerSlots(10, 70));

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/sensor"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(SensorTileEntity::new));
    }

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY).itemValid(no()).build();

    @Cap(type = CapType.CONTAINER)
    private LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Sensor")
            .containerSupplier(container(LogicBlockModule.CONTAINER_SENSOR, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .setupSync(this));

    @GuiValue
    private int number = 0;

    private SensorType sensorType = SensorType.SENSOR_BLOCK;
    @GuiValue
    public static final Value<SensorTileEntity, String> VALUE_TYPE = Value.createEnum("type", SensorType.values(), SensorTileEntity::getSensorType, SensorTileEntity::setSensorType);

    private AreaType areaType = AreaType.AREA_1;
    @GuiValue
    public static final Value<SensorTileEntity, String> VALUE_AREA = Value.createEnum("area", AreaType.values(), SensorTileEntity::getAreaType, SensorTileEntity::setAreaType);

    private GroupType groupType = GroupType.GROUP_ONE;
    @GuiValue
    public static final Value<SensorTileEntity, String> VALUE_GROUP = Value.createEnum("group", GroupType.values(), SensorTileEntity::getGroupType, SensorTileEntity::setGroupType);

    private int checkCounter = 0;
    private AABB cachedBox = null;

    public SensorTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TYPE_SENSOR.get(), pos, state);
    }

    public int getNumber() {
        return number;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
        cachedBox = null;
        setChanged();
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
        cachedBox = null;
        setChanged();
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
        cachedBox = null;
        setChanged();
    }

    @Override
    protected void tickServer() {
        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 10;

        support.setRedstoneState(this, checkSensor() ? 15 : 0);
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public boolean checkSensor() {
        boolean newout;
        LogicFacing facing = LogicSupport.getFacing(level.getBlockState(getBlockPos()));
        Direction inputSide = facing.getInputSide();
        BlockPos newpos = getBlockPos().relative(inputSide);

        newout = switch (sensorType) {
            case SENSOR_BLOCK -> checkBlockOrFluid(newpos, facing, inputSide, this::checkBlock);
            case SENSOR_FLUID -> checkBlockOrFluid(newpos, facing, inputSide, this::checkFluid);
            case SENSOR_GROWTHLEVEL -> checkGrowthLevel(newpos, facing, inputSide);
            case SENSOR_ENTITIES -> checkEntities(newpos, facing, inputSide, Entity.class);
            case SENSOR_PLAYERS -> checkEntities(newpos, facing, inputSide, Player.class);
            case SENSOR_HOSTILE -> checkEntitiesHostile(newpos, facing, inputSide);
            case SENSOR_PASSIVE -> checkEntitiesPassive(newpos, facing, inputSide);
            case SENSOR_ITEMS -> checkEntityItems(newpos, facing, inputSide);
        };
        return newout;
    }

    private boolean checkBlockOrFluid(BlockPos newpos, LogicFacing facing, Direction dir, Function<BlockPos, Boolean> blockChecker) {
        int blockCount = areaType.getBlockCount();
        if (blockCount > 0) {
            Boolean x = checkBlockOrFluidRow(newpos, dir, blockChecker, blockCount);
            if (x != null) {
                return x;
            }
        } else if (blockCount < 0) {
            // Area
            Direction downSide = facing.getSide();
            Direction inputSide = facing.getInputSide();
            Direction rightSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
            Direction leftSide = LogicSlabBlock.rotateRight(downSide, inputSide);

            blockCount = -blockCount;
            Boolean x = checkBlockOrFluidRow(newpos, dir, blockChecker, blockCount);
            if (x != null) {
                return x;
            }

            for (int i = 1 ; i <= (blockCount-1)/2 ; i++) {
                BlockPos p = newpos.relative(leftSide, i);
                x = checkBlockOrFluidRow(p, dir, blockChecker, blockCount);
                if (x != null) {
                    return x;
                }
                p = newpos.relative(rightSide, i);
                x = checkBlockOrFluidRow(p, dir, blockChecker, blockCount);
                if (x != null) {
                    return x;
                }
            }
        }

        return groupType == GroupType.GROUP_ALL;
    }

    private Boolean checkBlockOrFluidRow(BlockPos newpos, Direction dir, Function<BlockPos, Boolean> blockChecker, int count) {
        for (int i = 0; i < count; i++) {
            boolean result = blockChecker.apply(newpos);
            if (result && groupType == GroupType.GROUP_ONE) {
                return true;
            }
            if ((!result) && groupType == GroupType.GROUP_ALL) {
                return false;
            }
            newpos = newpos.relative(dir);
        }
        return null;
    }

    private boolean checkBlock(BlockPos newpos) {
        BlockState state = level.getBlockState(newpos);
        ItemStack matcher = items.getStackInSlot(0);
        if (matcher.isEmpty()) {
            return state.canOcclude();
        }
        ItemStack stack = state.getBlock().getCloneItemStack(level, newpos, state);
        if (!stack.isEmpty()) {
            return matcher.getItem() == stack.getItem();
        } else {
            return matcher.getItem() == state.getBlock().asItem();
        }
    }

    private boolean checkFluid(BlockPos newpos) {
        BlockState state = level.getBlockState(newpos);
        ItemStack matcher = items.getStackInSlot(0);
        Block block = state.getBlock();
        if (matcher.isEmpty()) {
            if (block instanceof LiquidBlock || block instanceof IFluidBlock) {
                return !level.getBlockState(newpos).isAir();
            }

            return false;
        }
        ItemStack stack = block.getCloneItemStack(level, newpos, state);
        Item matcherItem = matcher.getItem();

        FluidStack matcherFluidStack = null;
//        if (matcherItem instanceof IFluidContainerItem) {
//            matcherFluidStack = ((IFluidContainerItem)matcherItem).getFluid(matcher);
//            return checkFluid(block, matcherFluidStack, state, newpos);
//        }
        if (matcherItem instanceof BucketItem) {
            matcherFluidStack = new FluidBucketWrapper(matcher).getFluid();
            return checkFluid(block, matcherFluidStack, state, newpos);
        }

        return false;
    }

    private boolean checkFluid(Block block, FluidStack matcherFluidStack, BlockState state, BlockPos newpos) {
        if (matcherFluidStack == null) {
            return level.getBlockState(newpos).isAir();
        }

        Fluid matcherFluid = matcherFluidStack.getFluid();
        if (matcherFluid == null) {
            return false;
        }

        Block matcherFluidBlock = matcherFluid.defaultFluidState().createLegacyBlock().getBlock();

//        String matcherBlockName = matcherFluidBlock.getUnlocalizedName();
//        String blockName = block.getUnlocalizedName();
//        return blockName.equals(matcherBlockName);
        return matcherFluidBlock == block;
    }

    private boolean checkGrowthLevel(BlockPos newpos, LogicFacing facing, Direction dir) {
        int blockCount = areaType.getBlockCount();
        if (blockCount > 0) {
            Boolean x = checkGrowthLevelRow(newpos, dir, blockCount);
            if (x != null) {
                return x;
            }
        } else if (blockCount < 0) {
            // Area
            Direction downSide = facing.getSide();
            Direction inputSide = facing.getInputSide();
            Direction rightSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
            Direction leftSide = LogicSlabBlock.rotateRight(downSide, inputSide);

            blockCount = -blockCount;
            Boolean x = checkGrowthLevelRow(newpos, dir, blockCount);
            if (x != null) {
                return x;
            }

            for (int i = 1 ; i <= (blockCount-1)/2 ; i++) {
                BlockPos p = newpos.relative(leftSide, i);
                x = checkGrowthLevelRow(p, dir, blockCount);
                if (x != null) {
                    return x;
                }
                p = newpos.relative(rightSide, i);
                x = checkGrowthLevelRow(p, dir, blockCount);
                if (x != null) {
                    return x;
                }
            }
        }
        return groupType == GroupType.GROUP_ALL;
    }

    private Boolean checkGrowthLevelRow(BlockPos newpos, Direction dir, int blockCount) {
        for (int i = 0; i < blockCount; i++) {
            boolean result = checkGrowthLevel(newpos);
            if (result && groupType == GroupType.GROUP_ONE) {
                return true;
            }
            if ((!result) && groupType == GroupType.GROUP_ALL) {
                return false;
            }
            newpos = newpos.relative(dir);
        }
        return null;
    }

    private boolean checkGrowthLevel(BlockPos newpos) {
        BlockState state = level.getBlockState(newpos);
        int pct = 0;
        for (Property<?> property : state.getProperties()) {
            if(!"age".equals(property.getName())) {
                continue;
            }
            if(property.getValueClass() == Integer.class) {
                Property<Integer> integerProperty = (Property<Integer>)property;
                int age = state.getValue(integerProperty);
                int maxAge = Collections.max(integerProperty.getPossibleValues());
                pct = (age * 100) / maxAge;
            }
            break;
        }
        return pct >= number;
    }

    public void invalidateCache() {
        cachedBox = null;
    }

    private AABB getCachedBox(BlockPos pos1, LogicFacing facing, Direction dir) {
        if (cachedBox == null) {
            int n = areaType.getBlockCount();

            if (n > 0) {
                cachedBox = new AABB(pos1);
                if (n > 1) {
                    BlockPos pos2 = pos1.relative(dir, n - 1);
                    cachedBox = cachedBox.minmax(new AABB(pos2));
                }
                cachedBox = cachedBox.expandTowards(.1, .1, .1);
            } else {
                n = -n;
                cachedBox = new AABB(pos1);

                // Area
                Direction downSide = facing.getSide();
                Direction inputSide = facing.getInputSide();
                Direction rightSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
                Direction leftSide = LogicSlabBlock.rotateRight(downSide, inputSide);
                if (n > 1) {
                    BlockPos pos2 = pos1.relative(dir, n - 1);
                    cachedBox = cachedBox.minmax(new AABB(pos2));
                }
                BlockPos pos2 = pos1.relative(leftSide, (n-1)/2);
                cachedBox = cachedBox.minmax(new AABB(pos2));
                pos2 = pos1.relative(rightSide, (n-1)/2);
                cachedBox = cachedBox.minmax(new AABB(pos2));
            }
        }
        return cachedBox;
    }

    private boolean checkEntityItems(BlockPos pos1, LogicFacing facing, Direction dir) {
        List<? extends Entity> entities = level.getEntitiesOfClass(ItemEntity.class, getCachedBox(pos1, facing, dir));
        int cnt = 0;
        for (Entity entity : entities) {
            if (entity instanceof ItemEntity itemEntity) {
                cnt += itemEntity.getItem().getCount();
                if (cnt >= number) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkEntities(BlockPos pos1, LogicFacing facing, Direction dir, Class<? extends Entity> clazz) {
        List<? extends Entity> entities = level.getEntitiesOfClass(clazz, getCachedBox(pos1, facing, dir));
        return entities.size() >= number;
    }

    private boolean checkEntitiesHostile(BlockPos pos1, LogicFacing facing, Direction dir) {
        List<? extends Entity> entities = level.getEntitiesOfClass(PathfinderMob.class, getCachedBox(pos1, facing, dir));
        int cnt = 0;
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                cnt++;
                if (cnt >= number) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkEntitiesPassive(BlockPos pos1, LogicFacing facing, Direction dir) {
        List<? extends Entity> entities = level.getEntitiesOfClass(PathfinderMob.class, getCachedBox(pos1, facing, dir));
        int cnt = 0;
        for (Entity entity : entities) {
            if (entity instanceof Mob && !(entity instanceof Enemy)) {
                cnt++;
                if (cnt >= number) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        support.setPowerOutput(tagCompound.getBoolean("rs") ? 15 : 0);
    }

    @Override
    public void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        number = info.getInt("number");
        sensorType = SensorType.values()[info.getByte("sensor")];
        areaType = AreaType.values()[info.getByte("area")];
        groupType = GroupType.values()[info.getByte("group")];
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
    }

    @Override
    public void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        info.putInt("number", number);
        info.putByte("sensor", (byte) sensorType.ordinal());
        info.putByte("area", (byte) areaType.ordinal());
        info.putByte("group", (byte) groupType.ordinal());
    }

    @Override
    public void rotateBlock(Rotation axis) {
        invalidateCache();
    }

}
