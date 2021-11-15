package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.DefaultAction;
import mcjty.lib.bindings.IAction;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ComputerScreenModule;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;

import static mcjty.rftoolsutility.modules.screen.ScreenModule.TYPE_SCREEN_CONTROLLER;

public class ScreenControllerTileEntity extends GenericTileEntity implements ITickableTileEntity { // implements IPeripheral {

    public static final String ACTION_SCAN = "scan";
    public static final String ACTION_DETACH = "detach";

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0)
            .playerSlots(10, 70));

    @Override
    public IAction[] getActions() {
        return new IAction[] {
                new DefaultAction(ACTION_SCAN, this::scan),
                new DefaultAction(ACTION_DETACH, this::detach),
        };
    }

    public static final String COMPONENT_NAME = "screen_controller";

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, ScreenConfiguration.CONTROLLER_MAXENERGY.get(), ScreenConfiguration.CONTROLLER_RECEIVEPERTICK.get());

    @Cap(type = CapType.INFUSABLE)
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(ScreenControllerTileEntity.this));

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Screen Controller")
            .containerSupplier((windowId,player) -> new GenericContainer(ScreenModule.CONTAINER_SCREEN_CONTROLLER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), ScreenControllerTileEntity.this))
            .energyHandler(() -> energyStorage));

    private List<BlockPos> connectedScreens = new ArrayList<>();
    private int tickCounter = 20;

    public ScreenControllerTileEntity() {
        super(TYPE_SCREEN_CONTROLLER.get());
    }

//    @Override
//    @Optional.Method(modid = "ComputerCraft")
//    public String getType() {
//        return COMPONENT_NAME;
//    }
//
//    @Override
//    @Optional.Method(modid = "ComputerCraft")
//    public String[] getMethodNames() {
//        return new String[] { "getScreenCount", "getScreenIndex", "getScreenCoordinate", "addText", "setText", "clearText" };
//    }
//
//    @Override
//    @Optional.Method(modid = "ComputerCraft")
//    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
//        switch (method) {
//            case 0: return new Object[] { connectedScreens.size() };
//            case 1: return getScreenIndex(new Coordinate(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue()));
//            case 2: Coordinate c = connectedScreens.get(((Double) arguments[0]).intValue()); return new Object[] { c.getX(), c.getY(), c.getZ() };
//            case 3: return addText((String) arguments[0], (String) arguments[1], ((Double) arguments[2]).intValue());
//            case 4: return setText((String) arguments[0], (String) arguments[1], ((Double) arguments[2]).intValue());
//            case 5: return clearText((String) arguments[0]);
//        }
//        return new Object[0];
//    }
//
//    @Override
//    @Optional.Method(modid = "ComputerCraft")
//    public void attach(IComputerAccess computer) {
//
//    }
//
//    @Override
//    @Optional.Method(modid = "ComputerCraft")
//    public void detach(IComputerAccess computer) {
//
//    }
//
//    @Override
//    @Optional.Method(modid = "ComputerCraft")
//    public boolean equals(IPeripheral other) {
//        return false;
//    }

//    @Override
//    @Optional.Method(modid = "opencomputers")
//    public String getComponentName() {
//        return COMPONENT_NAME;
//    }
//
//    @Callback(doc = "Get the amount of screens controlled by this controller", getter = true)
//    @Optional.Method(modid = "opencomputers")
//    public Object[] getScreenCount(Context context, Arguments args) {
//        return new Object[] { connectedScreens.size() };
//    }
//
//    @Callback(doc = "Get a table with coordinates (every coordinate is a table indexed with 'x', 'y', and 'z') for all connected screens", getter = true)
//    @Optional.Method(modid = "opencomputers")
//    public Object[] getScreens(Context context, Arguments args) {
//        List<Map<String,Integer>> result = new ArrayList<>();
//        for (BlockPos screen : connectedScreens) {
//            Map<String,Integer> coordinate = new HashMap<>();
//            coordinate.put("x", screen.getX());
//            coordinate.put("y", screen.getY());
//            coordinate.put("z", screen.getZ());
//            result.add(coordinate);
//        }
//
//        return new Object[] { result };
//    }

//    @Callback(doc = "Given a screen coordinate (table indexed by 'x', 'y', and 'z') return the index of that screen", getter = true)
//    @Optional.Method(modid = "opencomputers")
//    public Object[] getScreenIndex(Context context, Arguments args) throws Exception {
//        Map screen = args.checkTable(0);
//        if (!screen.containsKey("x") || !screen.containsKey("y") || !screen.containsKey("z")) {
//            throw new IllegalArgumentException("Screen map doesn't contain the right x,y,z coordinate!");
//        }
//        BlockPos recC = new BlockPos(((Double) screen.get("x")).intValue(), ((Double) screen.get("y")).intValue(), ((Double) screen.get("z")).intValue());
//        return getScreenIndex(recC);
//    }

    private Object[] getScreenIndex(BlockPos coordinate) {
        int i = 0;
        for (BlockPos connectedScreen : connectedScreens) {
            if (connectedScreen.equals(coordinate)) {
                return new Object[] { i };
            }
            i++;
        }

        return null;
    }

//    @Callback(doc = "Given a screen index return the coordinate (table indexed by 'x', 'y', and 'z') of that screen", getter = true)
//    @Optional.Method(modid = "opencomputers")
//    public Object[] getScreenCoordinate(Context context, Arguments args) throws Exception {
//        int index = args.checkInteger(0);
//        if (index < 0 || index >= connectedScreens.size()) {
//            throw new IllegalArgumentException("Screen index out of range!");
//        }
//        BlockPos screen = connectedScreens.get(index);
//        Map<String,Integer> coordinate = new HashMap<>();
//        coordinate.put("x", screen.getX());
//        coordinate.put("y", screen.getY());
//        coordinate.put("z", screen.getZ());
//
//        return new Object[] { coordinate };
//    }


