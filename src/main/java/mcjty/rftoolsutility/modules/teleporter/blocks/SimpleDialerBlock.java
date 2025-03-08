package mcjty.rftoolsutility.modules.teleporter.blocks;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.data.SimpleDialerData;
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
        SimpleDialerData data = stack.get(TeleporterModule.ITEM_SIMPLEDIALER_DATA);
        if (data != null) {
            return data.onceMode();
        }
        return false;
    }

    private static String getTransmitterInfo(ItemStack stack) {
        SimpleDialerData data = stack.get(TeleporterModule.ITEM_SIMPLEDIALER_DATA);
        if (data != null && data.transmitter().pos() != BlockPosTools.INVALID) {
            int transX = data.transmitter().pos().getX();
            int transY = data.transmitter().pos().getY();
            int transZ = data.transmitter().pos().getZ();
            String dim = data.transmitter().dimension().location().toString();
            return transX + "," + transY + "," + transZ + " (dim " + dim + ")";
        }
        return "<unset>";
    }

    private static String getReceiverInfo(ItemStack stack) {
        SimpleDialerData data = stack.get(TeleporterModule.ITEM_SIMPLEDIALER_DATA);
        if (data != null && data.receiver() != -1) {
            return Integer.toString(data.receiver());
        }
        return "<unset>";
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
        if (te instanceof SimpleDialerTileEntity simpleDialer) {
            simpleDialer.update();
        }
    }
}
