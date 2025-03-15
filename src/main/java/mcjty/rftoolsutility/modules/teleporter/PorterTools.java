package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsutility.modules.teleporter.data.ChargedPorterData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.modules.teleporter.items.porter.AdvancedChargedPorterItem;
import mcjty.rftoolsutility.modules.teleporter.items.teleportprobe.TeleportProbeItem;
import mcjty.rftoolsutility.modules.teleporter.network.PacketTargetsReady;
import mcjty.rftoolsutility.setup.RFToolsUtilityMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicInteger;

public class PorterTools {

    public static void clearTarget(Player player, int index) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        ChargedPorterData data = heldItem.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data != null) {
            int old = data.getTarget(index);
            data = data.withTarget(index, -1);
            if (data.currentTarget() == old) {
                data = data.withCurrentTarget(-1);
            }
            heldItem.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
        }
    }

    public static void forceTeleport(Player player, ResourceKey<Level> dimension, BlockPos pos) {
        boolean probeInMainHand = !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof TeleportProbeItem;
        boolean probeInOffHand = !player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() instanceof TeleportProbeItem;
        if ((!probeInMainHand) && (!probeInOffHand)) {
            return;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        ResourceKey<Level> currentId = player.getCommandSenderWorld().dimension();
        if (!currentId.equals(dimension)) {
            mcjty.lib.varia.TeleportationTools.teleportToDimension(player, dimension, x + .5, y + 1, z + .5);
        } else {
            player.teleportTo(x+.5, y + 1.5, z+.5);
        }
    }

    public static void cycleDestination(Player player, boolean next) {
        ItemStack stack = player.getMainHandItem();
        cycleDestination(player, next, stack);
    }

    public static void cycleDestination(Player player, boolean next, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof AdvancedChargedPorterItem) {
            ChargedPorterData data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
            if (data == null) {
                return;
            }
            TeleportDestinations destinations = TeleportDestinations.get(player.getCommandSenderWorld());

            int curtarget = data.currentTarget();

            int donext = 0;
            // To wrap around we cycle through the list twice
            for (int i = 0; i < AdvancedChargedPorterItem.MAXTARGETS * 2; i++) {
                int tgt;
                if (next) {
                    tgt = i % AdvancedChargedPorterItem.MAXTARGETS;
                } else {
                    tgt = (AdvancedChargedPorterItem.MAXTARGETS * 2 - i) % AdvancedChargedPorterItem.MAXTARGETS;
                }
                AtomicInteger newTarget = new AtomicInteger(-1);
                donext = checkTarget(player, data, destinations, newTarget, curtarget, donext, tgt);
                if (donext == 2) {
                    data = data.withCurrentTarget(newTarget.get());
                    stack.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
                    break;
                }
            }
        }
    }

    private static int checkTarget(Player playerEntity, ChargedPorterData data, TeleportDestinations destinations, AtomicInteger newTarget, int curtarget, int donext, int tgt) {
        if (data.getTarget(tgt) != -1) {
            int target = data.getTarget(tgt);
            GlobalPos gc = destinations.getCoordinateForId(target);
            if (gc != null) {
                TeleportDestination destination = destinations.getDestination(gc);
                if (destination != null) {
                    if (donext == 1) {
                        String name = destination.getName() + " (dimension " + destination.getDimension().location().getPath() + ")";
                        newTarget.set(target);
                        Component component = ComponentFactory.literal(ChatFormatting.GREEN + "Target: "+
                        ChatFormatting.WHITE + name);
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

    public static void returnDestinationInfo(Player player, int receiverId) {
        Level world = player.getCommandSenderWorld();
        TeleportDestinations destinations = TeleportDestinations.get(world);
        String name = TeleportDestinations.getDestinationName(destinations, receiverId);
        RFToolsUtilityMessages.sendToClient(player, ClientCommandHandler.CMD_RETURN_DESTINATION_INFO,
                TypedMap.builder().put(ClientCommandHandler.PARAM_ID, receiverId).put(ClientCommandHandler.PARAM_NAME, name));
    }

    public static void setTarget(Player player, int target) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        ChargedPorterData data = heldItem.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
        if (data != null) {
            data = data.withCurrentTarget(target);
            heldItem.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA, data);
        }
    }

    public static void returnTargets(Player player) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }

        ChargedPorterData data = heldItem.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);

        int target = -1;
        int[] targets = new int[AdvancedChargedPorterItem.MAXTARGETS];
        String[] names = new String[AdvancedChargedPorterItem.MAXTARGETS];
        TeleportDestinations destinations = TeleportDestinations.get(player.getCommandSenderWorld());

        if (data != null) {
            target = data.currentTarget();
            for (int i = 0 ; i < AdvancedChargedPorterItem.MAXTARGETS ; i++) {
                names[i] = "";
                if (data.getTarget(i) != -1) {
                    targets[i] = data.getTarget(i);
                    GlobalPos gc = destinations.getCoordinateForId(targets[i]);
                    if (gc != null) {
                        TeleportDestination destination = destinations.getDestination(gc);
                        if (destination != null) {
                            names[i] = destination.getName() + " (dimension " + destination.getDimension().location().getPath() + ")";
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

        RFToolsUtilityMessages.sendToPlayer(PacketTargetsReady.create(target, targets, names), player);
    }
}
