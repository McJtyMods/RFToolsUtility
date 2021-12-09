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
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

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
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Timer")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_TIMER, this))
            .setupSync(this));

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/timer"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(TimerTileEntity::new));
    }
    
    public TimerTileEntity() {
        super(LogicBlockModule.TYPE_TIMER.get());
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
    public void checkRedstone(World world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        support.setPowerOutput(tagCompound.getBoolean("rs") ? 15 : 0);
        prevIn = tagCompound.getBoolean("prevIn");
        timer = tagCompound.getInt("timer");
    }

    @Override
    public void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        delay = info.getInt("delay");
        redstonePauses = info.getBoolean("redstonePauses");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
        tagCompound.putBoolean("prevIn", prevIn);
        tagCompound.putInt("timer", timer);
    }

    @Override
    public void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("delay", delay);
        info.putBoolean("redstonePauses", redstonePauses);
    }

}
