package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;

public class ScreenContainer extends GenericContainer {

    public static final int SLOT_MODULES = 0;
    public static final int SCREEN_MODULES = 11;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(SCREEN_MODULES)
            .box(generic().in(), SLOT_MODULES, 7, 8, 1, SCREEN_MODULES)
            .playerSlots(85, 142));

    private ScreenContainer(ContainerType type, int id, BlockPos pos, @Nullable GenericTileEntity te, @Nonnull PlayerEntity player) {
        super(type, id, CONTAINER_FACTORY.get(), pos, te, player);
    }

    public static ScreenContainer create(int id, BlockPos pos, @Nullable GenericTileEntity te, @Nonnull PlayerEntity player) {
        return new ScreenContainer(ScreenModule.CONTAINER_SCREEN.get(), id, pos, te, player);
    }

    public static ScreenContainer createRemote(int id, BlockPos pos, @Nullable GenericTileEntity te, @Nonnull PlayerEntity player) {
        return new ScreenContainer(ScreenModule.CONTAINER_SCREEN_REMOTE.get(), id, pos, te, player) {
            @Override
            protected boolean isRemoteContainer() {
                return true;
            }
        };
    }

    public static ScreenContainer createRemoteCreative(int id, BlockPos pos, @Nullable GenericTileEntity te, @Nonnull PlayerEntity player) {
        return new ScreenContainer(ScreenModule.CONTAINER_SCREEN_REMOTE_CREATIVE.get(), id, pos, te, player) {
            @Override
            protected boolean isRemoteContainer() {
                return true;
            }
        };
    }

    protected boolean isRemoteContainer() {
        return false;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        // If we are a remote container our canInteractWith should ignore distance
        if (isRemoteContainer()) {
            return te == null || !te.isRemoved();
        } else {
            return super.stillValid(player);
        }
    }

}
