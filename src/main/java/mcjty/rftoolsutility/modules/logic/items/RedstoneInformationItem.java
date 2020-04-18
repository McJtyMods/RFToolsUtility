package mcjty.rftoolsutility.modules.logic.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
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

public class RedstoneInformationItem extends Item implements ITabletSupport {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
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
                .defaultMaxDamage(1)
                .group(RFToolsUtility.setup.getTab()));
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flag);
    }

    @Override
    public Item getInstalledTablet() {
        return LogicBlockSetup.TABLET_REDSTONE.get();
    }

    @Override
    public void openGui(@Nonnull PlayerEntity player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("Redstone Module");
            }

            @Override
            public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
                return new RedstoneInformationContainer(id, player.getPosition(), player);
            }
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
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
