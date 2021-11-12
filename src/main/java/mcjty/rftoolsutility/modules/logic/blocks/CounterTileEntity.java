package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
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

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.rftoolsutility.modules.logic.LogicBlockModule.TYPE_COUNTER;

public class CounterTileEntity extends LogicTileEntity {

    public static final String CMD_SETCOUNTER = "counter.setCounter";
    public static final String CMD_SETCURRENT = "counter.setCurrent";

    // For pulse detection.
    private boolean prevIn = false;

    private int counter = 1;
    private int current = 0;

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Analog")
            .integerListener(Sync.integer(this::getCounter, this::setCounter))
            .integerListener(Sync.integer(this::getCurrent, this::setCurrent))
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockModule.CONTAINER_COUNTER.get(), windowId, ContainerFactory.EMPTY.get(), getBlockPos(), CounterTileEntity.this)));

    public CounterTileEntity() {
        super(TYPE_COUNTER.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/counter"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(CounterTileEntity::new));
    }

    public int getCounter() {
        return counter;
    }

    public int getCurrent() {
        return current;
    }

    public void setCounter(int counter) {
        this.counter = counter;
        current = 0;
        setChanged();
    }

    public void setCurrent(int current) {
        this.current = current;
        setChanged();
    }

    protected void update() {
        if (level.isClientSide) {
            return;
        }
        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;

        int newout = 0;

        if (pulse) {
            current++;
            if (current >= counter) {
                current = 0;
                newout = 15;
            }

            setChanged();
            setRedstoneState(newout);
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        powerOutput = tagCompound.getBoolean("rs") ? 15 : 0;
        prevIn = tagCompound.getBoolean("prevIn");
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        counter = info.getInt("counter");
        if (counter == 0) {
            counter = 1;
        }
        current = info.getInt("current");
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        tagCompound.putBoolean("prevIn", prevIn);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("counter", counter);
        info.putInt("current", current);
    }

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        super.checkRedstone(world, pos);
        update();
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SETCOUNTER.equals(command)) {
            int counter;
            try {
                counter = Integer.parseInt(params.get(TextField.PARAM_TEXT));
            } catch (NumberFormatException e) {
                counter = 0;
            }
            setCounter(counter);
            return true;
        } else if (CMD_SETCURRENT.equals(command)) {
            int current;
            try {
                current = Integer.parseInt(params.get(TextField.PARAM_TEXT));
            } catch (NumberFormatException e) {
                current = 0;
            }
            setCurrent(current);
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
