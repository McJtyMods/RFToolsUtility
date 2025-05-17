package mcjty.rftoolsutility.modules.teleporter.items.porter;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.IEnergyItem;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.ChargedPorterData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.setup.ForgeEventHandlers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.*;

public class ChargedPorterItem extends Item implements IEnergyItem, IComponentsToPreserve, ITooltipSettings {

    private final Supplier<Integer> capacity;
    private final Supplier<Integer> maxReceive;
    private final int maxExtract;

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsutility:machines/teleporter");

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(
                    parameter("energy", this::getEnergyString),
                    parameter("target", this::hasTarget, this::getTargetString),
                    key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(),
                    parameter("energy", this::getEnergyString),
                    parameter("target", this::hasTarget, this::getTargetString))
    );

    public Supplier<Integer> getCapacity() {
        return capacity;
    }

    public Supplier<Integer> getMaxReceive() {
        return maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    private String getEnergyString(ItemStack stack) {
        ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data != null) {
            return Integer.toString(data.energy());
        }
        return "0";
    }

    private boolean hasTarget(ItemStack stack) {
        ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data != null) {
            return data.currentTarget() != -1;
        }
        return false;
    }

    private String getTargetString(ItemStack stack) {
        ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data != null) {
            return Integer.toString(data.currentTarget());
        }
        return "<not set>";
    }

    @Override
    public ManualEntry getManualEntry() {
        return MANUAL;
    }

    public ChargedPorterItem() {
        this(TeleportConfiguration.CHARGEDPORTER_MAXENERGY);
    }

    protected ChargedPorterItem(Supplier<Integer> capacity) {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
        this.capacity = capacity;

        maxReceive = TeleportConfiguration.CHARGEDPORTER_RECEIVEPERTICK;
        maxExtract = 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.isEmpty() != newStack.isEmpty()) {
            return true;
        }
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, Level worldIn, @Nonnull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
            if (data == null) {
                return;
            }
            if (!(entityIn instanceof Player player)) {
                return;
            }
            int timer = data.tpTimer();
            if (timer < 0) {
                return;
            }
            timer--;
            if (timer <= 0) {
                timer = -1;
                stack.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data.withTpTimer(timer));
                TeleportDestinations destinations = TeleportDestinations.get(worldIn);
                int target = data.currentTarget();
                GlobalPos coordinate = destinations.getCoordinateForId(target);
                if (coordinate == null) {
                    Logging.message(player, ChatFormatting.RED + "Something went wrong! The target has disappeared!");
                    TeleportationTools.applyEffectForSeverity(player, 3, false);
                    return;
                }
                TeleportDestination destination = destinations.getDestination(coordinate);
                ForgeEventHandlers.addPlayerToTeleportHere(destination, player);
