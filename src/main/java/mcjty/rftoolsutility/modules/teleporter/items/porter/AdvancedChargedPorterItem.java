package mcjty.rftoolsutility.modules.teleporter.items.porter;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.various.IItemCycler;
import mcjty.rftoolsutility.modules.teleporter.PorterTools;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.client.GuiAdvancedPorter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

public class AdvancedChargedPorterItem extends ChargedPorterItem implements IItemCycler {
    public static final int MAXTARGETS = 8;

    public AdvancedChargedPorterItem() {
        super(TeleportConfiguration.ADVANCED_CHARGEDPORTER_MAXENERGY);
    }

    @Override
    public void cycle(Player player, ItemStack stack, boolean next) {
        PorterTools.cycleDestination(player, next, stack);
    }

    @Override
    protected int getSpeedBonus() {
        return TeleportConfiguration.advancedSpeedBonus.get();
    }

    @Override
    protected void selectOnReceiver(Player player, Level world, CompoundTag tagCompound, int id) {
        for (int i = 0 ; i < MAXTARGETS ; i++) {
            if (tagCompound.contains("target"+i) && tagCompound.getInt("target"+i) == id) {
                // Id is already there.
                Logging.message(player, ChatFormatting.YELLOW + "Receiver " + id + " was already added to the charged porter.");
                return;
            }
        }

        for (int i = 0 ; i < MAXTARGETS ; i++) {
            if (!tagCompound.contains("target"+i)) {
                tagCompound.putInt("target"+i, id);
                Logging.message(player, "Receiver " + id + " is added to the charged porter.");
                if (!tagCompound.contains("target")) {
                    tagCompound.putInt("target", id);
                }
                return;
            }
        }
        Logging.message(player, ChatFormatting.YELLOW + "Charged porter has no free targets!");
    }

    @Override
    protected void selectReceiver(ItemStack stack, Level world, Player player) {
        if (world.isClientSide) {
            GuiAdvancedPorter.open();
        }
    }

    @Override
    protected void selectOnThinAir(Player player, Level world, CompoundTag tagCompound, ItemStack stack) {
        selectReceiver(stack, world, player);
    }
}
