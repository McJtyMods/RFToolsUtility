package mcjty.rftoolsutility.modules.teleporter.items.teleportprobe;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.client.GuiTeleportProbe;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class TeleportProbeItem extends Item {

    public TeleportProbeItem() {
        super(RFToolsUtility.setup.defaultProperties()
                .stacksTo(1)
                .defaultDurability(1));
    }

//    public void initModel() {
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
//    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            GuiTeleportProbe.open();
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}