package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ResultCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.*;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.NbtSanitizerModuleGuiBuilder;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataBoolean;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataInteger;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataString;
import mcjty.rftoolsutility.modules.screen.modules.ComputerScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ScreenModuleHelper;
import mcjty.rftoolsutility.modules.screen.modulesclient.TextClientScreenModule;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.rftoolsutility.modules.screen.ScreenModule.TYPE_SCREEN;

public class ScreenTileEntity extends GenericTileEntity implements ITickableTileEntity {

    // Client side data for CMD_SCREEN_INFO
    public List<String> infoReceived = Collections.emptyList();

    @GuiValue
    public static final Value<?, Boolean> VALUE_BRIGHT = Value.<ScreenTileEntity, Boolean>create("bright", Type.BOOLEAN, ScreenTileEntity::isBright, ScreenTileEntity::setBright);

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Screen")
            .containerSupplier((windowId, player) -> ScreenContainer.create(windowId, getBlockPos(), ScreenTileEntity.this))
            .dataListener(Sync.values(new ResourceLocation(RFToolsUtility.MODID, "data"), this))
            .itemHandler(() -> items));
    @Cap(type = CapType.MODULE)
    private final LazyOptional<IModuleSupport> moduleSupportHandler = LazyOptional.of(() -> new DefaultModuleSupport(ScreenContainer.SLOT_MODULES, ScreenContainer.SCREEN_MODULES - 1) {
        @Override
        public boolean isModule(ItemStack itemStack) {
            return itemStack.getItem() instanceof IModuleProvider;
        }
    });

    // This is a map that contains a map from the coordinate of the screen to a map of screen data from the server indexed by slot number,
    public static final Map<GlobalPos, Map<Integer, IModuleData>> screenData = new HashMap<>();

    // Cached client screen modules
    private List<IClientScreenModule<?>> clientScreenModules = null;

    // A list of tags linked to computer modules.
    private final Map<String, List<ComputerScreenModule>> computerModules = new HashMap<>();

    // If set this is a dummy tile entity
    private RegistryKey<World> dummyType = null;

    private boolean needsServerData = false;
    private boolean showHelp = true;
    private boolean powerOn = false;        // True if screen is powered.
    private boolean connected = false;      // True if screen is connected to a controller.
    private int size = 0;                   // Size of screen (0 is normal, 1 is large, 2 is huge)
    private boolean transparent = false;    // Transparent screen.
    private int color = 0;                  // Color of the screen.
    private boolean bright = false;         // True if the screen contents is full bright

    private int trueTypeMode = 0;           // 0 is default, -1 is disabled, 1 is truetype

    // Sever side, the module we are hovering over
    // Client side, the last set of values we sent to the server
    private int hoveringModule = -1;
    private int hoveringX = -1;
    private int hoveringY = -1;


    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_HUGE = 2;

    // Cached server screen modules
    private List<IScreenModule<?>> screenModules = null;
    private List<ActivatedModule> clickedModules = new ArrayList<>();

    private static class ActivatedModule {
        private int module;
        private int ticks;
        private int x;
        private int y;

        public ActivatedModule(int module, int ticks, int x, int y) {
            this.module = module;
            this.ticks = ticks;
            this.x = x;
            this.y = y;
        }
    }

    private int totalRfPerTick = 0;     // The total rf per tick for all modules.
    private boolean controllerNeededInCreative = false; // If any of this screen's modules use the screen controller, thus requiring one even for creative screens.

    public long lastTime = 0;

    public ScreenTileEntity() {
        super(TYPE_SCREEN.get());
    }

    public ScreenTileEntity(TileEntityType<?> type) {
        super(type);
    }

    // Used for a dummy tile entity (tablet usage)
    public ScreenTileEntity(RegistryKey<World> type) {
        this();
        dummyType = type;
    }

    // Return true if this is a dummy tile entity for the tablet
    public boolean isDummy() {
        return dummyType != null;
    }

    @Override
    public RegistryKey<World> getDimension() {
        if (dummyType != null) {
            return dummyType;
        }
        return super.getDimension();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        return new AxisAlignedBB(xCoord - size - 1, yCoord - size - 1, zCoord - size - 1, xCoord + size + 1, yCoord + size + 1, zCoord + size + 1); // TODO see if we can shrink this
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            checkStateClient();
        } else {
            checkStateServer();
        }
    }

    private void checkStateClient() {
        if (clickedModules.isEmpty()) {
            return;
        }
        List<ActivatedModule> newClickedModules = new ArrayList<>();
        for (ActivatedModule cm : clickedModules) {
            cm.ticks--;
            if (cm.ticks > 0) {
                newClickedModules.add(cm);
            } else {
                List<IClientScreenModule<?>> modules = getClientScreenModules();
                if (cm.module < modules.size()) {
                    modules.get(cm.module).mouseClick(level, cm.x, cm.y, false);
                }
            }
        }
        clickedModules = newClickedModules;
    }

    private void checkStateServer() {
        if (clickedModules.isEmpty()) {
            return;
        }
        List<ActivatedModule> newClickedModules = new ArrayList<>();
        for (ActivatedModule cm : clickedModules) {
            cm.ticks--;
            if (cm.ticks > 0) {
                newClickedModules.add(cm);
            } else {
                List<IScreenModule<?>> modules = getScreenModules();
                if (cm.module < modules.size()) {
                    ItemStack itemStack = items.getStackInSlot(cm.module);
                    IScreenModule<?> module = modules.get(cm.module);
                    module.mouseClick(level, cm.x, cm.y, false, null);
                    if (module instanceof IScreenModuleUpdater) {
                        CompoundNBT newCompound = ((IScreenModuleUpdater) module).update(itemStack.getTag(), level, null);
                        if (newCompound != null) {
                            itemStack.setTag(newCompound);
                            markDirtyClient();
                        }
                    }
                }
            }
        }
        clickedModules = newClickedModules;
    }

    private void resetModules() {
        clientScreenModules = null;
        screenModules = null;
        clickedModules.clear();
        showHelp = true;
        computerModules.clear();
    }

    public static class ModuleRaytraceResult {
        private final int x;
        private final int y;
        private final int currenty;
        private final int moduleIndex;

        public ModuleRaytraceResult(int moduleIndex, int x, int y, int currenty) {
            this.moduleIndex = moduleIndex;
            this.x = x;
            this.y = y;
            this.currenty = currenty;
        }

        public int getModuleIndex() {
            return moduleIndex;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getCurrenty() {
            return currenty;
        }
    }

    private boolean isActivated(int index) {
        for (ActivatedModule module : clickedModules) {
            if (module.module == index) {
                return true;
            }
        }
        return false;
    }

    public void focusModuleClient(double hitX, double hitY, double hitZ, Direction side, Direction horizontalFacing) {
        int x;
        int y;
        int module;
        ModuleRaytraceResult result = getHitModule(hitX, hitY, hitZ, side, horizontalFacing, size);
        if (result == null) {
            x = -1;
            y = -1;
            module = -1;
        } else {
            x = result.getX();
            y = result.getY() - result.getCurrenty();
            module = result.getModuleIndex();
        }

        if (x != hoveringX || y != hoveringY || module != hoveringModule) {
            PacketServerCommandTyped packet = new PacketServerCommandTyped(getBlockPos(), getDimension(), CMD_HOVER.getName(), TypedMap.builder()
                    .put(PARAM_X, x)
                    .put(PARAM_Y, y)
                    .put(PARAM_MODULE, module)
                    .build());
            RFToolsUtilityMessages.INSTANCE.sendToServer(packet);
            hoveringX = x;
            hoveringY = y;
            hoveringModule = module;
        }
    }

    public void hitScreenClient(double hitX, double hitY, double hitZ, Direction side, Direction horizontalFacing) {
        ModuleRaytraceResult result = getHitModule(hitX, hitY, hitZ, side, horizontalFacing, size);
        if (result == null) {
            return;
        }

        hitScreenClient(result);
    }

    public void hitScreenClient(ModuleRaytraceResult result) {
        List<IClientScreenModule<?>> modules = getClientScreenModules();
        int module = result.getModuleIndex();
        if (isActivated(module)) {
            // We are getting a hit twice. Module is already activated. Do nothing
            return;
        }
        modules.get(module).mouseClick(level, result.getX(), result.getY() - result.getCurrenty(), true);
        clickedModules.add(new ActivatedModule(module, 3, result.getX(), result.getY()));

        PacketServerCommandTyped packet = new PacketServerCommandTyped(getBlockPos(), getDimension(), CMD_CLICK.getName(), TypedMap.builder()
                .put(PARAM_X, result.getX())
                .put(PARAM_Y, result.getY() - result.getCurrenty())
                .put(PARAM_MODULE, module)
                .build());
        RFToolsUtilityMessages.INSTANCE.sendToServer(packet);
    }

    public ModuleRaytraceResult getHitModule(double hitX, double hitY, double hitZ, Direction side, Direction horizontalFacing, int size) {
        ModuleRaytraceResult result;
        float factor = size + 1.0f;
        float dx = 0;
        float dy = 0;
        switch (side) {
            case NORTH:
                dx = (float) ((1.0 - hitX) / factor);
                dy = (float) ((1.0 - hitY) / factor);
                break;
            case SOUTH:
                dx = (float) (hitX / factor);
                dy = (float) ((1.0 - hitY) / factor);
                break;
            case WEST:
                dx = (float) (hitZ / factor);
                dy = (float) ((1.0 - hitY) / factor);
                break;
            case EAST:
                dx = (float) ((1.0 - hitZ) / factor);
                dy = (float) ((1.0 - hitY) / factor);
                break;
            case UP:
                switch (horizontalFacing) {
                    case NORTH:
                        dx = (float) ((1.0 - hitX) / factor);
                        dy = (float) ((1.0 - hitZ) / factor);
                        break;
                    case SOUTH:
                        dx = (float) (hitX / factor);
                        dy = (float) (hitZ / factor);
                        break;
                    case WEST:
                        dx = (float) (hitZ / factor);
                        dy = (float) ((1.0 - hitX) / factor);
                        break;
                    case EAST:
                        dx = (float) ((1.0 - hitZ) / factor);
                        dy = (float) (hitX / factor);
                }
                break;
            case DOWN:
                switch (horizontalFacing) {
                    case NORTH:
                        dx = (float) ((1.0 - hitX) / factor);
                        dy = (float) (hitZ / factor);
                        break;
                    case SOUTH:
                        dx = (float) (hitX / factor);
                        dy = (float) ((1.0 - hitZ) / factor);
                        break;
                    case WEST:
                        dx = (float) (hitZ / factor);
                        dy = (float) (hitX / factor);
                        break;
                    case EAST:
                        dx = (float) ((1.0 - hitZ) / factor);
                        dy = (float) ((1.0 - hitX) / factor);
                }
                break;
            default:
                return null;
        }
        int x = (int) (dx * 128);
        int y = (int) (dy * 128);
        int currenty = 7;

        int moduleIndex = 0;
        List<IClientScreenModule<?>> clientScreenModules = getClientScreenModules();
        for (IClientScreenModule<?> module : clientScreenModules) {
            if (module != null) {
                int height = module.getHeight();
                // Check if this module has enough room
                if (currenty + height <= 124) {
                    if (currenty <= y && y < (currenty + height)) {
                        break;
                    }
                    currenty += height;
                }
            }
            moduleIndex++;
        }
        if (moduleIndex >= clientScreenModules.size()) {
            return null;
        }
        result = new ModuleRaytraceResult(moduleIndex, x, y, currenty);
        return result;
    }

    private void hitScreenServer(PlayerEntity player, int x, int y, int module) {
        List<IScreenModule<?>> screenModules = getScreenModules();
        IScreenModule<?> screenModule = screenModules.get(module);
        if (screenModule != null) {
            ItemStack itemStack = items.getStackInSlot(module);
            screenModule.mouseClick(level, x, y, true, player);
            if (screenModule instanceof IScreenModuleUpdater) {
                CompoundNBT newCompound = ((IScreenModuleUpdater) screenModule).update(itemStack.getTag(), level, player);
                if (newCompound != null) {
                    itemStack.setTag(newCompound);
                    markDirtyClient();
                }
            }
            clickedModules.add(new ActivatedModule(module, 5, x, y));
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        powerOn = tagCompound.getBoolean("powerOn");
        connected = tagCompound.getBoolean("connected");
        totalRfPerTick = tagCompound.getInt("rfPerTick");
        controllerNeededInCreative = tagCompound.getBoolean("controllerNeededInCreative");
        readRestorableFromNBT(tagCompound);
    }

    // @todo 1.14 loot tables
    public void readRestorableFromNBT(CompoundNBT tagCompound) {
        resetModules();
        if (tagCompound.contains("large")) {
            size = tagCompound.getBoolean("large") ? 1 : 0;
        } else {
            size = tagCompound.getInt("size");
        }
        transparent = tagCompound.getBoolean("transparent");
        color = tagCompound.getInt("color");
        bright = tagCompound.getBoolean("bright");
        trueTypeMode = tagCompound.getInt("truetype");
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putBoolean("powerOn", powerOn);
        tagCompound.putBoolean("connected", connected);
        tagCompound.putInt("rfPerTick", totalRfPerTick);
        tagCompound.putBoolean("controllerNeededInCreative", controllerNeededInCreative);
        writeRestorableToNBT(tagCompound);
        return tagCompound;
    }

    // @todo 1.14 loot tables
    public void writeRestorableToNBT(CompoundNBT tagCompound) {
        tagCompound.putInt("size", size);
        tagCompound.putBoolean("transparent", transparent);
        tagCompound.putInt("color", color);
        tagCompound.putBoolean("bright", bright);
        tagCompound.putInt("truetype", trueTypeMode);
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        writeRestorableToNBT(tagCompound);
        writeItemHandlerCap(tagCompound);
        tagCompound.putBoolean("powerOn", powerOn);
        tagCompound.putBoolean("connected", connected);
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        powerOn = tagCompound.getBoolean("powerOn");
        connected = tagCompound.getBoolean("connected");
        readRestorableFromNBT(tagCompound);
        readItemHandlerCap(tagCompound);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    public void setSize(int size) {
        this.size = size;
        setChanged();
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
        setChanged();
    }

    public int getTrueTypeMode() {
        return trueTypeMode;
    }

    public void setTrueTypeMode(int trueTypeMode) {
        this.trueTypeMode = trueTypeMode;
        setChanged();
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        setChanged();
    }

    public int getSize() {
        return size;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setPower(boolean power) {
        if (powerOn == power) {
            return;
        }
        powerOn = power;
        markDirtyClient();
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public boolean isRenderable() {
        if (powerOn) {
            return true;
        }
        if (isShowHelp()) {
            return true;    // True because then we give help
        }
        return isCreative();
    }

    public boolean isCreative() {
        return false;
    }

    public void setConnected(boolean c) {
        if (connected == c) {
            return;
        }
        connected = c;
        setChanged();
    }

    public boolean isConnected() {
        return connected;
    }

    public void updateModuleData(int slot, CompoundNBT tagCompound) {
        ItemStack stack = items.getStackInSlot(slot);
        ScreenBlock.getModuleProvider(stack).ifPresent(moduleProvider -> {
            NbtSanitizerModuleGuiBuilder sanitizer = new NbtSanitizerModuleGuiBuilder(level, stack.getTag());
            moduleProvider.createGui(sanitizer);
            stack.setTag(sanitizer.sanitizeNbt(tagCompound));
            screenModules = null;
            clientScreenModules = null;
            computerModules.clear();
            markDirtyClient();
        });
    }

    private static List<IClientScreenModule<?>> helpingScreenModules = null;

    public static List<IClientScreenModule<?>> getHelpingScreenModules() {
        if (helpingScreenModules == null) {
            helpingScreenModules = new ArrayList<>();
            addLine("Read me", 0x7799ff, true);
            addLine("", 0xffffff, false);
            addLine("Sneak-right click for", 0xffffff, false);
            addLine("GUI and insertion of", 0xffffff, false);
            addLine("modules", 0xffffff, false);
            addLine("", 0xffffff, false);
            addLine("Use Screen Controller", 0xffffff, false);
            addLine("to power screens", 0xffffff, false);
            addLine("remotely", 0xffffff, false);
        }
        return helpingScreenModules;
    }

    private static void addLine(String s, int color, boolean large) {
        TextClientScreenModule t1 = new TextClientScreenModule();
        t1.setLine(s);
        t1.setColor(color);
        t1.setLarge(large);
        helpingScreenModules.add(t1);
    }


    // This is called client side.
    public List<IClientScreenModule<?>> getClientScreenModules() {
        if (clientScreenModules == null) {
            needsServerData = false;
            showHelp = true;
            clientScreenModules = new ArrayList<>();
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack itemStack = items.getStackInSlot(i);
                if (!itemStack.isEmpty() && ScreenBlock.hasModuleProvider(itemStack)) {
                    ScreenBlock.getModuleProvider(itemStack).ifPresent(moduleProvider -> {
                        IClientScreenModule<?> clientScreenModule;
                        try {
                            clientScreenModule = moduleProvider.getClientScreenModule().newInstance();
                        } catch (InstantiationException e) {
                            Logging.logError("Internal error with screen modules!", e);
                            return;
                        } catch (IllegalAccessException e) {
                            Logging.logError("Internal error with screen modules!", e);
                            return;
                        }
                        clientScreenModule.setupFromNBT(itemStack.getTag(), level.dimension(), getBlockPos());
                        clientScreenModules.add(clientScreenModule);
                        if (clientScreenModule.needsServerData()) {
                            needsServerData = true;
                        }
                        showHelp = false;
                    });
                } else {
                    clientScreenModules.add(null);        // To keep the indexing correct so that the modules correspond with there slot number.
                }
            }
        }
        return clientScreenModules;
    }

    // Called client side only
    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isNeedsServerData() {
        return needsServerData;
    }

    public int getTotalRfPerTick() {
        if (isCreative()) return 0;
        if (screenModules == null) {
            getScreenModules();
        }
        return totalRfPerTick;
    }

    public boolean isControllerNeeded() {
        if (!isCreative()) return true;
        if (screenModules == null) {
            getScreenModules();
        }
        return controllerNeededInCreative;
    }

    // This is called server side.
    public List<IScreenModule<?>> getScreenModules() {
        if (screenModules == null) {
            totalRfPerTick = 0;
            controllerNeededInCreative = false;
            screenModules = new ArrayList<>();
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack itemStack = items.getStackInSlot(i);
                if (!itemStack.isEmpty() && ScreenBlock.hasModuleProvider(itemStack)) {
                    ScreenBlock.getModuleProvider(itemStack).ifPresent(moduleProvider -> {
                        IScreenModule<?> screenModule;
                        try {
                            screenModule = moduleProvider.getServerScreenModule().newInstance();
                        } catch (InstantiationException e) {
                            Logging.logError("Internal error with screen modules!", e);
                            return;
                        } catch (IllegalAccessException e) {
                            Logging.logError("Internal error with screen modules!", e);
                            return;
                        }
                        screenModule.setupFromNBT(itemStack.getTag(), level.dimension(), getBlockPos());
                        screenModules.add(screenModule);
                        totalRfPerTick += screenModule.getRfPerTick();
                        if (screenModule.needsController()) controllerNeededInCreative = true;

                        if (screenModule instanceof ComputerScreenModule) {
                            ComputerScreenModule computerScreenModule = (ComputerScreenModule) screenModule;
                            String tag = computerScreenModule.getTag();
                            if (!computerModules.containsKey(tag)) {
                                computerModules.put(tag, new ArrayList<ComputerScreenModule>());
                            }
                            computerModules.get(tag).add(computerScreenModule);
                        }
                    });
                } else {
                    screenModules.add(null);        // To keep the indexing correct so that the modules correspond with there slot number.
                }
            }
        }
        return screenModules;
    }

    public List<ComputerScreenModule> getComputerModules(String tag) {
        return computerModules.get(tag);
    }

    public Set<String> getTags() {
        return computerModules.keySet();
    }

    private IScreenDataHelper screenDataHelper = new IScreenDataHelper() {
        @Override
        public IModuleDataInteger createInteger(int i) {
            return new ModuleDataInteger(i);
        }

        @Override
        public IModuleDataBoolean createBoolean(boolean b) {
            return new ModuleDataBoolean(b);
        }

        @Override
        public IModuleDataString createString(String b) {
            return new ModuleDataString(b);
        }

        @Override
        public IModuleDataContents createContents(long contents, long maxContents, long lastPerTick) {
            return new ScreenModuleHelper.ModuleDataContents(contents, maxContents, lastPerTick);
        }
    };

    // This is called server side.
    public Map<Integer, IModuleData> getScreenData(long millis) {
        Map<Integer, IModuleData> map = new HashMap<>();
        List<IScreenModule<?>> screenModules = getScreenModules();
        int moduleIndex = 0;
        for (IScreenModule<?> module : screenModules) {
            if (module != null) {
                IModuleData data = module.getData(screenDataHelper, level, millis);
                if (data != null) {
                    map.put(moduleIndex, data);
                }
            }
            moduleIndex++;
        }
        return map;
    }

    public IScreenModule<?> getHoveringModule() {
        return getHoveringModule(hoveringModule);
    }

    public IScreenModule<?> getHoveringModule(int hoveringModule) {
        if (hoveringModule == -1) {
            return null;
        }
        getScreenModules();
        if (hoveringModule >= 0 && hoveringModule < screenModules.size()) {
            return screenModules.get(hoveringModule);
        }
        return null;
    }

    public int getHoveringX() {
        return hoveringX;
    }

    public int getHoveringY() {
        return hoveringY;
    }

    public static final Key<Integer> PARAM_X = new Key<>("x", Type.INTEGER);
    public static final Key<Integer> PARAM_Y = new Key<>("y", Type.INTEGER);
    public static final Key<Integer> PARAM_MODULE = new Key<>("module", Type.INTEGER);
    public static final Key<Integer> PARAM_TRUETYPE = new Key<>("truetype", Type.INTEGER);

    @ServerCommand
    public static final Command<?> CMD_CLICK = Command.<ScreenTileEntity>create("screen.click",
            (te, player, params) -> te.hitScreenServer(player, params.get(PARAM_X), params.get(PARAM_Y), params.get(PARAM_MODULE)));

    @ServerCommand
    public static final Command<?> CMD_HOVER = Command.<ScreenTileEntity>create("screen.hover",
            (te, player, params) -> {
                te.hoveringX = params.get(PARAM_X);
                te.hoveringY = params.get(PARAM_Y);
                te.hoveringModule = params.get(PARAM_MODULE);
            });

    @ServerCommand
    public static final Command<?> CMD_SETTRUETYPE = Command.<ScreenTileEntity>create("screen.setTruetype",
            (te, player, params) -> te.setTrueTypeMode(params.get(PARAM_TRUETYPE)));

    public static final Key<List<String>> PARAM_INFO = new Key<>("info", Type.STRING_LIST);
    @ServerCommand
    public static final ResultCommand<?> CMD_SCREEN_INFO = ResultCommand.<ScreenTileEntity>create("getScreenInfo",
            (te, player, params) -> {
                IScreenModule<?> module = te.getHoveringModule(params.get(PARAM_MODULE));
                List<String> info = Collections.emptyList();
                if (module instanceof ITooltipInfo) {
                    info = ((ITooltipInfo) module).getInfo(te.level, params.get(PARAM_X), params.get(PARAM_Y));
                }
                return TypedMap.builder()
                        .put(PARAM_INFO, info)
                        .build();
            },
            (te, player, params) -> {
                te.infoReceived = params.get(PARAM_INFO);
            });


    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(ScreenTileEntity.this, ScreenContainer.CONTAINER_FACTORY.get()) {

            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                resetModules();
            }
        };
    }
}
