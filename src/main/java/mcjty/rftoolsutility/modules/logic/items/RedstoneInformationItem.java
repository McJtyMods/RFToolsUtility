package mcjty.rftoolsutility.modules.logic.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.RedstoneInformationData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

public class RedstoneInformationItem extends Item implements ITabletSupport, ITooltipSettings {

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsutility:logic/redstone_information");

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(), gold(), parameter("channels", stack -> {
                Set<Integer> channels = getChannels(stack);
                List<Integer> list = channels.stream().sorted().toList();
                String s = "";
                String prefix = "";
                for (Integer channel : list) {
                    s += prefix + channel;
                    prefix = ", ";
                }
                return s;
            })));

    public RedstoneInformationItem() {
        super(RFToolsUtility.setup.defaultProperties().durability(1));
    }

    @Override
    public ManualEntry getManualEntry() {
        return MANUAL;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
    }

    @Override
    public Item getInstalledTablet() {
        return LogicBlockModule.TABLET_REDSTONE.get();
    }

    @Override
    public void openGui(@Nonnull Player player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        player.openMenu(new MenuProvider() {
            @Nonnull
            @Override
            public Component getDisplayName() {
                return ComponentFactory.literal("Redstone Module");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player player) {
                return new RedstoneInformationContainer(id, player.blockPosition(), player);
            }
        });
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            openGui(player, stack, stack);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }


    public static Set<Integer> getChannels(ItemStack stack) {
        RedstoneInformationData data = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA, RedstoneInformationData.DEFAULT);
        return data.channels();
    }

    public static boolean addChannel(ItemStack stack, int channel) {
        RedstoneInformationData data = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA, RedstoneInformationData.DEFAULT);
        if (!data.hasChannel(channel)) {
            data = data.addChannel(channel);
            stack.set(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA, data);
            return true;
        }
        return false;
    }

    public static void removeChannel(ItemStack stack, int channel) {
        RedstoneInformationData data = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA, RedstoneInformationData.DEFAULT);
        data = data.removeChannel(channel);
        stack.set(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA, data);
    }
}
