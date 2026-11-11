package mcjty.rftoolsutility.modules.logic.blocks;

import com.mojang.serialization.MapCodec;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.*;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.ThreeLogicData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.rftoolsutility.setup.Registration.standardBlockProperties;

public class ThreeLogicTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    @Cap(type = CapType.CONTAINER)
    private static final Function<ThreeLogicTileEntity, MenuProvider> SCREEN_CAP = be  -> new DefaultContainerProvider<GenericContainer>("Logic")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_LOGIC, be))
            .data(LogicBlockModule.THREELOGIC_DATA, ThreeLogicData.STREAM_CODEC, ThreeLogicData.CODEC)
            .setupSync(be);

    public static LogicSlabBlock createBlock(BlockBehaviour.Properties properties) {
        return new LogicSlabBlock(new BlockBuilder().properties(standardBlockProperties(properties))
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:logic/logic"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(ThreeLogicTileEntity::new));
    }

    public ThreeLogicTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.LOGIC.be().get(), pos, state);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public int getState(int index) {
        ThreeLogicData data = getData(LogicBlockModule.THREELOGIC_DATA);
        return data.logicTable()[index];
    }

    public void checkRedstone() {
        ThreeLogicData data = getData(LogicBlockModule.THREELOGIC_DATA);
        int s = data.logicTable()[powerLevel];
        if (s == -1) {
            return; // Nothing happens (keep mode)
        }
        support.setRedstoneState(this, s == 1 ? 15 : 0);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        HolderLookup.Provider provider = input.lookup();
        support.setPowerOutput(tag.getBooleanOr("rs", false) ? 15 : 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        tag.putBoolean("rs", support.getPowerOutput() > 0);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }

    public static final Key<Integer> PARAM_INDEX = new Key<>("index", Type.INTEGER);
    public static final Key<Integer> PARAM_STATE = new Key<>("state", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_SETSTATE = Command.<ThreeLogicTileEntity>create("logic.setState",
        (te, player, params) -> {
            ThreeLogicData data = te.getData(LogicBlockModule.THREELOGIC_DATA);
            int[] logicTable = data.logicTableCopy();
            logicTable[params.get(PARAM_INDEX)] = params.get(PARAM_STATE);
            te.setData(LogicBlockModule.THREELOGIC_DATA, data.withLogicTable(logicTable));
            te.checkRedstone(te.level, te.worldPosition);
        });

    private static final Set<BlockPos> loopDetector = new HashSet<>();

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (loopDetector.add(pos)) {
            try {
                LogicFacing facing = LogicSupport.getFacing(state);
                Direction downSide = facing.getSide();
                Direction inputSide = facing.getInputSide();
                Direction leftSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
                Direction rightSide = LogicSlabBlock.rotateRight(downSide, inputSide);

                int powered1 = support.getInputStrength(world, pos, leftSide) > 0 ? 1 : 0;
                int powered2 = support.getInputStrength(world, pos, inputSide) > 0 ? 2 : 0;
                int powered3 = support.getInputStrength(world, pos, rightSide) > 0 ? 4 : 0;
                setPowerInput(powered1 + powered2 + powered3);
                checkRedstone();
            } finally {
                loopDetector.remove(pos);
            }
        }
    }
}
