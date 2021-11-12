package mcjty.rftoolsutility.modules.environmental.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.gui.widgets.ScrollableLabel;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.RedstoneMode;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.environmental.EnvModuleProvider;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class EnvironmentalControllerTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final String CMD_SETRADIUS = "env.setRadius";
    public static final String CMD_RSMODE = "env.setRsMode";

    public static final String CMD_SETBOUNDS = "env.setBounds";
    public static final Key<Integer> PARAM_MIN = new Key<>("min", Type.INTEGER);
    public static final Key<Integer> PARAM_MAX = new Key<>("max", Type.INTEGER);

    public static final String CMD_SETMODE = "env.setBlacklist";
    public static final Key<Integer> PARAM_MODE = new Key<>("mode", Type.INTEGER);

    public static final String CMD_ADDPLAYER = "env.addPlayer";
    public static final String CMD_DELPLAYER = "env.delPlayer";
    public static final Key<String> PARAM_NAME = new Key<>("name", Type.STRING);

    public static final String CMD_GETPLAYERS = "getPlayers";
    public static final String CLIENTCMD_GETPLAYERS = "getPlayers";

    public static final String COMPONENT_NAME = "environmental_controller";

    public static final int ENV_MODULES = 7;
    public static final int SLOT_MODULES = 0;
    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(ENV_MODULES)
            .box(specific(s -> s.getItem() instanceof EnvModuleProvider).in().out(),
                    CONTAINER_CONTAINER, SLOT_MODULES, 7, 8, 1, 7)
            .playerSlots(27, 142));

    public static final String CONTAINER_INVENTORY = "container";

