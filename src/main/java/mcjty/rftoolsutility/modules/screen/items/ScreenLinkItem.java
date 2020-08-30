package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.client.GuiTools;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.rftoolsbase.api.various.ITabletSupport;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class ScreenLinkItem extends Item implements ITabletSupport {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("info", this::getInfoString));

    protected String getInfoString(ItemStack stack) {
        return ModuleTools.getTargetString(stack);
    }

    public ScreenLinkItem() {
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
        return ScreenModule.TABLET_SCREEN.get();
    }

    @Override
    public void openGui(@Nonnull PlayerEntity player, @Nonnull ItemStack tabletItem, @Nonnull ItemStack containingItem) {
        BlockPos pos = ModuleTools.getPositionFromModule(containingItem);
        DimensionId dimensionType = ModuleTools.getDimensionFromModule(containingItem);
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


        GuiTools.openRemoteGui(player, dimensionType, pos, te -> new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("Remote Screen");
            }

            @Nullable
            @Override
            public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                ScreenContainer container = ScreenContainer.createRemote(id, pos, (GenericTileEntity) te);
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    container.setupInventories(h, inventory);
                });
                return container;
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

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        PlayerEntity player = context.getPlayer();
        TileEntity te = world.getTileEntity(pos);
        CompoundNBT tagCompound = stack.getOrCreateTag();
        if (te instanceof ScreenTileEntity) {
            tagCompound.putString("monitordim", DimensionId.fromWorld(world).getRegistryName().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            BlockState state = player.getEntityWorld().getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (block != null && !block.isAir(state, world, pos)) {
                name = BlockTools.getReadableName(world, pos);
            }
            tagCompound.putString("monitorname", name);
            if (world.isRemote) {
                Logging.message(player, "Screen link is set to block '" + name + "'");
            }
        } else {
            tagCompound.remove("monitordim");
            tagCompound.remove("monitorx");
            tagCompound.remove("monitory");
            tagCompound.remove("monitorz");
            tagCompound.remove("monitorname");
            if (world.isRemote) {
                Logging.message(player, "Screen link is cleared");
            }
        }
        stack.setTag(tagCompound);
        return ActionResultType.SUCCESS;
    }

}
