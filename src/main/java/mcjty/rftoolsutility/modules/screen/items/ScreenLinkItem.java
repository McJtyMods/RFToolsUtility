package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.client.GuiTools;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item.Properties;

public class ScreenLinkItem extends Item implements ITabletSupport {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("info", this::getInfoString));

    protected String getInfoString(ItemStack stack) {
        return ModuleTools.getTargetString(stack);
    }

    public ScreenLinkItem() {
        super(new Properties()
                .defaultDurability(1)
                .tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flag);
    }

    @Override
    public Item getInstalledTablet() {
        return ScreenModule.TABLET_SCREEN.get();
    }

    @Override
    public void openGui(@Nonnull Player player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        BlockPos pos = ModuleTools.getPositionFromModule(containingItem);
        ResourceKey<Level> dimensionType = ModuleTools.getDimensionFromModule(containingItem);
//        World world = player.getEntityWorld();
//        if (dimensionType != null) {
//            world = WorldTools.getWorld(world, dimensionType);
//        }
//        if (!WorldTools.isLoaded(world, pos)) {
//            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + "Screen is not loaded!"), false);
//            return;
//        }
//        TileEntity te = world.getTileEntity(pos);
//        if (!(te instanceof ScreenTileEntity)) {
//            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + "Screen is missing!"), false);
//            return;
//        }
//        NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
//            @Override
//            public ITextComponent getDisplayName() {
//                return new StringTextComponent("Screen Module");
//            }
//
//            @Override
//            public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
//                return new ScreenTabletContainer(id, pos, (ScreenTileEntity) te, player);
//            }
//        }, pos);


        GuiTools.openRemoteGui(player, dimensionType, pos, te -> new MenuProvider() {
            @Nonnull
            @Override
            public Component getDisplayName() {
                return new TextComponent("Remote Screen");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) {
                boolean creative = false;
                if (te instanceof ScreenTileEntity) {
                    creative = ((ScreenTileEntity) te).isCreative();
                }
                ScreenContainer container = creative ?
                        ScreenContainer.createRemoteCreative(id, pos, (GenericTileEntity) te) :
                        ScreenContainer.createRemote(id, pos, (GenericTileEntity) te);
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    container.setupInventories(h, inventory);
                });
                return container;
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

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Player player = context.getPlayer();
        BlockEntity te = world.getBlockEntity(pos);
        CompoundTag tagCompound = stack.getOrCreateTag();
        if (te instanceof ScreenTileEntity) {
            tagCompound.putString("monitordim", world.dimension().location().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            BlockState state = player.getCommandSenderWorld().getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
                name = Tools.getReadableName(world, pos);
            }
            tagCompound.putString("monitorname", name);
            if (world.isClientSide) {
                Logging.message(player, "Screen link is set to block '" + name + "'");
            }
        } else {
            tagCompound.remove("monitordim");
            tagCompound.remove("monitorx");
            tagCompound.remove("monitory");
            tagCompound.remove("monitorz");
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Screen link is cleared");
            }
        }
        stack.setTag(tagCompound);
        return InteractionResult.SUCCESS;
    }

}
