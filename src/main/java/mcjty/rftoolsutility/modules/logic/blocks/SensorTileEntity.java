package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.sync.GuiSync;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LogicFacing;
import mcjty.lib.varia.NamedEnum;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.tools.AreaType;
import mcjty.rftoolsutility.modules.logic.tools.GroupType;
import mcjty.rftoolsutility.modules.logic.tools.SensorType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.ghost;

public class SensorTileEntity extends LogicTileEntity implements ITickableTileEntity {

    public static final String CMD_SETNUMBER = "sensor.setNumber";
    public static final String CMD_SETTYPE = "sensor.setType";
    public static final String CMD_SETAREA = "sensor.setArea";
    public static final String CMD_SETGROUP = "sensor.setGroup";

    public static final String CONTAINER_INVENTORY = "container";
    public static final int SLOT_ITEMMATCH = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(ghost(), CONTAINER_CONTAINER, SLOT_ITEMMATCH, 154, 24)
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
    private NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.CONTAINER)
    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Sensor")
            .containerSupplier((windowId, player) -> new GenericContainer(LogicBlockModule.CONTAINER_SENSOR.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), SensorTileEntity.this))
            .itemHandler(() -> items)
            .setupSync(this));

    @GuiSync
    private int number = 0;
    @GuiSync
    private SensorType sensorType = SensorType.SENSOR_BLOCK;
    @GuiSync
    private AreaType areaType = AreaType.AREA_1;
    @GuiSync
    private GroupType groupType = GroupType.GROUP_ONE;

    private int checkCounter = 0;
    private AxisAlignedBB cachedBox = null;

    public SensorTileEntity() {
        super(LogicBlockModule.TYPE_SENSOR.get());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        cachedBox = null;
        setChanged();
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
    public void tick() {
        if (level.isClientSide) {
            return;
        }

        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 10;

        setRedstoneState(checkSensor() ? 15 : 0);
    }

    public boolean checkSensor() {
        boolean newout;
        LogicFacing facing = getFacing(level.getBlockState(getBlockPos()));
        Direction inputSide = facing.getInputSide();
        BlockPos newpos = getBlockPos().relative(inputSide);

        switch (sensorType) {
            case SENSOR_BLOCK:
                newout = checkBlockOrFluid(newpos, facing, inputSide, this::checkBlock);
                break;
            case SENSOR_FLUID:
                newout = checkBlockOrFluid(newpos, facing, inputSide, this::checkFluid);
                break;
            case SENSOR_GROWTHLEVEL:
                newout = checkGrowthLevel(newpos, facing, inputSide);
                break;
            case SENSOR_ENTITIES:
                newout = checkEntities(newpos, facing, inputSide, Entity.class);
                break;
            case SENSOR_PLAYERS:
                newout = checkEntities(newpos, facing, inputSide, PlayerEntity.class);
                break;
            case SENSOR_HOSTILE:
                newout = checkEntitiesHostile(newpos, facing, inputSide);
                break;
            case SENSOR_PASSIVE:
                newout = checkEntitiesPassive(newpos, facing, inputSide);
                break;
            default:
                newout = false;
        }
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
            if (block instanceof FlowingFluidBlock || block instanceof IFluidBlock) {
                return !block.isAir(state, level, newpos);
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
            return block.isAir(state,  level, newpos);
        }

        Fluid matcherFluid = matcherFluidStack.getFluid();
        if (matcherFluid == null) {
            return false;
        }

        Block matcherFluidBlock = matcherFluid.defaultFluidState().createLegacyBlock().getBlock();
        if (matcherFluidBlock == null) {
            return false;
        }

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

    private AxisAlignedBB getCachedBox(BlockPos pos1, LogicFacing facing, Direction dir) {
        if (cachedBox == null) {
            int n = areaType.getBlockCount();

            if (n > 0) {
                cachedBox = new AxisAlignedBB(pos1);
                if (n > 1) {
                    BlockPos pos2 = pos1.relative(dir, n - 1);
                    cachedBox = cachedBox.minmax(new AxisAlignedBB(pos2));
                }
                cachedBox = cachedBox.expandTowards(.1, .1, .1);
            } else {
                n = -n;
                cachedBox = new AxisAlignedBB(pos1);

                // Area
                Direction downSide = facing.getSide();
                Direction inputSide = facing.getInputSide();
                Direction rightSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
                Direction leftSide = LogicSlabBlock.rotateRight(downSide, inputSide);
                if (n > 1) {
                    BlockPos pos2 = pos1.relative(dir, n - 1);
                    cachedBox = cachedBox.minmax(new AxisAlignedBB(pos2));
                }
                BlockPos pos2 = pos1.relative(leftSide, (n-1)/2);
                cachedBox = cachedBox.minmax(new AxisAlignedBB(pos2));
                pos2 = pos1.relative(rightSide, (n-1)/2);
                cachedBox = cachedBox.minmax(new AxisAlignedBB(pos2));
            }
        }
        return cachedBox;
    }

    private boolean checkEntities(BlockPos pos1, LogicFacing facing, Direction dir, Class<? extends Entity> clazz) {
        List<Entity> entities = level.getEntitiesOfClass(clazz, getCachedBox(pos1, facing, dir));
        return entities.size() >= number;
    }

    private boolean checkEntitiesHostile(BlockPos pos1, LogicFacing facing, Direction dir) {
        List<Entity> entities = level.getEntitiesOfClass(CreatureEntity.class, getCachedBox(pos1, facing, dir));
        int cnt = 0;
        for (Entity entity : entities) {
            if (entity instanceof IMob) {
                cnt++;
                if (cnt >= number) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkEntitiesPassive(BlockPos pos1, LogicFacing facing, Direction dir) {
        List<Entity> entities = level.getEntitiesOfClass(CreatureEntity.class, getCachedBox(pos1, facing, dir));
        int cnt = 0;
        for (Entity entity : entities) {
            if (entity instanceof MobEntity && !(entity instanceof IMob)) {
                cnt++;
                if (cnt >= number) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        powerOutput = tagCompound.getBoolean("rs") ? 15 : 0;
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        number = info.getInt("number");
        sensorType = SensorType.values()[info.getByte("sensor")];
        areaType = AreaType.values()[info.getByte("area")];
        groupType = GroupType.values()[info.getByte("group")];
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("number", number);
        info.putByte("sensor", (byte) sensorType.ordinal());
        info.putByte("area", (byte) areaType.ordinal());
        info.putByte("group", (byte) groupType.ordinal());
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SETAREA.equals(command)) {
            AreaType type = NamedEnum.getEnumByName(params.get(ChoiceLabel.PARAM_CHOICE), AreaType.values());
            setAreaType(type);
            return true;
        } else if (CMD_SETTYPE.equals(command)) {
            SensorType type = NamedEnum.getEnumByName(params.get(ChoiceLabel.PARAM_CHOICE), SensorType.values());
            setSensorType(type);
            return true;
        } else if (CMD_SETGROUP.equals(command)) {
            GroupType type = NamedEnum.getEnumByName(params.get(ChoiceLabel.PARAM_CHOICE), GroupType.values());
            setGroupType(type);
            return true;
        } else if (CMD_SETNUMBER.equals(command)) {
            int number;
            try {
                number = Integer.parseInt(params.get(TextField.PARAM_TEXT));
            } catch (NumberFormatException e) {
                number = 1;
            }
            setNumber(number);
            return true;
        }
        return false;
    }

    @Override
    public void rotateBlock(Rotation axis) {
        invalidateCache();
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }
}
