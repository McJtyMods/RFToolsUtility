package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.modules.teleporter.items.porter.AdvancedChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.teleportprobe.TeleportProbeItem;
import mcjty.rftoolsutility.modules.teleporter.network.PacketTargetsReady;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

public class PorterTools {

    public static void clearTarget(PlayerEntity player, int index) {
        ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        CompoundNBT tagCompound = heldItem.getTag();
        if (tagCompound == null) {
            return;
        }
        if (tagCompound.contains("target"+ index)) {
            int id = tagCompound.getInt("target"+ index);
            if (tagCompound.contains("target") && tagCompound.getInt("target") == id) {
                tagCompound.remove("target");
            }
            tagCompound.remove("target"+ index);
        }
    }

    public static void forceTeleport(PlayerEntity player, DimensionId dimension, BlockPos pos) {
        boolean probeInMainHand = !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof TeleportProbeItem;
        boolean probeInOffHand = !player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() instanceof TeleportProbeItem;
        if ((!probeInMainHand) && (!probeInOffHand)) {
            return;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        DimensionId currentId = DimensionId.fromWorld(player.getCommandSenderWorld());
        if (!currentId.equals(dimension)) {
            mcjty.lib.varia.TeleportationTools.teleportToDimension(player, dimension, x + .5, y + 1, z + .5);
        } else {
            player.teleportTo(x+.5, y + 1.5, z+.5);
        }
    }

    public static void cycleDestination(PlayerEntity player, boolean next) {
        ItemStack stack = player.getMainHandItem();
        cycleDestination(player, next, stack);
    }

    public static void cycleDestination(PlayerEntity player, boolean next, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof AdvancedChargedPorterItem) {
            CompoundNBT tagCompound = stack.getTag();
            if (tagCompound == null) {
                return;
            }
            TeleportDestinations destinations = TeleportDestinations.get(player.getCommandSenderWorld());

            int curtarget = tagCompound.getInt("target");

            int donext = 0;
            // To wrap around we cycle through the list twice
            for (int i = 0; i < AdvancedChargedPorterItem.MAXTARGETS * 2; i++) {
                int tgt;
                if (next) {
                    tgt = i % AdvancedChargedPorterItem.MAXTARGETS;
                } else {
                    tgt = (AdvancedChargedPorterItem.MAXTARGETS * 2 - i) % AdvancedChargedPorterItem.MAXTARGETS;
                }
                donext = checkTarget(player, tagCompound, destinations, curtarget, donext, tgt);
                if (donext == 2) {
                    break;
                }
            }
        }
    }

    private static int checkTarget(PlayerEntity playerEntity, CompoundNBT tagCompound, TeleportDestinations destinations, int curtarget, int donext, int tgt) {
        if (tagCompound.contains("target" + tgt)) {
            int target = tagCompound.getInt("target" + tgt);
            GlobalCoordinate gc = destinations.getCoordinateForId(target);
            if (gc != null) {
                TeleportDestination destination = destinations.getDestination(gc);
                if (destination != null) {
                    if (donext == 1) {
                        String name = destination.getName() + " (dimension " + destination.getDimension().getName() + ")";
                        tagCompound.putInt("target", target);
                        ITextComponent component = new StringTextComponent(TextFormatting.GREEN + "Target: "+
                        TextFormatting.WHITE + name);
                        if (playerEntity != null) {
                            playerEntity.displayClientMessage(component, false);
                        }
                        donext = 2;
                    } else if (target == curtarget) {
                        donext = 1;
                    }
                }
            }
        }
        return donext;
    }

    public static void returnDestinationInfo(PlayerEntity player, int receiverId) {
        World world = player.getCommandSenderWorld();
        TeleportDestinations destinations = TeleportDestinations.get(world);
        String name = TeleportDestinations.getDestinationName(destinations, receiverId);
        RFToolsUtilityMessages.sendToClient(player, ClientCommandHandler.CMD_RETURN_DESTINATION_INFO,
                TypedMap.builder().put(ClientCommandHandler.PARAM_ID, receiverId).put(ClientCommandHandler.PARAM_NAME, name));
    }

    public static void setTarget(PlayerEntity player, int target) {
        ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        CompoundNBT tagCompound = heldItem.getTag();
        if (tagCompound == null) {
            return;
        }
        tagCompound.putInt("target", target);
    }

    public static void returnTargets(PlayerEntity player) {
        ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        CompoundNBT tagCompound = heldItem.getTag();

        int target = -1;
        int targets[] = new int[AdvancedChargedPorterItem.MAXTARGETS];
        String names[] = new String[AdvancedChargedPorterItem.MAXTARGETS];
        TeleportDestinations destinations = TeleportDestinations.get(player.getCommandSenderWorld());

        if (tagCompound != null) {
            if (tagCompound.contains("target")) {
                target = tagCompound.getInt("target");
            } else {
                target = -1;
            }
            for (int i = 0 ; i < AdvancedChargedPorterItem.MAXTARGETS ; i++) {
                names[i] = "";
                if (tagCompound.contains("target" + i)) {
                    targets[i] = tagCompound.getInt("target" + i);
                    GlobalCoordinate gc = destinations.getCoordinateForId(targets[i]);
                    if (gc != null) {
                        TeleportDestination destination = destinations.getDestination(gc);
                        if (destination != null) {
                            names[i] = destination.getName() + " (dimension " + destination.getDimension().getName() + ")";
                        }
                    }
                } else {
                    targets[i] = -1;
                }
            }
        } else {
            for (int i = 0 ; i < AdvancedChargedPorterItem.MAXTARGETS ; i++) {
                targets[i] = -1;
                names[i] = "";
            }
        }

        PacketTargetsReady msg = new PacketTargetsReady(target, targets, names);
        RFToolsUtilityMessages.INSTANCE.sendTo(msg, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