//    @Callback(doc = "Add text to all screens listening to the given 'tag'. Parameters are: 'tag', 'text' and 'color' (RGB value)")
//    @Optional.Method(modid = "opencomputers")
//    public Object[] addText(Context context, Arguments args) {
//        String tag = args.checkString(0);
//        String text = args.checkString(1);
//        int color = args.checkInteger(2);
//
//        return addText(tag, text, color);
//    }

//    @Callback(doc = "Set text to all screens listening to the given 'tag'. Parameters are: 'tag', 'text' and 'color' (RGB value)")
//    @Optional.Method(modid = "opencomputers")
//    public Object[] setText(Context context, Arguments args) {
//        String tag = args.checkString(0);
//        String text = args.checkString(1);
//        int color = args.checkInteger(2);
//
//        clearText(tag);
//        return addText(tag, text, color);
//    }

    private Object[] setText(String tag, String text, int color) {
        clearText(tag);
        return addText(tag, text, color);
    }

    private Object[] addText(String tag, String text, int color) {
        for (BlockPos screen : connectedScreens) {
            TileEntity te = level.getBlockEntity(screen);
            if (te instanceof ScreenTileEntity) {
                ScreenTileEntity screenTileEntity = (ScreenTileEntity) te;
                List<ComputerScreenModule> computerScreenModules = screenTileEntity.getComputerModules(tag);
                if (computerScreenModules != null) {
                    for (ComputerScreenModule screenModule : computerScreenModules) {
                        screenModule.addText(text, color);
                    }
                }
            }
        }
        return null;
    }

//    @Callback(doc = "Clear text to all screens listening to the given 'tag'. The 'tag' is the only parameter")
//    @Optional.Method(modid = "opencomputers")
//    public Object[] clearText(Context context, Arguments args) {
//        String tag = args.checkString(0);
//
//        return clearText(tag);
//    }

    private Object[] clearText(String tag) {
        for (BlockPos screen : connectedScreens) {
            TileEntity te = level.getBlockEntity(screen);
            if (te instanceof ScreenTileEntity) {
                ScreenTileEntity screenTileEntity = (ScreenTileEntity) te;
                List<ComputerScreenModule> computerScreenModules = screenTileEntity.getComputerModules(tag);
                if (computerScreenModules != null) {
                    for (ComputerScreenModule screenModule : computerScreenModules) {
                        screenModule.clearText();
                    }
                }
            }
        }
        return null;
    }

//    @Callback(doc = "Get a table of all tags supported by all connected screens", getter = true)
//    @Optional.Method(modid = "opencomputers")
//    public Object[] getTags(Context context, Arguments args) {
//        List<String> tags = new ArrayList<>();
//        for (BlockPos screen : connectedScreens) {
//            TileEntity te = world.getTileEntity(screen);
//            if (te instanceof ScreenTileEntity) {
//                ScreenTileEntity screenTileEntity = (ScreenTileEntity) te;
//                tags.addAll(screenTileEntity.getTags());
//            }
//        }
//        return new Object[] { tags };
//    }


    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
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
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
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
        return tagCompound;
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        tickCounter--;
        if (tickCounter > 0) {
            return;
        }
        tickCounter = 20;
        long rf = energyStorage.getEnergy();
        long rememberRf = rf;
        boolean fixesAreNeeded = false;
        for (BlockPos c : connectedScreens) {
            TileEntity te = level.getBlockEntity(c);
            if (te instanceof ScreenTileEntity) {
                ScreenTileEntity screenTileEntity = (ScreenTileEntity) te;
                int rfModule = screenTileEntity.getTotalRfPerTick() * 20;

                if (rfModule > rf) {
                    screenTileEntity.setPower(false);
                } else {
                    rf -= rfModule;
                    screenTileEntity.setPower(true);
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
                TileEntity te = level.getBlockEntity(c);
                if (te instanceof ScreenTileEntity) {
                    newScreens.add(c);
                }
            }
            connectedScreens = newScreens;
            setChanged();
        }
    }

    private void scan() {
        detach();
        float factor = infusableHandler.map(IInfusable::getInfusedFactor).orElse(0.0f);
        int radius = 32 + (int) (factor * 32);

        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        for (int y = yCoord - radius ; y <= yCoord + radius ; y++) {
            if (y >= 0 && y < 256) {
                for (int x = xCoord - radius; x <= xCoord + radius; x++) {
                    for (int z = zCoord - radius; z <= zCoord + radius; z++) {
                        BlockPos spos = new BlockPos(x, y, z);
                        if (level.getBlockState(spos).getBlock() instanceof ScreenBlock) {
                            TileEntity te = level.getBlockEntity(spos);
                            if (te instanceof ScreenTileEntity) {
                                ScreenTileEntity ste = (ScreenTileEntity)te;
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

    public void detach() {
        for (BlockPos c : connectedScreens) {
            TileEntity te = level.getBlockEntity(c);
            if (te instanceof ScreenTileEntity) {
                ((ScreenTileEntity) te).setPower(false);
                ((ScreenTileEntity) te).setConnected(false);
            }
        }

        connectedScreens.clear();
        setChanged();
    }

    public List<BlockPos> getConnectedScreens() {
        return connectedScreens;
    }
}