//    private InventoryHelper inventoryHelper = new InventoryHelper(this, CONTAINER_FACTORY, ENV_MODULES);
    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(
            this, true, EnvironmentalConfiguration.ENVIRONMENTAL_MAXENERGY.get(), EnvironmentalConfiguration.ENVIRONMENTAL_RECEIVEPERTICK.get());
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(EnvironmentalControllerTileEntity.this));
    private final LazyOptional<IPowerInformation> powerInfoHandler = LazyOptional.of(this::createPowerInfo);
    private final LazyOptional<IModuleSupport> moduleSupportHandler = LazyOptional.of(() -> new DefaultModuleSupport(SLOT_MODULES, SLOT_MODULES + ENV_MODULES - 1) {
        @Override
        public boolean isModule(ItemStack itemStack) {
            return itemStack.getItem() instanceof EnvModuleProvider;
        }
    });

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Builder")
            .containerSupplier((windowId, player) -> new GenericContainer(EnvironmentalModule.CONTAINER_ENVIRONENTAL_CONTROLLER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), EnvironmentalControllerTileEntity.this))
            .dataListener(Sync.values(new ResourceLocation(RFToolsUtility.MODID, "data"), this))
            .shortListener(Sync.integer(this::getRadius, this::setRadius))
            .shortListener(Sync.integer(this::getMiny, this::setMiny))
            .shortListener(Sync.integer(this::getMaxy, this::setMaxy))
            .shortListener(Sync.enumeration(this::getMode, this::setMode, EnvironmentalMode.values()))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
    );

    public enum EnvironmentalMode {
        MODE_BLACKLIST,
        MODE_WHITELIST,
        MODE_HOSTILE,
        MODE_PASSIVE,
        MODE_MOBS,
        MODE_ALL
    }

    // Cached server modules
    private List<EnvironmentModule> environmentModules = null;
    public Set<String> players = new HashSet<>();  // @todo convert to UUID!
    private EnvironmentalMode mode = EnvironmentalMode.MODE_BLACKLIST;
    private int totalRfPerTick = 0;     // The total rf per tick for all modules.
    private int radius = 50;
    private int miny = 30;
    private int maxy = 70;
    private int volume = -1;
    private boolean active = false;

    private int powerTimeout = 0;

    public EnvironmentalControllerTileEntity() {
        super(EnvironmentalModule.TYPE_ENVIRONENTAL_CONTROLLER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(AbstractBlock.Properties.of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .lightLevel(value -> 13))
                .tileEntitySupplier(EnvironmentalControllerTileEntity::new)
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsutility:machines/environmental"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(), gold())) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    @Override
    public IValue<?>[] getValues() {
        return new IValue[]{
                new DefaultValue<>(VALUE_RSMODE, this::getRSModeInt, this::setRSModeInt),
        };
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    public EnvironmentalMode getMode() {
        return mode;
    }

    public void setMode(EnvironmentalMode mode) {
        this.mode = mode;
        setChanged();
    }

    private float getPowerMultiplier() {
        switch (mode) {
            case MODE_BLACKLIST:
            case MODE_WHITELIST:
                return 1.0f;
            case MODE_HOSTILE:
            case MODE_PASSIVE:
            case MODE_MOBS:
            case MODE_ALL:
                return (float) (double) EnvironmentalConfiguration.mobsPowerMultiplier.get();
        }
        return 1.0f;
    }

    public boolean isEntityAffected(Entity entity) {
        switch (mode) {
            case MODE_BLACKLIST:
                if (entity instanceof PlayerEntity) {
                    return isPlayerAffected((PlayerEntity) entity);
                } else {
                    return false;
                }
            case MODE_WHITELIST:
                if (entity instanceof PlayerEntity) {
                    return isPlayerAffected((PlayerEntity) entity);
                } else {
                    return false;
                }
            case MODE_HOSTILE:
                return entity instanceof IMob;
            case MODE_PASSIVE:
                return entity instanceof MobEntity && !(entity instanceof IMob);
            case MODE_MOBS:
                return entity instanceof MobEntity;
            case MODE_ALL:
                if (entity instanceof PlayerEntity) {
                    return isPlayerAffected((PlayerEntity) entity);
                } else {
                    return true;
                }
        }
        return false;
    }

    public boolean isPlayerAffected(PlayerEntity player) {
        if (mode == EnvironmentalMode.MODE_WHITELIST) {
            return players.contains(player.getName().getString());
        } else if (mode == EnvironmentalMode.MODE_BLACKLIST) {
            return !players.contains(player.getName().getString());
        } else {
            return mode == EnvironmentalMode.MODE_ALL;
        }
    }

    private List<String> getPlayersAsList() {
        return new ArrayList<>(players);
    }

    private void addPlayer(String player) {
        if (!players.contains(player)) {
            players.add(player);
            setChanged();
        }
    }

    private void delPlayer(String player) {
        if (players.contains(player)) {
            players.remove(player);
            setChanged();
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getTotalRfPerTick() {
        if (environmentModules == null) {
            getEnvironmentModules();
        }
        float factor = infusableHandler.map(IInfusable::getInfusedFactor).orElse(0.0f);
        int rfNeeded = (int) (totalRfPerTick * getPowerMultiplier() * (4.0f - factor) / 4.0f);
        if (environmentModules.isEmpty()) {
            return rfNeeded;
        }
        if (rfNeeded < EnvironmentalConfiguration.MIN_USAGE.get()) {
            rfNeeded = EnvironmentalConfiguration.MIN_USAGE.get();
        }
        return rfNeeded;
    }

    public int getVolume() {
        if (volume == -1) {
            volume = (int) ((radius * radius * Math.PI) * (maxy - miny + 1));
        }
        return volume;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        volume = -1;
        environmentModules = null;
        setChanged();
    }

    public int getMiny() {
        return miny;
    }

    public void setMiny(int miny) {
        if (miny == -1) {
            return;
        }
        this.miny = miny;
        volume = -1;
        environmentModules = null;
        setChanged();
    }

    public int getMaxy() {
        return maxy;
    }

    public void setMaxy(int maxy) {
        if (maxy == -1) {
            return;
        }
        this.maxy = maxy;
        volume = -1;
        environmentModules = null;
        setChanged();
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        if (powerTimeout > 0) {
            powerTimeout--;
            return;
        }

        long rf = energyStorage.getEnergyStored();
        if (!isMachineEnabled()) {
            rf = 0;
        }

        getEnvironmentModules();

        int rfNeeded = getTotalRfPerTick();
        if (rfNeeded > rf || environmentModules.isEmpty()) {
            deactivate();
            powerTimeout = 20;
        } else {
            energyStorage.consumeEnergy(rfNeeded);
            for (EnvironmentModule module : environmentModules) {
                module.activate(true);
                module.tick(level, getBlockPos(), radius, miny, maxy, this);
            }
            if (!active) {
                active = true;
                setChanged();
            }
        }
    }

    public void deactivate() {
        for (EnvironmentModule module : environmentModules) {
            module.activate(false);
        }
        if (active) {
            active = false;
            setChanged();
        }
    }

    private Object[] setRedstoneMode(String mode) {
        RedstoneMode redstoneMode = RedstoneMode.getMode(mode);
        if (redstoneMode == null) {
            throw new IllegalArgumentException("Not a valid mode");
        }
        setRSMode(redstoneMode);
        return null;
    }

    @Override
    public void setPowerInput(int powered) {
        if (powerLevel != powered) {
            powerTimeout = 0;
        }
        super.setPowerInput(powered);
    }

    // This is called server side.
    public List<EnvironmentModule> getEnvironmentModules() {
        if (environmentModules == null) {
            int volume = getVolume();
            totalRfPerTick = 0;
            environmentModules = new ArrayList<>();
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack itemStack = items.getStackInSlot(i);
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof EnvModuleProvider) {
                    EnvModuleProvider moduleProvider = (EnvModuleProvider) itemStack.getItem();
                    Supplier<? extends EnvironmentModule> supplier = moduleProvider.getServerEnvironmentModule();
                    EnvironmentModule environmentModule = supplier.get();
                    environmentModules.add(environmentModule);
                    totalRfPerTick += (int) (environmentModule.getRfPerTick() * volume);
                }
            }

        }
        return environmentModules;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        totalRfPerTick = tagCompound.getInt("rfPerTick");
        active = tagCompound.getBoolean("active");
    }

    @Override
    protected void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        radius = info.getInt("radius");
        miny = info.getInt("miny");
        maxy = info.getInt("maxy");
        volume = -1;

        // Compatibility
        if (info.contains("whitelist")) {
            boolean wl = info.getBoolean("whitelist");
            mode = wl ? EnvironmentalMode.MODE_WHITELIST : EnvironmentalMode.MODE_BLACKLIST;
        } else {
            int m = info.getInt("mode");
            mode = EnvironmentalMode.values()[m];
        }

        players.clear();
        ListNBT playerList = info.getList("players", Constants.NBT.TAG_STRING);
        if (!playerList.isEmpty()) {
            for (int i = 0; i < playerList.size(); i++) {
                String player = playerList.getString(i);
                players.add(player);
            }
        }
    }


    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putInt("rfPerTick", totalRfPerTick);
        tagCompound.putBoolean("active", active);
        return tagCompound;
    }

    @Override
    protected void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("radius", radius);
        info.putInt("miny", miny);
        info.putInt("maxy", maxy);

        info.putInt("mode", mode.ordinal());

        ListNBT playerTagList = new ListNBT();
        for (String player : players) {
            playerTagList.add(StringNBT.valueOf(player));
        }
        info.put("players", playerTagList);
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_ADDPLAYER.equals(command)) {
            addPlayer(params.get(PARAM_NAME));
            return true;
        } else if (CMD_DELPLAYER.equals(command)) {
            delPlayer(params.get(PARAM_NAME));
            return true;
        } else if (CMD_RSMODE.equals(command)) {
            setRSMode(RedstoneMode.values()[params.get(ImageChoiceLabel.PARAM_CHOICE_IDX)]);
            return true;
        } else if (CMD_SETRADIUS.equals(command)) {
            setRadius(params.get(ScrollableLabel.PARAM_VALUE));
            return true;
        } else if (CMD_SETMODE.equals(command)) {
            setMode(EnvironmentalMode.values()[params.get(PARAM_MODE)]);
            return true;
        } else if (CMD_SETBOUNDS.equals(command)) {
            setMiny(params.get(PARAM_MIN));
            setMaxy(params.get(PARAM_MAX));
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, TypedMap args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (CMD_GETPLAYERS.equals(command)) {
            return type.convert(getPlayersAsList());
        }
        return Collections.emptyList();
    }

    @Override
    public <T> boolean receiveListFromServer(String command, List<T> list, Type<T> type) {
        boolean rc = super.receiveListFromServer(command, list, type);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_GETPLAYERS.equals(command)) {
            players = new HashSet<>(Type.STRING.convert(list));
            return true;
        }
        return false;
    }

    @Override
    public void onReplaced(World world, BlockPos pos, BlockState state, BlockState newstate) {
        deactivate();
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof EnvModuleProvider;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }

    @Nonnull
    private IPowerInformation createPowerInfo() {
        return new IPowerInformation() {
            @Override
            public long getEnergyDiffPerTick() {
                return isActive() ? -getTotalRfPerTick() : 0;
            }

            @Nullable
            @Override
            public String getEnergyUnitName() {
                return "RF";
            }

            @Override
            public boolean isMachineActive() {
                return isActive();
            }

            @Override
            public boolean isMachineRunning() {
                return isActive();
            }

            @Nullable
            @Override
            public String getMachineStatus() {
                return isActive() ? "active" : "idle";
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        if (cap == CapabilityInfusable.INFUSABLE_CAPABILITY) {
            return infusableHandler.cast();
        }
        if (cap == CapabilityModuleSupport.MODULE_CAPABILITY) {
            return moduleSupportHandler.cast();
        }
        if (cap == CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY) {
            return powerInfoHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

}
