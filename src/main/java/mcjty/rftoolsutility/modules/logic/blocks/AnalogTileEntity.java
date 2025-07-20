package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.AnalogData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class AnalogTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    @GuiValue(name = "mul_eq")
    public static final Value<AnalogTileEntity, Float> MUL_EQUAL = Value.create("mul_eq", Type.FLOAT, t -> t.getAnalogData().mulEqual(), (t, v) -> t.setAnalogData(t.getAnalogData().withMulEqual(v)));
    @GuiValue(name = "mul_less")
    public static final Value<AnalogTileEntity, Float> MUL_LESS = Value.create("mul_less", Type.FLOAT, t -> t.getAnalogData().mulLess(), (t, v) -> t.setAnalogData(t.getAnalogData().withMulLess(v)));
    @GuiValue(name = "mul_greater")
    public static final Value<AnalogTileEntity, Float> MUL_GREATER = Value.create("mul_greater", Type.FLOAT, t -> t.getAnalogData().mulGreater(), (t, v) -> t.setAnalogData(t.getAnalogData().withMulGreater(v)));

    @GuiValue(name = "add_eq")
    public static final Value<AnalogTileEntity, Integer> ADD_EQUAL = Value.create("add_eq", Type.INTEGER, t -> t.getAnalogData().addEqual(), (t, v) -> t.setAnalogData(t.getAnalogData().withAddEqual(v)));
    @GuiValue(name = "add_less")
    public static final Value<AnalogTileEntity, Integer> ADD_LESS = Value.create("add_less", Type.INTEGER, t -> t.getAnalogData().addLess(), (t, v) -> t.setAnalogData(t.getAnalogData().withAddLess(v)));
    @GuiValue(name = "add_greater")
    public static final Value<AnalogTileEntity, Integer> ADD_GREATER = Value.create("add_greater", Type.INTEGER, t -> t.getAnalogData().addGreater(), (t, v) -> t.setAnalogData(t.getAnalogData().withAddGreater(v)));

    @Cap(type = CapType.CONTAINER)
    private static final Function<AnalogTileEntity, MenuProvider> screenHandler = be -> new DefaultContainerProvider<GenericContainer>("Analog")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_ANALOG, be))
            .setupSync(be);

    public AnalogTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.ANALOG.be().get(), pos, state);
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:logic/analog"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(AnalogTileEntity::new));
    }

    public AnalogData getAnalogData() {
        return getData(LogicBlockModule.ANALOG_DATA);
    }

    public void setAnalogData(AnalogData data) {
        setData(LogicBlockModule.ANALOG_DATA, data);
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

                AnalogData data = getAnalogData();
                int outputStrength;
                int inputStrength = support.getInputStrength(world, pos, inputSide);
                int inputLeft = support.getInputStrength(world, pos, leftSide);
                int inputRight = support.getInputStrength(world, pos, rightSide);
                if (inputLeft == inputRight) {
                    outputStrength = (int) (inputStrength * data.mulEqual() + data.addEqual());
                } else if (inputLeft < inputRight) {
                    outputStrength = (int) (inputStrength * data.mulLess() + data.addLess());
                } else {
                    outputStrength = (int) (inputStrength * data.mulGreater() + data.addGreater());
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
