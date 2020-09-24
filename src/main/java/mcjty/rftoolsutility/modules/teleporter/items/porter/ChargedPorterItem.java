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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class ChargedPorterItem extends Item implements IEnergyItem, INBTPreservingIngredient, ITooltipSettings {

    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsutility:machines/teleporter");

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
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
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            return tag.contains("target");
        }
        return false;
    }

    private String getTargetString(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
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
        this(TeleportConfiguration.CHARGEDPORTER_MAXENERGY.get());
    }

    protected ChargedPorterItem(int capacity) {
        super(new Properties().maxStackSize(1).defaultMaxDamage(1).group(RFToolsUtility.setup.getTab()));
        this.capacity = capacity;

        maxReceive = TeleportConfiguration.CHARGEDPORTER_RECEIVEPERTICK.get();
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
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemCapabilityProvider(stack, this);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) {
            CompoundNBT tagCompound = stack.getTag();
            if (tagCompound == null) {
                return;
            }
            if (!tagCompound.contains("tpTimer")) {
                return;
            }
            if (!(entityIn instanceof PlayerEntity)) {
                return;
            }
            PlayerEntity player = (PlayerEntity) entityIn;
            int timer = tagCompound.getInt("tpTimer");
            timer--;
            if (timer <= 0) {
                tagCompound.remove("tpTimer");
                TeleportDestinations destinations = TeleportDestinations.get(worldIn);
                int target = tagCompound.getInt("target");
                GlobalCoordinate coordinate = destinations.getCoordinateForId(target);
                if (coordinate == null) {
                    Logging.message(player, TextFormatting.RED + "Something went wrong! The target has disappeared!");
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
        ItemModelsProperties.registerProperty(item, new ResourceLocation(RFToolsUtility.MODID, "charge"), (stack, world, livingEntity) -> {
            CompoundNBT tagCompound = stack.getTag();
            int energy = tagCompound == null ? 0 : tagCompound.getInt("Energy");
            int level = (9 * energy) / item.capacity;
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
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            startTeleport(stack, player, world);
        } else {
            selectReceiver(stack, world, player);
        }
        return super.onItemRightClick(world, player, hand);
    }

    protected void selectReceiver(ItemStack stack, World world, PlayerEntity player) {
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            TileEntity te = world.getTileEntity(pos);
            setTarget(stack, player, world, te);
        } else {
            startTeleport(stack, player, world);
        }
        return ActionResultType.SUCCESS;
    }

    private void startTeleport(ItemStack stack, PlayerEntity player, World world) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null || (!tagCompound.contains("target")) || tagCompound.getInt("target") == -1) {
            if (world.isRemote) {
                Logging.message(player, TextFormatting.RED + "The charged porter has no target.");
            }
            return;
        }

        if (!world.isRemote) {
            if (tagCompound.contains("tpTimer")) {
                Logging.message(player, TextFormatting.RED + "Already teleporting!");
                return;
            }

//            PorterProperties porterProperties = PlayerExtendedProperties.getPorterProperties(player);
//            if (porterProperties != null) {
//                if (porterProperties.isTeleporting()) {
//                    Logging.message(player, TextFormatting.RED + "Already teleporting!");
//                    return;
//                }
//
//            }

            int target = tagCompound.getInt("target");

            TeleportDestinations destinations = TeleportDestinations.get(world);
            GlobalCoordinate coordinate = destinations.getCoordinateForId(target);
            if (coordinate == null) {
                Logging.message(player, TextFormatting.RED + "Something went wrong! The target has disappeared!");
                TeleportationTools.applyEffectForSeverity(player, 3, false);
                return;
            }
            TeleportDestination destination = destinations.getDestination(coordinate);

            if (!TeleportationTools.checkValidTeleport(player, DimensionId.fromWorld(world), destination.getDimension())) {
                return;
            }

            BlockPos playerCoordinate = new BlockPos((int) player.getPosX(), (int) player.getPosY(), (int) player.getPosZ());
            int cost = TeleportationTools.calculateRFCost(world, playerCoordinate, destination);
            cost *= 1.5f;
            long energy = getEnergyStoredL(stack);
            if (cost > energy) {
                Logging.message(player, TextFormatting.RED + "Not enough energy to start the teleportation!");
                return;
            }
            extractEnergyNoMax(stack, cost, false);

            int ticks = TeleportationTools.calculateTime(world, playerCoordinate, destination);
            ticks /= getSpeedBonus();
//            if (porterProperties != null) {
//                porterProperties.startTeleport(target, ticks);
//            }
            tagCompound.putInt("tpTimer", ticks);
            Logging.message(player, TextFormatting.YELLOW + "Start teleportation!");
        }
    }

    private void setTarget(ItemStack stack, PlayerEntity player, World world, TileEntity te) {
        CompoundNBT tagCompound = stack.getTag();

        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
        }
        int id = -1;
        if (te instanceof MatterReceiverTileEntity) {
            MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) te;
            if (!matterReceiverTileEntity.checkAccess(player.getUniqueID())) {
                Logging.message(player, TextFormatting.RED + "You have no access to target this receiver!");
                return;
            }
            id = matterReceiverTileEntity.getId();
        }

        if (id != -1) {
            selectOnReceiver(player, world, tagCompound, id);
        } else {
            selectOnThinAir(player, world, tagCompound, stack);
        }
        stack.setTag(tagCompound);
    }

    protected void selectOnReceiver(PlayerEntity player, World world, CompoundNBT tagCompound, int id) {
        if (world.isRemote) {
            Logging.message(player, "Charged porter target is set to " + id + ".");
        }
        tagCompound.putInt("target", id);
    }

    protected void selectOnThinAir(PlayerEntity player, World world, CompoundNBT tagCompound, ItemStack stack) {
        if (world.isRemote) {
            Logging.message(player, "Charged porter is cleared.");
        }
        tagCompound.remove("target");
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flag);
    }

    @Override
    public long receiveEnergyL(ItemStack container, long maxReceive, boolean simulate) {
        if (container.getTag() == null) {
            container.setTag(new CompoundNBT());
        }
        int energy = container.getTag().getInt("Energy");
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, EnergyTools.unsignedClampToInt(maxReceive)));

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
        return capacity;
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Arrays.asList("Energy");
    }
}
