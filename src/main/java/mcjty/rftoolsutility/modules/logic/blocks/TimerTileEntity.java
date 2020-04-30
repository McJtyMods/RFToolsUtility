package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.various.TickOrderHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class TimerTileEntity extends LogicTileEntity implements ITickableTileEntity, TickOrderHandler.ICheckStateServer {

    public static final String CMD_SETDELAY = "timer.setDelay";
    public static final String CMD_SETPAUSES = "timer.setPauses";

    // For pulse detection.
    private boolean prevIn = false;

    private int delay = 20;
    private int timer = 0;
    private boolean redstonePauses = false;

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Timer")
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockSetup.CONTAINER_TIMER.get(), windowId, EmptyContainer.CONTAINER_FACTORY.get(), getPos(), TimerTileEntity.this)));

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(TimerTileEntity::new));
    }
    
    public TimerTileEntity() {
        super(LogicBlockSetup.TYPE_TIMER.get());    
    }

    public int getDelay() {
        return delay;
    }

    public int getTimer() {
        return timer;
    }

    public boolean getRedstonePauses() {
        return redstonePauses;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        timer = delay;
        markDirtyClient();
    }

    public void setRedstonePauses(boolean redstonePauses) {
        this.redstonePauses = redstonePauses;
        if(redstonePauses && powerLevel > 0) {
            timer = delay;
        }
        markDirtyClient();
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            TickOrderHandler.queueTimer(this);
        }
    }

    @Override
    public void checkStateServer() {
        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;

        markDirty();

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

        setRedstoneState(newout);
    }

    @Override
    public DimensionType getDimension() {
        return world.getDimension().getType();
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        powerOutput = tagCompound.getBoolean("rs") ? 15 : 0;
        prevIn = tagCompound.getBoolean("prevIn");
        timer = tagCompound.getInt("timer");
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        delay = info.getInt("delay");
        redstonePauses = info.getBoolean("redstonePauses");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        tagCompound.putBoolean("prevIn", prevIn);
        tagCompound.putInt("timer", timer);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("delay", delay);
        info.putBoolean("redstonePauses", redstonePauses);
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SETDELAY.equals(command)) {
            String text = params.get(TextField.PARAM_TEXT);
            int delay;
            try {
                delay = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                delay = 1;
            }
            setDelay(delay);
            return true;
        } else if (CMD_SETPAUSES.equals(command)) {
            Boolean on = params.get(ToggleButton.PARAM_ON);
            setRedstonePauses(on);
            return true;
        }
        return false;
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
