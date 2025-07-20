package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.SequencerData;
import mcjty.rftoolsutility.modules.logic.tools.SequencerMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class SequencerTileEntity extends TickingTileEntity implements TickOrderHandler.IOrderTicker {

    private final LogicSupport support = new LogicSupport();

    private int currentStep = -1;

    public static final Key<Integer> PARAM_BIT = new Key<>("bit", Type.INTEGER);
    public static final Key<Boolean> PARAM_CHOICE = new Key<>("choice", Type.BOOLEAN);

    @GuiValue
    public static final Value<SequencerTileEntity, String> VALUE_MODE = Value.createEnum("mode", SequencerMode.values(), SequencerTileEntity::getMode, SequencerTileEntity::setMode);

    @GuiValue
    public static final Value<SequencerTileEntity, Boolean> VALUE_ENDSTATE = Value.create("endstate", Type.BOOLEAN, SequencerTileEntity::getEndState, SequencerTileEntity::setEndState);
    @GuiValue
    public static final Value<SequencerTileEntity, Integer> VALUE_STEPCOUNT = Value.create("stepcount", Type.INTEGER, SequencerTileEntity::getStepcount, SequencerTileEntity::setStepcount);
    @GuiValue
    public static final Value<SequencerTileEntity, Integer> VALUE_DELAY = Value.create("delay", Type.INTEGER, SequencerTileEntity::getDelay, SequencerTileEntity::setDelay);

    // For pulse detection.
    private boolean prevIn = false;
    private int timer = 0;

    @Cap(type = CapType.CONTAINER)
    private static final Function<SequencerTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Sequencer")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_SEQUENCER, be))
            .data(LogicBlockModule.SEQUENCER_DATA, SequencerData.STREAM_CODEC, SequencerData.CODEC)
            .setupSync(be);

    public boolean getEndState() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        return data.endstate();
    }

    public void setEndState(boolean endstate) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        data = data.withEndstate(endstate);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
    }

    public int getStepcount() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        return data.stepcount();
    }

    public void setStepcount(int stepcount) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        data = data.withStepcount(stepcount);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
    }

    public int getDelay() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        return data.delay();
    }

    public void setDelay(int delay) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        data = data.withDelay(delay);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:logic/sequencer"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(SequencerTileEntity::new));
    }

    public SequencerTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.SEQUENCER.be().get(), pos, state);
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public SequencerMode getMode() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        return data.sequencerMode();
    }

    public void setMode(SequencerMode mode) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        data = data.withSequencerMode(mode);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
        switch (mode) {
            case MODE_ONCE1, MODE_ONCE2, MODE_LOOP3, MODE_LOOP4 -> currentStep = -1;
            case MODE_LOOP1, MODE_LOOP2, MODE_STEP -> currentStep = 0;
        }
        setChanged();
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public boolean getCycleBit(int bit) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        return ((data.bits() >> bit) & 1) == 1;
    }

    public long getCycleBits() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        return data.bits();
    }

    public void setCycleBit(int bit, boolean flag) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        long cycleBits = data.bits();
        if (flag) {
            cycleBits |= 1L << bit;
        } else {
            cycleBits &= ~(1L << bit);
        }
        data = data.withBits(cycleBits);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
    }

    public void flipCycleBits() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        data = data.withBits(data.bits() ^ ~0L);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
    }

    public void clearCycleBits() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        data = data.withBits(0L);
        setData(LogicBlockModule.SEQUENCER_DATA, data);
    }

    @Override
    protected void tickServer() {
        TickOrderHandler.queue(this);
    }

    @Override
    public TickOrderHandler.Rank getRank() {
        return TickOrderHandler.Rank.RANK_4;
    }

    @Override
    public void tickOnServer() {
        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;

        if (pulse) {
            handlePulse();
        }

        setChanged();
        timer--;
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        if (timer <= 0) {
            timer = data.delay();
            support.setRedstoneState(this, checkOutput() ? 15 : 0);
            handleCycle(powerLevel > 0);
        } else if (timer > data.delay()) {
            timer = data.delay();
        }
    }

    public boolean checkOutput() {
        return currentStep == -1 ? getData(LogicBlockModule.SEQUENCER_DATA).endstate() : getCycleBit(currentStep);
    }

    /**
     * Handle a cycle step.
     *
     * @param redstone true if there is a redstone signal
     */
    private void handleCycle(boolean redstone) {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        switch (data.sequencerMode()) {
            case MODE_ONCE1:
            case MODE_ONCE2:
                if (currentStep != -1) {
                    nextStepAndStop();
                }
                break;
            case MODE_LOOP1:
                nextStep();
                break;
            case MODE_LOOP2:
                nextStep();
                break;
            case MODE_LOOP3:
                if (redstone) {
                    nextStep();
                }
                break;
            case MODE_LOOP4:
                if (redstone) {
                    nextStep();
                } else {
                    currentStep = -1;
                }
                break;
            case MODE_STEP:
                break;
        }
    }

    /**
     * Handle the arrival of a new redstone pulse.
     */
    private void handlePulse() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        switch (data.sequencerMode()) {
            case MODE_ONCE1:
                // If we're not doing a cycle then we start one now. Otherwise we do nothing.
                if (currentStep == -1) {
                    currentStep = 0;
                }
                break;
            case MODE_ONCE2:
                // If we're not doing a cycle then we start one now. Otherwise we restart the cycle..
                currentStep = 0;
                break;
            case MODE_LOOP1:
                // Ignore signals
                break;
            case MODE_LOOP2:
                // Set cycle to the start.
                currentStep = 0;
                break;
            case MODE_LOOP3:
            case MODE_LOOP4:
                // Ignore pulses. We just work on redstone signal.
                break;
            case MODE_STEP:
                // Go to next step.
                nextStep();
                break;
        }
    }

    private void nextStep() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        currentStep++;
        if (currentStep >= data.stepcount()) {
            currentStep = 0;
        }
    }

    private void nextStepAndStop() {
        SequencerData data = getData(LogicBlockModule.SEQUENCER_DATA);
        currentStep++;
        if (currentStep >= data.stepcount()) {
            currentStep = -1;
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        support.setPowerOutput(tag.getBoolean("rs") ? 15 : 0);
        currentStep = tag.getInt("step");
        prevIn = tag.getBoolean("prevIn");
        timer = tag.getInt("timer");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("rs", support.getPowerOutput() > 0);
        tag.putInt("step", currentStep);
        tag.putBoolean("prevIn", prevIn);
        tag.putInt("timer", timer);
    }

    @ServerCommand
    public static final Command<?> CMD_FLIPBITS = Command.<SequencerTileEntity>create("sequencer.flipBits",
            (te, player, params) -> te.flipCycleBits());
    @ServerCommand
    public static final Command<?> CMD_CLEARBITS = Command.<SequencerTileEntity>create("sequencer.clearBits",
            (te, player, params) -> te.clearCycleBits());
    @ServerCommand
    public static final Command<?> CMD_SETBIT = Command.<SequencerTileEntity>create("sequencer.setBit",
            (te, player, params) -> te.setCycleBit(params.get(PARAM_BIT), params.get(PARAM_CHOICE)));
}
