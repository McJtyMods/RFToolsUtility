package mcjty.rftoolsutility.modules.teleporter.items.teleportprobe;

import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.modules.teleporter.client.GuiTeleportProbe;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class TeleportProbeItem extends Item implements ITooltipSettings {

    public TeleportProbeItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(1));
    }

//    public void initModel() {
//        ModelLoader.setCustomModelIdentifier(this, 0, new ModelIdentifier(getRegistryName(), "inventory"));
//    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Nonnull
    @Override
    public InteractionResult use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide()) {
            GuiTeleportProbe.open();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ManualEntry getManualEntry() {
        return ManualHelper.create("rftoolsutility:machines/teleporter");
    }

}
