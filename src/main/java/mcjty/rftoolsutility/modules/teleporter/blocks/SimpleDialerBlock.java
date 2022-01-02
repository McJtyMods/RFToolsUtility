package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SimpleDialerBlock extends LogicSlabBlock {

    public SimpleDialerBlock() {
        super(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header(),
                        parameter("transmitter", SimpleDialerBlock::getTransmitterInfo),
                        parameter("receiver", SimpleDialerBlock::getReceiverInfo),
                        parameter("once", SimpleDialerBlock::hasOnce, stack -> hasOnce(stack) ? "Once mode enabled" : ""))
                .tileEntitySupplier(SimpleDialerTileEntity::new));
    }

    private static boolean hasOnce(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getBoolean, "once", false);
    }

    private static String getTransmitterInfo(ItemStack stack) {
        if (NBTTools.hasInfoNBT(stack, "transX")) {
            int transX = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "transX", 0);
            int transY = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "transY", 0);
            int transZ = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "transZ", 0);
            String dim = NBTTools.getInfoNBT(stack, CompoundTag::getString, "transZ", Level.OVERWORLD.location().toString());
            return transX + "," + transY + "," + transZ + " (dim " + dim + ")";
        }
        return "<unset>";
    }

    private static String getReceiverInfo(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, (info, s) -> Integer.toString(info.getInt(s)), "receiver", "<unset>");
    }

    @Override
    protected boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        if (!world.isClientSide) {
            SimpleDialerTileEntity simpleDialerTileEntity = (SimpleDialerTileEntity) world.getBlockEntity(pos);
            if (simpleDialerTileEntity != null) {
                boolean onceMode = !simpleDialerTileEntity.isOnceMode();
                simpleDialerTileEntity.setOnceMode(onceMode);
                if (onceMode) {
                    Logging.message(player, "Enabled 'dial once' mode");
                } else {
                    Logging.message(player, "Disabled 'dial once' mode");
                }
            }
        }
        return true;
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof SimpleDialerTileEntity) {
            SimpleDialerTileEntity simpleDialerTileEntity = (SimpleDialerTileEntity) te;
            simpleDialerTileEntity.update();
        }
    }
}
