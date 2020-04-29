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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

;

public class ThreeLogicTileEntity extends LogicTileEntity {

    public static final String CMD_SETSTATE = "logic.setState";
    public static final Key<Integer> PARAM_INDEX = new Key<>("index", Type.INTEGER);
    public static final Key<Integer> PARAM_STATE = new Key<>("state", Type.INTEGER);

    private int[] logicTable = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };    // 0 == off, 1 == on, -1 == keep

    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Logic")
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockSetup.CONTAINER_LOGIC.get(), windowId, EmptyContainer.CONTAINER_FACTORY.get(), getPos(), ThreeLogicTileEntity.this)));

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(ThreeLogicTileEntity::new));
    }

    public ThreeLogicTileEntity() {
        super(LogicBlockSetup.TYPE_LOGIC.get());
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

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
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

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SETSTATE.equals(command)) {
            logicTable[params.get(PARAM_INDEX)] = params.get(PARAM_STATE);
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

}

