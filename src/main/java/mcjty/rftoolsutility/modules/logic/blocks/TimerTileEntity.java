package mcjty.rftoolsutility.modules.logic.blocks;

import com.mojang.serialization.MapCodec;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Type;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.TimerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.rftoolsutility.setup.Registration.standardBlockProperties;

public class TimerTileEntity extends TickingTileEntity implements TickOrderHandler.IOrderTicker {

    private final LogicSupport support = new LogicSupport();

    // For pulse detection.
    private boolean prevIn = false;

    private int timer = 0;

    @GuiValue
    public static final Value<TimerTileEntity, Integer> VALUE_DELAY = Value.create("delay", Type.INTEGER, TimerTileEntity::getDelay, TimerTileEntity::setDelay);

    @GuiValue(name = "pauses")
    public static final Value<TimerTileEntity, Boolean> VALUE_PAUSES = Value.create("pauses", Type.BOOLEAN, TimerTileEntity::isRedstonePauses, TimerTileEntity::setRedstonePauses);

    @Cap(type = CapType.CONTAINER)
    private static final Function<TimerTileEntity, MenuProvider> screenHandler = be -> new DefaultContainerProvider<GenericContainer>("Timer")
            .containerSupplier(empty(LogicBlockModule.CONTAINER_TIMER, be))
            .setupSync(be);

    public static LogicSlabBlock createBlock(BlockBehaviour.Properties properties) {
        return new LogicSlabBlock(new BlockBuilder().properties(standardBlockProperties(properties))
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:logic/timer"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(TimerTileEntity::new));
    }
    
    public TimerTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.TIMER.be().get(), pos, state);
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

    public int getDelay() {
        TimerData data = getData(LogicBlockModule.TIMER_DATA);
        return data.delay();
    }

    public void setDelay(int delay) {
        TimerData data = getData(LogicBlockModule.TIMER_DATA);
        data = data.withDelay(delay);
        setData(LogicBlockModule.TIMER_DATA, data);
    }

    public boolean isRedstonePauses() {
        TimerData data = getData(LogicBlockModule.TIMER_DATA);
        return data.redstonePauses();
    }

    public void setRedstonePauses(boolean redstonePauses) {
        TimerData data = getData(LogicBlockModule.TIMER_DATA);
        data = data.withRedstonePauses(redstonePauses);
        setData(LogicBlockModule.TIMER_DATA, data);
    }

    @Override
    public void tickOnServer() {
        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;

        setChanged();

        TimerData data = getData(LogicBlockModule.TIMER_DATA);

        if (pulse) {
            timer = data.delay();
        }

        int newout;

        if(!data.redstonePauses() || !prevIn) {
            timer--;
        }
        if (timer <= 0) {
            timer = data.delay();
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
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        HolderLookup.Provider provider = input.lookup();
        support.setPowerOutput(tag.getBooleanOr("rs", false) ? 15 : 0);
        prevIn = tag.getBooleanOr("prevIn", false);
        timer = tag.getIntOr("timer", 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        tag.putBoolean("rs", support.getPowerOutput() > 0);
        tag.putBoolean("prevIn", prevIn);
        tag.putInt("timer", timer);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }
}
