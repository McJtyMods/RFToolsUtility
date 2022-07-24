package mcjty.rftoolsutility.modules.logic.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mcjty.lib.builder.TooltipBuilder.*;

public class RedstoneInformationItem extends Item implements ITabletSupport, ITooltipSettings {

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsutility:logic/redstone_information");

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
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
            }));

    public RedstoneInformationItem() {
        super(new Properties()
                .defaultDurability(1)
                .tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    public ManualEntry getManualEntry() {
        return MANUAL;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
    }

    @Override
    public Item getInstalledTablet() {
        return LogicBlockModule.TABLET_REDSTONE.get();
    }

    @Override
    public void openGui(@Nonnull Player player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
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
        return NBTTools.getTag(stack).map(tag ->
                IntStream.of(tag.getIntArray("Channels")).boxed().collect(Collectors.toSet())).orElse(Collections.emptySet());
    }

    public static boolean addChannel(ItemStack stack, int channel) {
        Set<Integer> channels = getChannels(stack);
        if (!channels.contains(channel)) {
            channels = new HashSet<>(channels);
            channels.add(channel);
            CompoundTag tag = stack.getOrCreateTag();
            tag.putIntArray("Channels", new ArrayList<>(channels));
            return true;
        }
        return false;
    }

    public static void removeChannel(ItemStack stack, int channel) {
        Set<Integer> channels = getChannels(stack);
        channels.remove(channel);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putIntArray("Channels", new ArrayList<>(channels));
    }
}