//                    TeleportationTools.performTeleport(player, destination, 0, 10, false);
            } else {
                stack.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data.withTpTimer(timer));
            }
        }
    }

    public static void initOverrides(ChargedPorterItem item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(RFToolsUtility.MODID, "charge"), (stack, world, livingEntity, seed) -> {
            ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
            int energy = data == null ? 0 : data.energy();
            int level = (9 * energy) / item.capacity.get();
            if (level < 0) {
                level = 0;
            } else if (level > 8) {
                level = 8;
            }
            return 9 - level;
        });
    }


    protected int getSpeedBonus() {
        return 1;
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            startTeleport(stack, player, world);
        } else {
            selectReceiver(stack, world, player);
        }
        return super.use(world, player, hand);
    }

    protected void selectReceiver(ItemStack stack, Level world, Player player) {
    }

    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            BlockEntity te = world.getBlockEntity(pos);
            setTarget(stack, player, world, te);
        } else {
            startTeleport(stack, player, world);
        }
        return InteractionResult.SUCCESS;
    }

    private void startTeleport(ItemStack stack, Player player, Level world) {
        if (world.isClientSide) {
            return;
        }
        ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data == null || data.currentTarget() == -1) {
            Logging.message(player, ChatFormatting.RED + "The charged porter has no target.");
            return;
        }

        if (data.tpTimer() >= 0) {
            Logging.message(player, ChatFormatting.RED + "Already teleporting!");
            return;
        }

        int target = data.currentTarget();

        TeleportDestinations destinations = TeleportDestinations.get(world);
        GlobalPos coordinate = destinations.getCoordinateForId(target);
        if (coordinate == null) {
            Logging.message(player, ChatFormatting.RED + "Something went wrong! The target has disappeared!");
            TeleportationTools.applyEffectForSeverity(player, 3, false);
            return;
        }
        TeleportDestination destination = destinations.getDestination(coordinate);

        if (!TeleportationTools.checkValidTeleport(player, world.dimension(), destination.getDimension())) {
            return;
        }

        BlockPos playerCoordinate = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
        int cost = TeleportationTools.calculateRFCost(world, playerCoordinate, destination);
        cost *= 1.5f;
        long energy = getEnergyStoredL(stack);
        if (cost > energy) {
            Logging.message(player, ChatFormatting.RED + "Not enough energy to start the teleportation!");
            return;
        }
        data = extractEnergyNoMax(data, cost, false);

        int ticks = TeleportationTools.calculateTime(world, playerCoordinate, destination);
        ticks /= getSpeedBonus();
        data = data.withTpTimer(ticks);
        stack.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
        Logging.message(player, ChatFormatting.YELLOW + "Start teleportation!");
    }

    private void setTarget(ItemStack stack, Player player, Level world, BlockEntity te) {
        if (world.isClientSide) {
            return;
        }
        ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);

        if (data == null) {
            data = ChargedPorterData.createDefault();
        }
        int id = -1;
        if (te instanceof MatterReceiverTileEntity receiver) {
            TeleportDestination destination = receiver.updateDestination();
            if (!destination.checkAccess(world, player.getUUID())) {
                Logging.message(player, ChatFormatting.RED + "You have no access to target this receiver!");
                return;
            }
            id = receiver.getId();
        }

        if (id != -1) {
            data = selectOnReceiver(player, world, data, id);
        } else {
            data = selectOnThinAir(player, world, data, stack);
        }
        stack.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
    }

    protected ChargedPorterData selectOnReceiver(Player player, Level world, ChargedPorterData data, int id) {
        Logging.message(player, "Charged porter target is set to " + id + ".");
        return data.withCurrentTarget(id);
    }

    protected ChargedPorterData selectOnThinAir(Player player, Level world, ChargedPorterData data, ItemStack stack) {
        if (world.isClientSide) {
            Logging.message(player, "Charged porter is cleared.");
        }
        return data.withCurrentTarget(-1);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
    }

    @Override
    public long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate) {
        ChargedPorterData data = container.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data == null) {
            data = ChargedPorterData.createDefault();
        }
        int energy = data.energy();
        int energyReceived = Math.min(capacity.get() - energy, Math.min(this.maxReceive.get(), EnergyTools.unsignedClampToInt(maxReceive)));
        if (!simulate) {
            energy += energyReceived;
            data = data.withEnergy(energy);
            container.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
        }
        return energyReceived;
    }

    @Override
    public long extractEnergyL(ItemStack container, long maxExtract, boolean simulate) {
        ChargedPorterData data = container.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data == null) {
            data = ChargedPorterData.createDefault();
        }
        int energy = data.energy();
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, EnergyTools.unsignedClampToInt(maxExtract)));
        if (!simulate) {
            energy -= energyExtracted;
            data = data.withEnergy(energy);
            container.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
        }
        return energyExtracted;
    }

    public ChargedPorterData extractEnergyNoMax(ChargedPorterData data, int maxExtract, boolean simulate) {
        int energy = data.energy();
        int energyExtracted = Math.min(energy, maxExtract);
        if (!simulate) {
            energy -= energyExtracted;
            data = data.withEnergy(energy);
        }
        return data;
    }

    @Override
    public long getEnergyStoredL(ItemStack container) {
        ChargedPorterData data = container.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data == null) {
            return 0;
        }
        return data.energy();
    }

    @Override
    public long getMaxEnergyStoredL(ItemStack container) {
        return capacity.get();
    }

    public IEnergyStorage createEnergyStorage(ItemStack container) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return (int) receiveEnergyL(container, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return (int) extractEnergyL(container, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return (int) getEnergyStoredL(container);
            }

            @Override
            public int getMaxEnergyStored() {
                return (int) getMaxEnergyStoredL(container);
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        return List.of(TeleporterModule.ITEM_CHARGEDPORTER_DATA.get());
    }
}
