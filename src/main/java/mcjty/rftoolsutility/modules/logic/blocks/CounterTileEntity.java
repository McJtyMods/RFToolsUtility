package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.typed.Type;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.CounterData;
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

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class CounterTileEntity extends GenericTileEntity {

    private final LogicSupport support = new LogicSupport();

    // For pulse detection.
    private boolean prevIn = false;

    @GuiValue
    public static final Value<CounterTileEntity, Integer> VALUE_COUNTER = Value.create("counter", Type.INTEGER, CounterTileEntity::getCounter, CounterTileEntity::setCounter);

    @GuiValue
    public static final Value<CounterTileEntity, Integer> VALUE_CURRENT = Value.create("current", Type.INTEGER, CounterTileEntity::getCurrent, CounterTileEntity::setCurrent);

    @Cap(type = CapType.CONTAINER)
    private static final Function<CounterTileEntity, MenuProvider> screenHandler = be -> new DefaultContainerProvider<GenericContainer>("Counter")
            .containerSupplier(DefaultContainerProvider.empty(LogicBlockModule.CONTAINER_COUNTER, be))
            .setupSync(be);

    public CounterTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.COUNTER.be().get(), pos, state);
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/counter"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(CounterTileEntity::new));
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public int getCounter() {
        CounterData data = getData(LogicBlockModule.COUNTER_DATA);
        return data.counter();
    }

    public int getCurrent() {
        CounterData data = getData(LogicBlockModule.COUNTER_DATA);
        return data.current();
    }

    public void setCounter(int counter) {
        CounterData data = getData(LogicBlockModule.COUNTER_DATA);
        if (counter != data.counter()) {
            data = data.withCounter(counter).withCurrent(0);
            setData(LogicBlockModule.COUNTER_DATA, data);
            support.setRedstoneState(this, 0);
        }
    }

    public void setCurrent(int current) {
        CounterData data = getData(LogicBlockModule.COUNTER_DATA);
        data = data.withCurrent(current);
        setData(LogicBlockModule.COUNTER_DATA, data);
    }

    protected void update() {
        if (level.isClientSide) {
            return;
        }
        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;

        int newout = 0;

        if (pulse) {
            CounterData data = getData(LogicBlockModule.COUNTER_DATA);
            int current = data.current();
            int counter = data.counter();
            current++;
            if (current >= counter) {
                current = 0;
                newout = 15;
            }
            data = data.withCurrent(current);
            setData(LogicBlockModule.COUNTER_DATA, data);
            support.setRedstoneState(this, newout);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        support.setPowerOutput(tag.getBoolean("rs") ? 15 : 0);
        prevIn = tag.getBoolean("prevIn");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("rs", support.getPowerOutput() > 0);
        tag.putBoolean("prevIn", prevIn);
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
        update();
    }
}
