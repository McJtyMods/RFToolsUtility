package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;

import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class AnalogTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

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
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Analog")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_ANALOG, this))
            .setupSync(this));

    public AnalogTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TYPE_ANALOG.get(), pos, state);
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
    public void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        mulEqual = info.getFloat("mulE");
        mulLess = info.getFloat("mulL");
        mulGreater = info.getFloat("mulG");
        addEqual = info.getInt("addE");
        addLess = info.getInt("addL");
        addGreater = info.getInt("addG");
    }

    @Override
    public void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag info = getOrCreateInfo(tagCompound);
        info.putFloat("mulE", mulEqual);
        info.putFloat("mulL", mulLess);
        info.putFloat("mulG", mulGreater);
        info.putInt("addE", addEqual);
        info.putInt("addL", addLess);
        info.putInt("addG", addGreater);
    }

    private static final Set<BlockPos> loopDetector = new HashSet<>();

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (loopDetector.add(pos)) {
            try {
                LogicFacing facing = LogicSupport.getFacing(state);
                Direction downSide = facing.getSide();
                Direction inputSide = facing.getInputSide();
                Direction rightSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
                Direction leftSide = LogicSlabBlock.rotateRight(downSide, inputSide);

                int outputStrength;
                int inputStrength = support.getInputStrength(world, pos, inputSide);
                int inputLeft = support.getInputStrength(world, pos, leftSide);
                int inputRight = support.getInputStrength(world, pos, rightSide);
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

                int oldPower = support.getPowerOutput();
                support.setRedstoneState(this, outputStrength);
                if (oldPower != outputStrength) {
                    world.updateNeighborsAt(pos, getBlockState().getBlock());
                }
            } finally {
                loopDetector.remove(pos);
            }
        }
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }
}
