package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.LogicFacing;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class ThreeLogicTileEntity extends LogicTileEntity {

    private int[] logicTable = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };    // 0 == off, 1 == on, -1 == keep

    @Cap(type = CapType.CONTAINER)
    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Logic")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_LOGIC, this))
            .shortListener(Sync.integer(() -> logicTable[0], v -> logicTable[0] = v))
            .shortListener(Sync.integer(() -> logicTable[1], v -> logicTable[1] = v))
            .shortListener(Sync.integer(() -> logicTable[2], v -> logicTable[2] = v))
            .shortListener(Sync.integer(() -> logicTable[3], v -> logicTable[3] = v))
            .shortListener(Sync.integer(() -> logicTable[4], v -> logicTable[4] = v))
            .shortListener(Sync.integer(() -> logicTable[5], v -> logicTable[5] = v))
            .shortListener(Sync.integer(() -> logicTable[6], v -> logicTable[6] = v))
            .shortListener(Sync.integer(() -> logicTable[7], v -> logicTable[7] = v))
            .setupSync(this)
    );

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/logic"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(ThreeLogicTileEntity::new));
    }

    public ThreeLogicTileEntity() {
        super(LogicBlockModule.TYPE_LOGIC.get());
    }

    public int getState(int index) {
        return logicTable[index];
    }

    public void checkRedstone() {
        int s = logicTable[powerLevel];
        if (s == -1) {
            return; // Nothing happens (keep mode)
        }
        setRedstoneState(s == 1 ? 15 : 0);
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
        for (int i = 0 ; i < 8 ; i++) {
            logicTable[i] = info.getInt("state" + i);
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        for (int i = 0 ; i < 8 ; i++) {
            info.putInt("state" + i, logicTable[i]);
        }
    }

    public static final Key<Integer> PARAM_INDEX = new Key<>("index", Type.INTEGER);
    public static final Key<Integer> PARAM_STATE = new Key<>("state", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_SETSTATE = Command.<ThreeLogicTileEntity>create("logic.setState",
        (te, player, params) -> {
            te.logicTable[params.get(PARAM_INDEX)] = params.get(PARAM_STATE);
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
                Direction leftSide = LogicSlabBlock.rotateLeft(downSide, inputSide);
                Direction rightSide = LogicSlabBlock.rotateRight(downSide, inputSide);

                int powered1 = getInputStrength(world, pos, leftSide) > 0 ? 1 : 0;
                int powered2 = getInputStrength(world, pos, inputSide) > 0 ? 2 : 0;
                int powered3 = getInputStrength(world, pos, rightSide) > 0 ? 4 : 0;
                setPowerInput(powered1 + powered2 + powered3);
                checkRedstone();
            } finally {
                loopDetector.remove(pos);
            }
        }
    }
}

