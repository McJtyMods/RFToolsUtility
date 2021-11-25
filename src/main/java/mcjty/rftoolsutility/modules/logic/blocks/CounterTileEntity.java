package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.rftoolsutility.modules.logic.LogicBlockModule.TYPE_COUNTER;

public class CounterTileEntity extends LogicTileEntity {

    // For pulse detection.
    private boolean prevIn = false;

    private int counter = 1;
    private int current = 0;

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Analog")
            .integerListener(Sync.integer(this::getCounter, this::setCounter))
            .integerListener(Sync.integer(this::getCurrent, this::setCurrent))
            .containerSupplier((windowId,player) -> new GenericContainer(LogicBlockModule.CONTAINER_COUNTER, windowId, ContainerFactory.EMPTY, this)));

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

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
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

    @ServerCommand
    public static final Command<?> CMD_SETCOUNTER = Command.<CounterTileEntity>create("counter.setCounter",
            (te, playerEntity, params) -> {
                try {
                    te.setCounter(Integer.parseInt(params.get(TextField.PARAM_TEXT)));
                } catch (NumberFormatException e) {
                    te.setCounter(0);
                }
            });

    @ServerCommand
    public static final Command<?> CMD_SETCURRENT = Command.<CounterTileEntity>create("counter.setCurrent",
            (te, playerEntity, params) -> {
                try {
                    te.setCurrent(Integer.parseInt(params.get(TextField.PARAM_TEXT)));
                } catch (NumberFormatException e) {
                    te.setCurrent(0);
                }
            });
}
