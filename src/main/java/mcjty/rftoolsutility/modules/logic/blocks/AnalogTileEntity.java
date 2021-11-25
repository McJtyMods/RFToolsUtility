package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class AnalogTileEntity extends LogicTileEntity {

    @GuiValue(name = "mul_eq")
    private float mulEqual = 1.0f;
    @GuiValue(name = "mul_less")
    private float mulLess = 1.0f;
    @GuiValue(name = "mul_greater")
    private float mulGreater = 1.0f;

    @GuiValue(name = "add_eq")
    private int addEqual = 0;
    @GuiValue(name = "add_less")
    private int addLess = 0;
    @GuiValue(name = "add_greater")
    private int addGreater = 0;

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Analog")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_ANALOG, this))
            .setupSync(this));

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
                    outputStrength = (int) (inputStrength * mulEqual + addEqual);
                } else if (inputLeft < inputRight) {
                    outputStrength = (int) (inputStrength * mulLess + addLess);
                } else {
                    outputStrength = (int) (inputStrength * mulGreater + addGreater);
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
