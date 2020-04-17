package mcjty.rftoolsutility.modules.logic.items;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsbase.modules.tablet.items.TabletItem;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import mcjty.rftoolsutility.modules.logic.tools.RedstoneChannels;
import mcjty.rftoolsutility.modules.screen.items.RedstoneModuleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedstoneInformationContainer extends GenericContainer {

	public static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory(0);

	private World world;
	private Set<Integer> channels;
	private Map<Integer, Pair<String, Integer>> values = null;

	// Called client-side when data is received from server
	public void sendData(Map<Integer, Pair<String, Integer>> channelData) {
		values = channelData;
	}

	public Map<Integer, Pair<String, Integer>> getChannelData() {
		return values;
	}

	public static ItemStack getRedstoneInformationItem(PlayerEntity player) {
		ItemStack tabletItem = player.getHeldItem(TabletItem.getHand(player));
		return TabletItem.getContainingItem(tabletItem, TabletItem.getCurrentSlot(tabletItem));
	}

	public RedstoneInformationContainer(int id, BlockPos pos, PlayerEntity player) {
		super(LogicBlockSetup.CONTAINER_REDSTONE_INFORMATION.get(), id, CONTAINER_FACTORY, pos, null);
		ItemStack infoItem = getRedstoneInformationItem(player);
		channels = RedstoneInformationItem.getChannels(infoItem);
		world = player.getEntityWorld();
	}

	@Override
	public void setupInventories(IItemHandler itemHandler, PlayerInventory inventory) {
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		boolean dirty = false;
		RedstoneChannels channels = RedstoneChannels.getChannels(world);
		if (values == null) {
			values = new HashMap<>();
			for (Integer channel : this.channels) {
				RedstoneChannels.RedstoneChannel c = channels.getChannel(channel);
				values.put(channel, Pair.of(c.getName(), c.getValue()));
			}
			dirty = true;
		} else {
			for (Integer channel : this.channels) {
				RedstoneChannels.RedstoneChannel c = channels.getChannel(channel);
				if (values.get(channel).getRight() != c.getValue()) {
					values.put(channel, Pair.of(c.getName(), c.getValue()));
					dirty = true;
				}
			}
		}

		if (dirty) {
			for (IContainerListener listener : listeners) {
				if (listener instanceof ServerPlayerEntity) {

				}
			}
		}
	}
}
