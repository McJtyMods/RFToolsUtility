package mcjty.rftoolsutility.modules.screen.items;

import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsutility.modules.screen.ScreenSetup;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ScreenBlockItem extends BlockItem implements ITabletSupport {

    public ScreenBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public Item getInstalledTablet() {
        return ScreenSetup.TABLET_SCREEN.get();
    }

    @Override
    public void openGui(@Nonnull PlayerEntity player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("Screen Module");
            }

            @Override
            public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
                return new ScreenTabletContainer(id, player.getPosition(), player);
            }
        });
    }
}
