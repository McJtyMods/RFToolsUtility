package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class AnalogTileEntity extends LogicTileEntity {

    public static final String CMD_UPDATE = "analog.update";
    public static final Key<Double> PARAM_MUL_EQ = new Key<>("mul_eq", Type.DOUBLE);
    public static final Key<Double> PARAM_MUL_LESS = new Key<>("mul_less", Type.DOUBLE);
    public static final Key<Double> PARAM_MUL_GT = new Key<>("mul_gt", Type.DOUBLE);
    public static final Key<Integer> PARAM_ADD_EQ = new Key<>("add_eq", Type.INTEGER);
    public static final Key<Integer> PARAM_ADD_LESS = new Key<>("add_less", Type.INTEGER);
    public static final Key<Integer> PARAM_ADD_GT = new Key<>("add_gt", Type.INTEGER);

    private float mulEqual = 1.0f;
    private float mulLess = 1.0f;
    private float mulGreater = 1.0f;

    private int addEqual = 0;
    private int addLess = 0;
    private int addGreater = 0;

    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Analog")
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockSetup.CONTAINER_ANALOG.get(), windowId, EmptyContainer.CONTAINER_FACTORY, getPos(), AnalogTileEntity.this)));

    public AnalogTileEntity() {
        super(LogicBlockSetup.TYPE_ANALOG.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(AnalogTileEntity::new));
    }

    public int getPowerLevel() {
        return powerLevel;
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
    }

    public int getAddLess() {
        return addLess;
    }

    public void setAddLess(int addLess) {
        this.addLess = addLess;
    }

    public int getAddGreater() {
        return addGreater;
    }

    public void setAddGreater(int addGreater) {
        this.addGreater = addGreater;
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

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_UPDATE.equals(command)) {
            mulEqual = params.get(PARAM_MUL_EQ).floatValue();
            mulLess = params.get(PARAM_MUL_LESS).floatValue();
            mulGreater = params.get(PARAM_MUL_GT).floatValue();
            addEqual = params.get(PARAM_ADD_EQ);
            addLess = params.get(PARAM_ADD_LESS);
            addGreater = params.get(PARAM_ADD_GT);
            markDirtyClient();
            checkRedstone(world, pos);
            return true;
        }
        return false;
    }

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

                int oldPower = getPowerLevel();
                setPowerInput(outputStrength);
                if (oldPower != outputStrength) {
                    world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
                }
            } finally {
                loopDetector.remove(pos);
            }
        }
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == getFacing(state).getInputSide()) {
            return getPowerLevel();
        } else {
            return 0;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
