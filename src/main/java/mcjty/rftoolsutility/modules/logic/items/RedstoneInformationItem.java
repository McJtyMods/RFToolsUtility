package mcjty.rftoolsutility.modules.logic.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.item.Item.Properties;

public class RedstoneInformationItem extends Item implements ITabletSupport, ITooltipSettings {

    public static final ManualEntry MANUAL = ManualHelper.create("rftoolsutility:logic/redstone_information");

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(), gold(), parameter("channels", stack -> {
                Set<Integer> channels = getChannels(stack);
                List<Integer> list = channels.stream().sorted().collect(Collectors.toList());
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
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable World world, @Nonnull List<ITextComponent> list, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flag);
    }

    @Override
    public Item getInstalledTablet() {
        return LogicBlockModule.TABLET_REDSTONE.get();
    }

    @Override
    public void openGui(@Nonnull PlayerEntity player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
            @Nonnull
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("Redstone Module");
            }

            @Override
            public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
                return new RedstoneInformationContainer(id, player.blockPosition(), player);
            }
        });
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            openGui(player, stack, stack);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
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
            CompoundNBT tag = stack.getOrCreateTag();
            tag.putIntArray("Channels", channels.stream().collect(Collectors.toList()));
            return true;
        }
        return false;
    }

    public static void removeChannel(ItemStack stack, int channel) {
        Set<Integer> channels = getChannels(stack);
        channels.remove(channel);
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putIntArray("Channels", channels.stream().collect(Collectors.toList()));
    }
}
