package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MatterReceiverBlock extends BaseBlock {

    public MatterReceiverBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .tileEntitySupplier(MatterReceiverTileEntity::new));
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable IBlockReader world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            String name = tagCompound.getString("tpName");
            int id = tagCompound.getInt("destinationId");
            list.add(new StringTextComponent(TextFormatting.GREEN + "Name: " + name + (id == -1 ? "" : (", Id: " + id))));
        }
        if (McJtyLib.proxy.isShiftKeyDown()) {
            list.add(new StringTextComponent(TextFormatting.WHITE + "If you place this block anywhere in the world then"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "you can dial to it using a Dialing Device. Before"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "teleporting to this block make sure to give it power!"));
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Infusing bonus: reduced power consumption."));
        } else {
            list.add(new StringTextComponent(TextFormatting.WHITE + RFToolsUtility.SHIFT_MESSAGE));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        World world = context.getWorld();
        if (world.isRemote) {
            return state;
        }
        TeleportDestinations destinations = TeleportDestinations.get(world);

        BlockPos pos = context.getPos();
        GlobalCoordinate gc = new GlobalCoordinate(pos, world.getDimension().getType());

        destinations.getNewId(gc);
        destinations.addDestination(gc);
        destinations.save();

        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // We don't want what BaseBlock does.
        // This is called AFTER onBlockPlaced below. Here we need to fix the destination settings.
        // @todo 1.14 check
//        restoreBlockFromNBT(world, pos, stack);
        if (!world.isRemote) {
            MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) world.getTileEntity(pos);
            matterReceiverTileEntity.getOrCalculateID();
            matterReceiverTileEntity.updateDestination();
        }
        setOwner(world, pos, placer);
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        super.onReplaced(state, world, pos, newstate, isMoving);
        if (world.isRemote) {
            return;
        }
        TeleportDestinations destinations = TeleportDestinations.get(world);
        destinations.removeDestination(pos, world.getDimension().getType());
        destinations.save();
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }
}
