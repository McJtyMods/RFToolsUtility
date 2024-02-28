package mcjty.rftoolsutility.modules.teleporter.items.porter;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.*;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.setup.ForgeEventHandlers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
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
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.*;

public class ChargedPorterItem extends Item implements IEnergyItem, INBTPreservingIngredient, ITooltipSettings {

    private final Supplier<Integer> capacity;
    private final Supplier<Integer> maxReceive;
    private final int maxExtract;

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsutility:machines/teleporter");

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(
                    parameter("energy", this::getEnergyString),
                    parameter("target", this::hasTarget, this::getTargetString),
                    key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(),
                    parameter("energy", this::getEnergyString),
                    parameter("target", this::hasTarget, this::getTargetString))
            ;

    private String getEnergyString(ItemStack stack) {
        return Integer.toString(stack.hasTag() ? stack.getTag().getInt("Energy") : 0);
    }

    private boolean hasTarget(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            return tag.contains("target");
        }
        return false;
    }

    private String getTargetString(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            return Integer.toString(tag.getInt("target"));
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
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).defaultDurability(1));
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
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityProvider(stack, this);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, Level worldIn, @Nonnull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide) {
            CompoundTag tagCompound = stack.getTag();
            if (tagCompound == null) {
                return;
            }
            if (!tagCompound.contains("tpTimer")) {
                return;
            }
            if (!(entityIn instanceof Player player)) {
                return;
            }
            int timer = tagCompound.getInt("tpTimer");
            timer--;
            if (timer <= 0) {
                tagCompound.remove("tpTimer");
                TeleportDestinations destinations = TeleportDestinations.get(worldIn);
                int target = tagCompound.getInt("target");
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
                tagCompound.putInt("tpTimer", timer);
            }
        }
    }

    public static void initOverrides(ChargedPorterItem item) {
        ItemProperties.register(item, new ResourceLocation(RFToolsUtility.MODID, "charge"), (stack, world, livingEntity, seed) -> {
            CompoundTag tagCompound = stack.getTag();
            int energy = tagCompound == null ? 0 : tagCompound.getInt("Energy");
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
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null || (!tagCompound.contains("target")) || tagCompound.getInt("target") == -1) {
            Logging.message(player, ChatFormatting.RED + "The charged porter has no target.");
            return;
        }

        if (tagCompound.contains("tpTimer")) {
            Logging.message(player, ChatFormatting.RED + "Already teleporting!");
            return;
        }

        int target = tagCompound.getInt("target");

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
        extractEnergyNoMax(stack, cost, false);

        int ticks = TeleportationTools.calculateTime(world, playerCoordinate, destination);
        ticks /= getSpeedBonus();
        tagCompound.putInt("tpTimer", ticks);
        Logging.message(player, ChatFormatting.YELLOW + "Start teleportation!");
    }

    private void setTarget(ItemStack stack, Player player, Level world, BlockEntity te) {
        if (world.isClientSide) {
            return;
        }
        CompoundTag tagCompound = stack.getTag();

        if (tagCompound == null) {
            tagCompound = new CompoundTag();
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
            selectOnReceiver(player, world, tagCompound, id);
        } else {
            selectOnThinAir(player, world, tagCompound, stack);
        }
        stack.setTag(tagCompound);
    }

    protected void selectOnReceiver(Player player, Level world, CompoundTag tagCompound, int id) {
        Logging.message(player, "Charged porter target is set to " + id + ".");
        tagCompound.putInt("target", id);
    }

    protected void selectOnThinAir(Player player, Level world, CompoundTag tagCompound, ItemStack stack) {
        if (world.isClientSide) {
            Logging.message(player, "Charged porter is cleared.");
        }
        tagCompound.remove("target");
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
    }

    @Override
    public long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate) {
        if (container.getTag() == null) {
            container.setTag(new CompoundTag());
        }
        int energy = container.getTag().getInt("Energy");
        int energyReceived = Math.min(capacity.get() - energy, Math.min(this.maxReceive.get(), EnergyTools.unsignedClampToInt(maxReceive)));

        if (!simulate) {
            energy += energyReceived;
            container.getTag().putInt("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public long extractEnergyL(ItemStack container, long maxExtract, boolean simulate) {
        if (container.getTag() == null || !container.getTag().contains("Energy")) {
            return 0;
        }
        int energy = container.getTag().getInt("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, EnergyTools.unsignedClampToInt(maxExtract)));

        if (!simulate) {
            energy -= energyExtracted;
            container.getTag().putInt("Energy", energy);
        }
        return energyExtracted;
    }

    public int extractEnergyNoMax(ItemStack container, int maxExtract, boolean simulate) {
        if (container.getTag() == null || !container.getTag().contains("Energy")) {
            return 0;
        }
        int energy = container.getTag().getInt("Energy");
        int energyExtracted = Math.min(energy, maxExtract);

        if (!simulate) {
            energy -= energyExtracted;
            container.getTag().putInt("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public long getEnergyStoredL(ItemStack container) {
        if (container.getTag() == null || !container.getTag().contains("Energy")) {
            return 0;
        }
        return container.getTag().getInt("Energy");
    }

    @Override
    public long getMaxEnergyStoredL(ItemStack container) {
        return capacity.get();
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Arrays.asList("Energy");
    }
}
