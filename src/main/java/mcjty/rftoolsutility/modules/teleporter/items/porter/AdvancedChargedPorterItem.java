package mcjty.rftoolsutility.modules.teleporter.items.porter;

import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.various.IItemCycler;
import mcjty.rftoolsutility.modules.teleporter.PorterTools;
import mcjty.rftoolsutility.modules.teleporter.TeleportConfiguration;
import mcjty.rftoolsutility.modules.teleporter.client.GuiAdvancedPorter;
import mcjty.rftoolsutility.modules.teleporter.data.ChargedPorterData;
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
    protected ChargedPorterData selectOnReceiver(Player player, Level world, ChargedPorterData data, int id) {
        for (int i = 0 ; i < MAXTARGETS ; i++) {
            if (data.getTarget(i) == id) {
                // Id is already there.
                Logging.message(player, ChatFormatting.YELLOW + "Receiver " + id + " was already added to the charged porter.");
                return data;
            }
        }

        for (int i = 0 ; i < MAXTARGETS ; i++) {
            if (data.getTarget(i) == -1) {
                data = data.withTarget(i, id);
                if (data.currentTarget() == -1) {
                    data = data.withCurrentTarget(id);
                }
                Logging.message(player, "Receiver " + id + " is added to the charged porter.");
                return data;
            }
        }
        Logging.message(player, ChatFormatting.YELLOW + "Charged porter has no free targets!");
        return data;
    }

    @Override
    protected void selectReceiver(ItemStack stack, Level world, Player player) {
        if (world.isClientSide) {
            GuiAdvancedPorter.open();
        }
    }

    @Override
    protected ChargedPorterData selectOnThinAir(Player player, Level world, ChargedPorterData data, ItemStack stack) {
        selectReceiver(stack, world, player);
        return data;
    }
}
