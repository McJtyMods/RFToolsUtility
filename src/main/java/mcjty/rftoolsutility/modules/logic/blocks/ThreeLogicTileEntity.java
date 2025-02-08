package mcjty.rftoolsutility.modules.logic.blocks;

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
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class ThreeLogicTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    private final int[] logicTable = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };    // 0 == off, 1 == on, -1 == keep

    @Cap(type = CapType.CONTAINER)
    private static final Function<ThreeLogicTileEntity, MenuProvider> SCREEN_CAP = be  -> new DefaultContainerProvider<GenericContainer>("Logic")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_LOGIC, be))
            .shortListener(Sync.integer(() -> be.logicTable[0], v -> be.logicTable[0] = v))
            .shortListener(Sync.integer(() -> be.logicTable[1], v -> be.logicTable[1] = v))
            .shortListener(Sync.integer(() -> be.logicTable[2], v -> be.logicTable[2] = v))
            .shortListener(Sync.integer(() -> be.logicTable[3], v -> be.logicTable[3] = v))
            .shortListener(Sync.integer(() -> be.logicTable[4], v -> be.logicTable[4] = v))
            .shortListener(Sync.integer(() -> be.logicTable[5], v -> be.logicTable[5] = v))
            .shortListener(Sync.integer(() -> be.logicTable[6], v -> be.logicTable[6] = v))
            .shortListener(Sync.integer(() -> be.logicTable[7], v -> be.logicTable[7] = v))
            .setupSync(be);

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/logic"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(ThreeLogicTileEntity::new));
    }

    public ThreeLogicTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TYPE_LOGIC.get(), pos, state);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public int getState(int index) {
        return logicTable[index];
    }

    public void checkRedstone() {
        int s = logicTable[powerLevel];
        if (s == -1) {
            return; // Nothing happens (keep mode)
        }
        support.setRedstoneState(this, s == 1 ? 15 : 0);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        support.setPowerOutput(tag.getBoolean("rs") ? 15 : 0);
    }

    // @todo 1.21 data
//    @Override
//    public void loadInfo(CompoundTag tagCompound) {
//        super.loadInfo(tagCompound);
//        CompoundTag info = tagCompound.getCompound("Info");
//        for (int i = 0 ; i < 8 ; i++) {
//            logicTable[i] = info.getInt("state" + i);
//        }
//    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("rs", support.getPowerOutput() > 0);
    }

    // @todo 1.21 data
//    @Override
//    public void saveInfo(CompoundTag tagCompound) {
//        super.saveInfo(tagCompound);
//        CompoundTag info = getOrCreateInfo(tagCompound);
//        for (int i = 0 ; i < 8 ; i++) {
//            info.putInt("state" + i, logicTable[i]);
//        }
//    }

    public static final Key<Integer> PARAM_INDEX = new Key<>("index", Type.INTEGER);
    public static final Key<Integer> PARAM_STATE = new Key<>("state", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_SETSTATE = Command.<ThreeLogicTileEntity>create("logic.setState",
        (te, player, params) -> {
            te.logicTable[params.get(PARAM_INDEX)] = params.get(PARAM_STATE);
            te.setChanged();
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

