package mcjty.rftoolsutility.modules.screen.blocks;

import com.mojang.serialization.MapCodec;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;

public class ScreenControllerTileEntity extends TickingTileEntity {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0)
            .playerSlots(10, 70));

    public static final String COMPONENT_NAME = "screen_controller";

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, ScreenConfiguration.CONTROLLER_MAXENERGY.get(), ScreenConfiguration.CONTROLLER_RECEIVEPERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<ScreenControllerTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    private final IInfusable infusable = new DefaultInfusable(ScreenControllerTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<ScreenControllerTileEntity, IInfusable> INFUSABLE_CAP = tile -> tile.infusable;

    @Cap(type = CapType.CONTAINER)
    private static final Function<ScreenControllerTileEntity, MenuProvider> screenHandler = be -> new DefaultContainerProvider<GenericContainer>("Screen Controller")
            .containerSupplier(container(ScreenModule.CONTAINER_SCREEN_CONTROLLER, CONTAINER_FACTORY, be))
            .energyHandler(() -> be.energyStorage)
            .setupSync(be);

    private List<BlockPos> connectedScreens = new ArrayList<>();
    private int tickCounter = 20;

    public ScreenControllerTileEntity(BlockPos pos, BlockState state) {
        super(ScreenModule.SCREEN_CONTROLLER.be().get(), pos, state);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        HolderLookup.Provider provider = input.lookup();
        int[] xes = tag.getIntArray("screensx").orElseGet(() -> new int[0]);
        int[] yes = tag.getIntArray("screensy").orElseGet(() -> new int[0]);
        int[] zes = tag.getIntArray("screensz").orElseGet(() -> new int[0]);
        connectedScreens.clear();
        for (int i = 0 ; i < xes.length ; i++) {
            connectedScreens.add(new BlockPos(xes[i], yes[i], zes[i]));
        }
        energyStorage.setEnergy(tag.getLongOr("Energy", 0L));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        int[] xes = new int[connectedScreens.size()];
        int[] yes = new int[connectedScreens.size()];
        int[] zes = new int[connectedScreens.size()];
        for (int i = 0 ; i < connectedScreens.size() ; i++) {
            BlockPos c = connectedScreens.get(i);
            xes[i] = c.getX();
            yes[i] = c.getY();
            zes[i] = c.getZ();
        }
        tag.putIntArray("screensx", xes);
        tag.putIntArray("screensy", yes);
        tag.putIntArray("screensz", zes);
        tag.putLong("Energy", energyStorage.getEnergy());
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }

    @Override
    protected void tickServer() {
        tickCounter--;
        if (tickCounter > 0) {
            return;
        }
        tickCounter = 20;
        long rf = energyStorage.getEnergy();
        long rememberRf = rf;
        boolean fixesAreNeeded = false;
        for (BlockPos c : connectedScreens) {
            BlockEntity te = level.getBlockEntity(c);
            if (te instanceof ScreenTileEntity screen) {
                int rfModule = screen.getTotalRfPerTick() * 20;

                if (rfModule > rf) {
                    screen.setPower(false);
                } else {
                    rf -= rfModule;
                    screen.setPower(true);
                }
            } else {
                // This coordinate is no longer a valid screen. We need to update.
                fixesAreNeeded = true;
            }
        }
        if (rf < rememberRf) {
            energyStorage.consumeEnergy(rememberRf - rf);
        }

        if (fixesAreNeeded) {
            List<BlockPos> newScreens = new ArrayList<>();
            for (BlockPos c : connectedScreens) {
                BlockEntity te = level.getBlockEntity(c);
                if (te instanceof ScreenTileEntity) {
                    newScreens.add(c);
                }
            }
            connectedScreens = newScreens;
            setChanged();
        }
    }

    @ServerCommand
    public static final Command<?> CMD_SCAN = Command.<ScreenControllerTileEntity>create("scan", (te, player, params) -> te.scan());

    private void scan() {
        detach();
        float factor = infusable.getInfusedFactor();
        int radius = 32 + (int) (factor * 32);

        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        for (int y = yCoord - radius ; y <= yCoord + radius ; y++) {
            if (y >= level.getMinY() && y < level.getMaxY() + 1) {
                for (int x = xCoord - radius; x <= xCoord + radius; x++) {
                    for (int z = zCoord - radius; z <= zCoord + radius; z++) {
                        BlockPos spos = new BlockPos(x, y, z);
                        if (level.getBlockState(spos).getBlock() instanceof ScreenBlock) {
                            BlockEntity te = level.getBlockEntity(spos);
                            if (te instanceof ScreenTileEntity ste) {
                                if (!ste.isConnected() && ste.isControllerNeeded()) {
                                    connectedScreens.add(spos);
                                    ste.setConnected(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        setChanged();
    }

    @ServerCommand
    public static final Command<?> CMD_DETACH = Command.<ScreenControllerTileEntity>create("detach", (te, player, params) -> te.detach());

    public void detach() {
        for (BlockPos c : connectedScreens) {
            BlockEntity te = level.getBlockEntity(c);
            if (te instanceof ScreenTileEntity screen) {
                screen.setPower(false);
                screen.setConnected(false);
            }
        }

        connectedScreens.clear();
        setChanged();
    }

    public List<BlockPos> getConnectedScreens() {
        return connectedScreens;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        detach();
        super.preRemoveSideEffects(pos, state);
    }
}
