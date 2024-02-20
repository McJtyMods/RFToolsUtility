package mcjty.rftoolsutility.modules.screen.blocks;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.rftoolsutility.modules.screen.ScreenModule.TYPE_SCREEN_CONTROLLER;

public class ScreenControllerTileEntity extends TickingTileEntity {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0)
            .playerSlots(10, 70));

    public static final String COMPONENT_NAME = "screen_controller";

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, ScreenConfiguration.CONTROLLER_MAXENERGY.get(), ScreenConfiguration.CONTROLLER_RECEIVEPERTICK.get());

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusableHandler = new DefaultInfusable(ScreenControllerTileEntity.this);

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Screen Controller")
            .containerSupplier(container(ScreenModule.CONTAINER_SCREEN_CONTROLLER, CONTAINER_FACTORY,this))
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    private List<BlockPos> connectedScreens = new ArrayList<>();
    private int tickCounter = 20;

    public ScreenControllerTileEntity(BlockPos pos, BlockState state) {
        super(TYPE_SCREEN_CONTROLLER.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        int[] xes = tagCompound.getIntArray("screensx");
        int[] yes = tagCompound.getIntArray("screensy");
        int[] zes = tagCompound.getIntArray("screensz");
        connectedScreens.clear();
        for (int i = 0 ; i < xes.length ; i++) {
            connectedScreens.add(new BlockPos(xes[i], yes[i], zes[i]));
        }
        energyStorage.setEnergy(tagCompound.getLong("Energy"));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        int[] xes = new int[connectedScreens.size()];
        int[] yes = new int[connectedScreens.size()];
        int[] zes = new int[connectedScreens.size()];
        for (int i = 0 ; i < connectedScreens.size() ; i++) {
            BlockPos c = connectedScreens.get(i);
            xes[i] = c.getX();
            yes[i] = c.getY();
            zes[i] = c.getZ();
        }
        tagCompound.putIntArray("screensx", xes);
        tagCompound.putIntArray("screensy", yes);
        tagCompound.putIntArray("screensz", zes);
        tagCompound.putLong("Energy", energyStorage.getEnergy());
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
        float factor = infusableHandler.getInfusedFactor();
        int radius = 32 + (int) (factor * 32);

        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        for (int y = yCoord - radius ; y <= yCoord + radius ; y++) {
            if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
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
}
