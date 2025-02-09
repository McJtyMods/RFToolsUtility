package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;

import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class TimerTileEntity extends TickingTileEntity implements TickOrderHandler.IOrderTicker {

    private final LogicSupport support = new LogicSupport();

    // For pulse detection.
    private boolean prevIn = false;

    private int timer = 0;

    @GuiValue
    private int delay = 20;

    @GuiValue(name = "pauses")
    private boolean redstonePauses = false;

    @Cap(type = CapType.CONTAINER)
    private static final Function<TimerTileEntity, MenuProvider> screenHandler = be -> new DefaultContainerProvider<GenericContainer>("Timer")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_TIMER, be))
            .setupSync(be);

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/timer"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(TimerTileEntity::new));
    }
    
    public TimerTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TYPE_TIMER.get(), pos, state);
    }

    public int getTimer() {
        return timer;
    }

    @Override
    protected void tickServer() {
        TickOrderHandler.queue(this);
    }

    @Override
    public TickOrderHandler.Rank getRank() {
        return TickOrderHandler.Rank.RANK_3;
    }

    @Override
    public void tickOnServer() {
        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;

        setChanged();

        if (pulse) {
            timer = delay;
        }

        int newout;

        if(!redstonePauses || !prevIn) {
            timer--;
        }
        if (timer <= 0) {
            timer = delay;
            newout = 15;
        } else {
            newout = 0;
        }

        support.setRedstoneState(this, newout);
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        support.setPowerOutput(tag.getBoolean("rs") ? 15 : 0);
        prevIn = tag.getBoolean("prevIn");
        timer = tag.getInt("timer");
    }

    // @todo 1.21 data
//    @Override
//    public void loadInfo(CompoundTag tagCompound) {
//        super.loadInfo(tagCompound);
//        CompoundTag info = tagCompound.getCompound("Info");
//        delay = info.getInt("delay");
//        redstonePauses = info.getBoolean("redstonePauses");
//    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("rs", support.getPowerOutput() > 0);
        tag.putBoolean("prevIn", prevIn);
        tag.putInt("timer", timer);
    }

    // @todo 1.21 data
//    @Override
//    public void saveInfo(CompoundTag tagCompound) {
//        super.saveInfo(tagCompound);
//        CompoundTag info = getOrCreateInfo(tagCompound);
//        info.putInt("delay", delay);
//        info.putBoolean("redstonePauses", redstonePauses);
//    }

}
