package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public class ScreenTabletContainer extends GenericContainer {

	public static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory(0);

	public ScreenTabletContainer(int id, BlockPos pos, ScreenTileEntity te, PlayerEntity player) {
		super(ScreenSetup.CONTAINER_TABLET_SCREEN.get(), id, CONTAINER_FACTORY, pos, te);
	}

	@Override
	public void setupInventories(IItemHandler itemHandler, PlayerInventory inventory) {
	}
}
