package mcjty.rftoolsutility.modules.logic.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Lazy;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.RedstoneInformationData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class RedstoneInformationItem extends Item implements ITabletSupport, ITooltipSettings {

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsbase:logic/redstone_information");

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

    public RedstoneInformationItem(Properties properties) {
        super(properties.durability(1));
    }

    @Override
    public ManualEntry getManualEntry() {
        return MANUAL;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable TooltipContext context, TooltipDisplay display, Consumer<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, display, list, flag);
        List<Component> lines = new ArrayList<>();
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, lines, flag);
        lines.forEach(list);
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
    public InteractionResult use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            openGui(player, stack, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }


    public static Set<Integer> getChannels(ItemStack stack) {
        RedstoneInformationData data = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA.get(), RedstoneInformationData.DEFAULT);
        return data.channels();
    }

    public static boolean addChannel(ItemStack stack, int channel) {
        RedstoneInformationData data = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA.get(), RedstoneInformationData.DEFAULT);
        if (!data.hasChannel(channel)) {
            data = data.addChannel(channel);
            stack.set(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA.get(), data);
            return true;
        }
        return false;
    }

    public static void removeChannel(ItemStack stack, int channel) {
        RedstoneInformationData data = stack.getOrDefault(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA.get(), RedstoneInformationData.DEFAULT);
        data = data.removeChannel(channel);
        stack.set(LogicBlockModule.ITEM_REDSTONE_INFORMATION_DATA.get(), data);
    }
}
