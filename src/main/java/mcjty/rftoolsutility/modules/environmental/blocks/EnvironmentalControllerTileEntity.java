package mcjty.rftoolsutility.modules.environmental.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.environmental.EnvModuleProvider;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalModule;
import mcjty.rftoolsutility.modules.environmental.data.EnvironmentalData;
import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class EnvironmentalControllerTileEntity extends TickingTileEntity {

    public static final String COMPONENT_NAME = "environmental_controller";

    public static final int ENV_MODULES = 7;
    public static final int SLOT_MODULES = 0;
    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(ENV_MODULES)
            .box(specific(s -> s.getItem() instanceof EnvModuleProvider).in().out(),
                    SLOT_MODULES, 7, 8, 1, 7)
            .playerSlots(27, 142));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> stack.getItem() instanceof EnvModuleProvider)
            .onUpdate((slot, stack) -> environmentModules = null)
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<EnvironmentalControllerTileEntity, GenericItemHandler> ITEM_CAP = tile -> tile.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(
            this, true, EnvironmentalConfiguration.ENVIRONMENTAL_MAXENERGY.get(), EnvironmentalConfiguration.ENVIRONMENTAL_RECEIVEPERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<EnvironmentalControllerTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    private final IInfusable infusable = new DefaultInfusable(EnvironmentalControllerTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<EnvironmentalControllerTileEntity, IInfusable> INFUSABLE_CAP = tile -> tile.infusable;

    private final IPowerInformation powerInfoHandler = createPowerInfo();
    @Cap(type = CapType.POWER_INFO)
    private static final Function<EnvironmentalControllerTileEntity, IPowerInformation> POWER_INFO_CAP = tile -> tile.powerInfoHandler;

    @Cap(type = CapType.MODULE)
    private static final Function<EnvironmentalControllerTileEntity, IModuleSupport> MODULE_CAP = tile -> new DefaultModuleSupport(SLOT_MODULES, SLOT_MODULES + ENV_MODULES - 1) {
        @Override
        public boolean isModule(ItemStack itemStack) {
            return itemStack.getItem() instanceof EnvModuleProvider;
        }
    };

    @Cap(type = CapType.CONTAINER)
    private static final Function<EnvironmentalControllerTileEntity, MenuProvider> SCREEN_CAP = tile -> new DefaultContainerProvider<GenericContainer>("Environmental Controller")
            .containerSupplier(container(EnvironmentalModule.CONTAINER_ENVIRONENTAL_CONTROLLER, CONTAINER_FACTORY, tile))
            .itemHandler(() -> tile.items)
            .energyHandler(() -> tile.energyStorage)
            .setupSync(tile);

    // Cached server modules
    private List<EnvironmentModule> environmentModules = null;
    private int totalRfPerTick = 0;     // The total rf per tick for all modules.

    @GuiValue
    private static final Value<?, ?> VALUE_MODE = Value.createEnum("mode", EnvironmentalMode.values(), EnvironmentalControllerTileEntity::getMode, EnvironmentalControllerTileEntity::setMode);

    @GuiValue
    public static final Value<?, ?> VALUE_RADIUS = Value.create("radius", Type.INTEGER, EnvironmentalControllerTileEntity::getRadius, EnvironmentalControllerTileEntity::setRadius);

    @GuiValue
    public static final Value<?, ?> VALUE_MINY = Value.create("miny", Type.INTEGER, EnvironmentalControllerTileEntity::getMiny, EnvironmentalControllerTileEntity::setMiny);
    @GuiValue
    public static final Value<?, ?> VALUE_MAXY = Value.create("maxy", Type.INTEGER, EnvironmentalControllerTileEntity::getMaxy, EnvironmentalControllerTileEntity::setMaxy);

    private int volume = -1;
    private boolean active = false;

    private int powerTimeout = 0;

    public EnvironmentalControllerTileEntity(BlockPos pos, BlockState state) {
        super(EnvironmentalModule.ENVIRONENTAL_CONTROLLER.be().get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
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
    protected boolean needsRedstoneMode() {
        return true;
    }

    public EnvironmentalMode getMode() {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        return data.mode();
    }

    public void setMode(EnvironmentalMode mode) {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        data = data.withMode(mode);
        setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data);
    }

    private float getPowerMultiplier() {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        return switch (data.mode()) {
            case MODE_BLACKLIST, MODE_WHITELIST -> 1.0f;
            case MODE_HOSTILE, MODE_PASSIVE, MODE_MOBS, MODE_ALL -> (float) (double) EnvironmentalConfiguration.mobsPowerMultiplier.get();
        };
    }

    public boolean isEntityAffected(Entity entity) {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        switch (data.mode()) {
            case MODE_BLACKLIST:
                if (entity instanceof Player) {
                    return isPlayerAffected((Player) entity);
                } else {
                    return false;
                }
            case MODE_WHITELIST:
                if (entity instanceof Player) {
                    return isPlayerAffected((Player) entity);
                } else {
                    return false;
                }
            case MODE_HOSTILE:
                return entity instanceof Enemy;
            case MODE_PASSIVE:
                return entity instanceof Mob && !(entity instanceof Enemy);
            case MODE_MOBS:
                return entity instanceof Mob;
            case MODE_ALL:
                if (entity instanceof Player) {
                    return isPlayerAffected((Player) entity);
                } else {
                    return true;
                }
        }
        return false;
    }

    public boolean isPlayerAffected(Player player) {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        if (data.mode() == EnvironmentalMode.MODE_WHITELIST) {
            return data.players().contains(player.getName().getString());
        } else if (data.mode() == EnvironmentalMode.MODE_BLACKLIST) {
            return !data.players().contains(player.getName().getString());
        } else {
            return data.mode() == EnvironmentalMode.MODE_ALL;
        }
    }

    public List<String> getPlayersAsList() {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        return new ArrayList<>(data.players());
    }

    private void addPlayer(String player) {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        Set<String> players = data.players();
        if (!players.contains(player)) {
            players.add(player);
            setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data.withPlayers(players));
        }
    }

    private void delPlayer(String player) {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        Set<String> players = data.players();
        if (players.contains(player)) {
            players.remove(player);
            setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data.withPlayers(players));
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getTotalRfPerTick() {
        if (environmentModules == null) {
            getEnvironmentModules();
        }
        float factor = infusable.getInfusedFactor();
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
            EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
            volume = (int) ((data.radius() * data.radius() * Math.PI) * (data.maxy() - data.miny() + 1));
        }
        return volume;
    }

    public int getRadius() {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        return data.radius();
    }

    public void setRadius(int radius) {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        data = data.withRadius(radius);
        setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data);
        volume = -1;
        environmentModules = null;
    }

    public int getMiny() {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        return data.miny();
    }

    public void setMiny(int miny) {
        if (miny == Integer.MIN_VALUE) {
            return;
        }
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        data = data.withMiny(miny);
        setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data);
        volume = -1;
        environmentModules = null;
    }

    public int getMaxy() {
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        return data.maxy();
    }

    public void setMaxy(int maxy) {
        if (maxy == Integer.MIN_VALUE) {
            return;
        }
        EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
        data = data.withMaxy(maxy);
        setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data);
        volume = -1;
        environmentModules = null;
    }

    @Override
    protected void tickServer() {
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
                EnvironmentalData data = getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
                module.tick(level, getBlockPos(), data.radius(), data.miny(), data.maxy(), this);
            }
            if (!active) {
                active = true;
                markDirtyClient();
            }
        }
    }

    public void deactivate() {
        for (EnvironmentModule module : environmentModules) {
            module.activate(false);
        }
        if (active) {
            active = false;
            markDirtyClient();
        }
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
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof EnvModuleProvider moduleProvider) {
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        totalRfPerTick = tag.getInt("rfPerTick");
        active = tag.getBoolean("active");
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        active = tag.getBoolean("active");
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("active", active);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("rfPerTick", totalRfPerTick);
        tag.putBoolean("active", active);
    }

    @ServerCommand
    public static final Command<?> CMD_RSMODE = Command.<EnvironmentalControllerTileEntity>create("env.setRsMode",
            (te, player, params) -> te.setRSMode(RedstoneMode.values()[params.get(ImageChoiceLabel.PARAM_CHOICE_IDX)]));

    public static final Key<Integer> PARAM_MIN = new Key<>("min", Type.INTEGER);
    public static final Key<Integer> PARAM_MAX = new Key<>("max", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_SETBOUNDS = Command.<EnvironmentalControllerTileEntity>create("env.setBounds",
            (te, player, params) -> {
                te.setMiny(params.get(PARAM_MIN));
                te.setMaxy(params.get(PARAM_MAX));
            });

    public static final Key<Integer> PARAM_MODE = new Key<>("mode", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_SETMODE = Command.<EnvironmentalControllerTileEntity>create("env.setBlacklist",
            (te, player, params) -> te.setMode(EnvironmentalMode.values()[params.get(PARAM_MODE)]));

    public static final Key<String> PARAM_NAME = new Key<>("name", Type.STRING);
    @ServerCommand
    public static final Command<?> CMD_ADDPLAYER = Command.<EnvironmentalControllerTileEntity>create("env.addPlayer",
            (te, player, params) -> te.addPlayer(params.get(PARAM_NAME)));

    @ServerCommand
    public static final Command<?> CMD_DELPLAYER = Command.<EnvironmentalControllerTileEntity>create("env.delPlayer",
            (te, player, params) -> te.delPlayer(params.get(PARAM_NAME)));

    @ServerCommand(type = String.class)
    public static final ListCommand<?, ?> CMD_GETPLAYERS = ListCommand.<EnvironmentalControllerTileEntity, String>create("rftoolsutility.env.getPlayers",
            (te, player, params) -> te.getPlayersAsList(),
            (te, player, params, list) -> {
                EnvironmentalData data = te.getData(EnvironmentalModule.ENVIRONMENTAL_DATA);
                data = data.withPlayers(new HashSet<>(list));
                te.setData(EnvironmentalModule.ENVIRONMENTAL_DATA, data);
            });

    @Override
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        deactivate();
    }

    @Nonnull
    private IPowerInformation createPowerInfo() {
        return new IPowerInformation() {
            @Override
            public long getEnergyDiffPerTick() {
                return isActive() ? -getTotalRfPerTick() : 0;
            }

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

            @Override
            public String getMachineStatus() {
                return isActive() ? "active" : "idle";
            }
        };
    }
}
