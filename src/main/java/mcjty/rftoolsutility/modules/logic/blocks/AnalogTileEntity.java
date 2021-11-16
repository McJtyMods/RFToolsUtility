package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.LogicFacing;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class AnalogTileEntity extends LogicTileEntity {

    private float mulEqual = 1.0f;
    private float mulLess = 1.0f;
    private float mulGreater = 1.0f;

    private int addEqual = 0;
    private int addLess = 0;
    private int addGreater = 0;

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Analog")
            .dataListener(Sync.flt(new ResourceLocation(RFToolsUtility.MODID, "eq"), this::getMulEqual, this::setMulEqual))
            .dataListener(Sync.flt(new ResourceLocation(RFToolsUtility.MODID, "less"), this::getMulLess, this::setMulLess))
            .dataListener(Sync.flt(new ResourceLocation(RFToolsUtility.MODID, "gt"), this::getMulGreater, this::setMulGreater))
            .integerListener(Sync.integer(this::getAddEqual, this::setAddEqual))
            .integerListener(Sync.integer(this::getAddLess, this::setAddLess))
            .integerListener(Sync.integer(this::getAddGreater, this::setAddGreater))
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockModule.CONTAINER_ANALOG.get(), windowId, ContainerFactory.EMPTY.get(), getBlockPos(), AnalogTileEntity.this)));

    public AnalogTileEntity() {
        super(LogicBlockModule.TYPE_ANALOG.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/analog"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(AnalogTileEntity::new));
    }

    public float getMulEqual() {
        return mulEqual;
    }

    public void setMulEqual(float mulEqual) {
        this.mulEqual = mulEqual;
        markDirtyQuick();
    }

    public float getMulLess() {
        return mulLess;
    }

    public void setMulLess(float mulLess) {
        this.mulLess = mulLess;
        markDirtyQuick();
    }

    public float getMulGreater() {
        return mulGreater;
    }

    public void setMulGreater(float mulGreater) {
        this.mulGreater = mulGreater;
        markDirtyQuick();
    }

    public int getAddEqual() {
        return addEqual;
    }

    public void setAddEqual(int addEqual) {
        this.addEqual = addEqual;
        markDirtyQuick();
    }

    public int getAddLess() {
        return addLess;
    }

    public void setAddLess(int addLess) {
        this.addLess = addLess;
        markDirtyQuick();
    }

    public int getAddGreater() {
        return addGreater;
    }

    public void setAddGreater(int addGreater) {
        this.addGreater = addGreater;
        markDirtyQuick();
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        mulEqual = info.getFloat("mulE");
        mulLess = info.getFloat("mulL");
        mulGreater = info.getFloat("mulG");
        addEqual = info.getInt("addE");
        addLess = info.getInt("addL");
        addGreater = info.getInt("addG");
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putFloat("mulE", mulEqual);
        info.putFloat("mulL", mulLess);
        info.putFloat("mulG", mulGreater);
        info.putInt("addE", addEqual);
        info.putInt("addL", addLess);
        info.putInt("addG", addGreater);
    }

    public static final Key<Double> PARAM_MUL_EQ = new Key<>("mul_eq", Type.DOUBLE);
    public static final Key<Double> PARAM_MUL_LESS = new Key<>("mul_less", Type.DOUBLE);
    public static final Key<Double> PARAM_MUL_GT = new Key<>("mul_gt", Type.DOUBLE);
    public static final Key<Integer> PARAM_ADD_EQ = new Key<>("add_eq", Type.INTEGER);
    public static final Key<Integer> PARAM_ADD_LESS = new Key<>("add_less", Type.INTEGER);
    public static final Key<Integer> PARAM_ADD_GT = new Key<>("add_gt", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_UPDATE = Command.<AnalogTileEntity>create("analog.update",
            (te, playerEntity, params) -> {
                te.mulEqual = params.get(PARAM_MUL_EQ).floatValue();
                te.mulLess = params.get(PARAM_MUL_LESS).floatValue();
                te.mulGreater = params.get(PARAM_MUL_GT).floatValue();
                te.addEqual = params.get(PARAM_ADD_EQ);
                te.addLess = params.get(PARAM_ADD_LESS);
                te.addGreater = params.get(PARAM_ADD_GT);
                te.setChanged();
                te.checkRedstone(te.level, te.worldPosition);
            });

    private static Set<BlockPos> loopDetector = new HashSet<>();

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (loopDetector.add(pos)) {
            try {
                LogicFacing facing = getFacing(state);
                Direction downSide = facing.getSide();
                Direction inputSide = facing.getInputSide();
                Direction rightSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
                Direction leftSide = LogicSlabBlock.rotateRight(downSide, inputSide);

                int outputStrength;
                int inputStrength = getInputStrength(world, pos, inputSide);
                int inputLeft = getInputStrength(world, pos, leftSide);
                int inputRight = getInputStrength(world, pos, rightSide);
                if (inputLeft == inputRight) {
                    outputStrength = (int) (inputStrength * getMulEqual() + getAddEqual());
                } else if (inputLeft < inputRight) {
                    outputStrength = (int) (inputStrength * getMulLess() + getAddLess());
                } else {
                    outputStrength = (int) (inputStrength * getMulGreater() + getAddGreater());
                }
                if (outputStrength > 15) {
                    outputStrength = 15;
                } else if (outputStrength < 0) {
                    outputStrength = 0;
                }

                int oldPower = getPowerOutput();
                setRedstoneState(outputStrength);
                if (oldPower != outputStrength) {
                    world.updateNeighborsAt(pos, getBlockState().getBlock());
                }
            } finally {
                loopDetector.remove(pos);
            }
        }
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == getFacing(state).getInputSide()) {
            return getPowerOutput();
        } else {
            return 0;
        }
    }
}
