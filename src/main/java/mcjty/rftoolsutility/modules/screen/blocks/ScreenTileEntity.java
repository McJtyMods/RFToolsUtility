package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ResultCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.screens.*;
import mcjty.rftoolsbase.api.screens.data.*;
import mcjty.rftoolsutility.modules.screen.NbtSanitizerModuleGuiBuilder;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataBoolean;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataInteger;
import mcjty.rftoolsutility.modules.screen.data.ModuleDataString;
import mcjty.rftoolsutility.modules.screen.modules.ComputerScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ScreenModuleHelper;
import mcjty.rftoolsutility.modules.screen.modulesclient.TextClientScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.rftoolsutility.modules.screen.ScreenModule.TYPE_SCREEN;

public class ScreenTileEntity extends TickingTileEntity {

    // Client side data for CMD_SCREEN_INFO
    public List<String> infoReceived = Collections.emptyList();

    @GuiValue
    private boolean bright = false;         // True if the screen contents is full bright

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items =  GenericItemHandler.create(this, ScreenContainer.CONTAINER_FACTORY)
            .onUpdate((slot, stack) -> resetModules())
            .build();

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Screen")
            .containerSupplier((windowId, player) -> ScreenContainer.create(windowId, getBlockPos(), ScreenTileEntity.this, player))
            .itemHandler(() -> items)
            .setupSync(this));

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
    private ResourceKey<Level> dummyType = null;

    private boolean needsServerData = false;
    private boolean showHelp = true;
    private boolean powerOn = false;        // True if screen is powered.
    private boolean connected = false;      // True if screen is connected to a controller.
    private int size = 0;                   // Size of screen (0 is normal, 1 is large, 2 is huge)
    private boolean transparent = false;    // Transparent screen.
    private int color = 0;                  // Color of the screen.

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
    private Map<ActivatedModule, ModuleTicker> clickedModules = new HashMap<>();

    // Module information
    private static class ModuleTicker {
        private int ticks;

        public ModuleTicker(int ticks) {
            this.ticks = ticks;
        }
    }
    private record ActivatedModule(int module, int x, int y) { }

    private int totalRfPerTick = 0;     // The total rf per tick for all modules.
    private boolean controllerNeededInCreative = false; // If any of this screen's modules use the screen controller, thus requiring one even for creative screens.

    public long lastTime = 0;

    public ScreenTileEntity(BlockPos pos, BlockState state) {
        super(TYPE_SCREEN.get(), pos, state);
    }

    public ScreenTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // Used for a dummy tile entity (tablet usage)
    public ScreenTileEntity(ResourceKey<Level> world, BlockPos pos) {
        this(pos, null);
        dummyType = world;
    }

    public ScreenTileEntity(BlockEntityType<?> type, ResourceKey<Level> world, BlockPos pos) {
        this(type, pos, null);
        dummyType = world;
    }

    // Return true if this is a dummy tile entity for the tablet
    public boolean isDummy() {
        return dummyType != null;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        if (dummyType != null) {
            return dummyType;
        }
        return super.getDimension();
    }

    @Override
    public AABB getRenderBoundingBox() {
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        return new AABB(xCoord - size - 1, yCoord - size - 1, zCoord - size - 1, xCoord + size + 1, yCoord + size + 1, zCoord + size + 1); // TODO see if we can shrink this
    }

    @Override
    protected void tickClient() {
        tickMe();
    }

    public void tickMe() {
        if (clickedModules.isEmpty()) {
            return;
        }
        Map<ActivatedModule, ModuleTicker> newClickedModules = new HashMap<>();
        for (Map.Entry<ActivatedModule, ModuleTicker> cm : clickedModules.entrySet()) {
            cm.getValue().ticks--;
            ActivatedModule activatedModule = cm.getKey();
            if (cm.getValue().ticks > 0) {
                newClickedModules.put(activatedModule, cm.getValue());
            } else {
                List<IClientScreenModule<?>> modules = getClientScreenModules();
                if (activatedModule.module < modules.size()) {
                    modules.get(activatedModule.module).mouseClick(level, activatedModule.x, activatedModule.y, false);
                }
            }
        }
        clickedModules = newClickedModules;
    }

    @Override
    protected void tickServer() {
        if (clickedModules.isEmpty()) {
            return;
        }
        Map<ActivatedModule, ModuleTicker> newClickedModules = new HashMap<>();
        for (Map.Entry<ActivatedModule, ModuleTicker> cm : clickedModules.entrySet()) {
            cm.getValue().ticks--;
            ActivatedModule activatedModule = cm.getKey();
            if (cm.getValue().ticks > 0) {
                newClickedModules.put(activatedModule, cm.getValue());
            } else {
                List<IScreenModule<?>> modules = getScreenModules();
                if (activatedModule.module < modules.size()) {
                    ItemStack itemStack = items.getStackInSlot(activatedModule.module);
                    IScreenModule<?> module = modules.get(activatedModule.module);
                    module.mouseClick(level, activatedModule.x, activatedModule.y, false, null);
                    if (module instanceof IScreenModuleUpdater) {
                        CompoundTag newCompound = ((IScreenModuleUpdater) module).update(itemStack.getTag(), level, null);
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

    public record ModuleRaytraceResult(int moduleIndex, int x, int y, int currenty) {
    }

    private boolean isActivated(int index) {
        for (ActivatedModule module : clickedModules.keySet()) {
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
            x = result.x();
            y = result.y() - result.currenty();
            module = result.moduleIndex();
        }

        if (x != hoveringX || y != hoveringY || module != hoveringModule) {
            PacketServerCommandTyped packet = PacketServerCommandTyped.create(getBlockPos(), getDimension(), CMD_HOVER.name(), TypedMap.builder()
                    .put(PARAM_X, x)
                    .put(PARAM_Y, y)
                    .put(PARAM_MODULE, module)
                    .build());
            Networking.sendToServer(packet);
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
        int module = result.moduleIndex();
        if (isActivated(module)) {
            // We are getting a hit twice. Module is already activated. Do nothing
            return;
        }
        modules.get(module).mouseClick(level, result.x(), result.y() - result.currenty(), true);
        clickedModules.put(new ActivatedModule(module, result.x(), result.y()), new ModuleTicker(3));

        PacketServerCommandTyped packet = PacketServerCommandTyped.create(getBlockPos(), getDimension(), CMD_CLICK.name(), TypedMap.builder()
                .put(PARAM_X, result.x())
                .put(PARAM_Y, result.y() - result.currenty())
                .put(PARAM_MODULE, module)
                .build());
        Networking.sendToServer(packet);
    }

    public ModuleRaytraceResult getHitModule(double hitX, double hitY, double hitZ, Direction side, Direction horizontalFacing, int size) {
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
                    case NORTH -> {
                        dx = (float) ((1.0 - hitX) / factor);
                        dy = (float) ((1.0 - hitZ) / factor);
                    }
                    case SOUTH -> {
                        dx = (float) (hitX / factor);
                        dy = (float) (hitZ / factor);
                    }
                    case WEST -> {
                        dx = (float) (hitZ / factor);
                        dy = (float) ((1.0 - hitX) / factor);
                    }
                    case EAST -> {
                        dx = (float) ((1.0 - hitZ) / factor);
                        dy = (float) (hitX / factor);
                    }
                }
                break;
            case DOWN:
                switch (horizontalFacing) {
                    case NORTH -> {
                        dx = (float) ((1.0 - hitX) / factor);
                        dy = (float) (hitZ / factor);
                    }
                    case SOUTH -> {
                        dx = (float) (hitX / factor);
                        dy = (float) ((1.0 - hitZ) / factor);
                    }
                    case WEST -> {
                        dx = (float) (hitZ / factor);
                        dy = (float) (hitX / factor);
                    }
                    case EAST -> {
                        dx = (float) ((1.0 - hitZ) / factor);
                        dy = (float) ((1.0 - hitX) / factor);
                    }
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
        return new ModuleRaytraceResult(moduleIndex, x, y, currenty);
    }

    private void hitScreenServer(Player player, int x, int y, int module) {
        List<IScreenModule<?>> screenModules = getScreenModules();
        IScreenModule<?> screenModule = screenModules.get(module);
        if (screenModule != null) {
            ItemStack itemStack = items.getStackInSlot(module);
            screenModule.mouseClick(level, x, y, true, player);
            if (screenModule instanceof IScreenModuleUpdater updater) {
                CompoundTag newCompound = updater.update(itemStack.getTag(), level, player);
                if (newCompound != null) {
                    itemStack.setTag(newCompound);
                    markDirtyClient();
                }
            }
            clickedModules.put(new ActivatedModule(module, x, y), new ModuleTicker(5));
        }
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        powerOn = tagCompound.getBoolean("powerOn");
        connected = tagCompound.getBoolean("connected");
        totalRfPerTick = tagCompound.getInt("rfPerTick");
        controllerNeededInCreative = tagCompound.getBoolean("controllerNeededInCreative");
        readRestorableFromNBT(tagCompound);
    }

    // @todo 1.14 loot tables
    public void readRestorableFromNBT(CompoundTag tagCompound) {
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

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("powerOn", powerOn);
        tagCompound.putBoolean("connected", connected);
        tagCompound.putInt("rfPerTick", totalRfPerTick);
        tagCompound.putBoolean("controllerNeededInCreative", controllerNeededInCreative);
        writeRestorableToNBT(tagCompound);
    }

    // @todo 1.14 loot tables
    public void writeRestorableToNBT(CompoundTag tagCompound) {
        tagCompound.putInt("size", size);
        tagCompound.putBoolean("transparent", transparent);
        tagCompound.putInt("color", color);
        tagCompound.putBoolean("bright", bright);
        tagCompound.putInt("truetype", trueTypeMode);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        writeRestorableToNBT(tagCompound);
        saveItemHandlerCap(tagCompound);
        tagCompound.putBoolean("powerOn", powerOn);
        tagCompound.putBoolean("connected", connected);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        powerOn = tagCompound.getBoolean("powerOn");
        connected = tagCompound.getBoolean("connected");
        readRestorableFromNBT(tagCompound);
        loadItemHandlerCap(tagCompound);
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

    public void updateModuleData(int slot, CompoundTag tagCompound) {
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
                        } catch (InstantiationException | IllegalAccessException e) {
                            Logging.logError("Internal error with screen modules!", e);
                            return;
                        }
                        clientScreenModule.setupFromNBT(itemStack.getTag(), getDimension(), getBlockPos());
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
                        } catch (InstantiationException | IllegalAccessException e) {
                            Logging.logError("Internal error with screen modules!", e);
                            return;
                        }
                        screenModule.setupFromNBT(itemStack.getTag(), level.dimension(), getBlockPos());
                        screenModules.add(screenModule);
                        totalRfPerTick += screenModule.getRfPerTick();
                        if (screenModule.needsController()) controllerNeededInCreative = true;

                        if (screenModule instanceof ComputerScreenModule computerScreenModule) {
                            String tag = computerScreenModule.getTag();
                            if (!computerModules.containsKey(tag)) {
                                computerModules.put(tag, new ArrayList<>());
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

    private final IScreenDataHelper screenDataHelper = new IScreenDataHelper() {
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


}
